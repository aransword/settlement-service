package dev.fisa.domain.service;

import dev.fisa.domain.dto.AdjustRequest;
import dev.fisa.domain.dto.SettlementResponse;
import dev.fisa.domain.dto.TransactionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SettlementService {

    private final BankClientService bankClientService;
    private final BankAccountClientService bankAccountClientService;

    public SettlementService(BankClientService bankClientService, BankAccountClientService bankAccountClientService) {
        this.bankClientService = bankClientService;
        this.bankAccountClientService = bankAccountClientService;
    }

    public SettlementResponse executeSettlement(LocalDate date) {
        log.info("[정산 시작] 대상 일자: {}", date);

        try {
            // 1. bank-service에서 영수증(거래 내역) 수집
            List<TransactionDto> transactions = bankClientService.getTransactionsByDate(date);
            log.info("수집된 거래 내역: {} 건", transactions.size());

            if (transactions.isEmpty()) {
                return new SettlementResponse(date, 0, BigDecimal.ZERO, "SUCCESS_NO_DATA");
            }

            int totalCount = transactions.size();
            BigDecimal totalAmount = transactions.stream()
                    .map(TransactionDto::amount) // record의 getter
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 2. 은행별 그룹화 및 차액(Netting) 계산
            Map<String, List<TransactionDto>> groupedByBank = transactions.stream()
                    .collect(Collectors.groupingBy(TransactionDto::bankCode));

            List<AdjustRequest> adjustRequests = new ArrayList<>();
            BigDecimal checkSum = BigDecimal.ZERO;

            for (Map.Entry<String, List<TransactionDto>> entry : groupedByBank.entrySet()) {
                String bankCode = entry.getKey();
                BigDecimal totalDebit = BigDecimal.ZERO;  // 줄 돈
                BigDecimal totalCredit = BigDecimal.ZERO; // 받을 돈

                for (TransactionDto tx : entry.getValue()) {
                    if ("WITHDRAWAL".equals(tx.transactionType())) {
                        totalDebit = totalDebit.add(tx.amount());
                    } else if ("DEPOSIT".equals(tx.transactionType())) {
                        totalCredit = totalCredit.add(tx.amount());
                    }
                }

                BigDecimal netAmount = totalCredit.subtract(totalDebit);
                checkSum = checkSum.add(netAmount);

                adjustRequests.add(new AdjustRequest(bankCode, netAmount));
            }

            // 3. 정산 무결성 검증 (모든 차액의 합은 0이어야 함)
            if (checkSum.compareTo(BigDecimal.ZERO) != 0) {
                log.error("정산 불일치 발생! 차액 합계가 0이 아님: {}", checkSum);
                return new SettlementResponse(date, totalCount, totalAmount, "FAILED_CHECKSUM_ERROR");
            }

            // 4. bank-account-service로 결과 전송 (한은 당좌예금 업데이트 요청)
            log.info("bank-account-service로 정산 결과 전달 시작");
            bankAccountClientService.sendAdjustments(adjustRequests);
            log.info("[정산 완료] 정상적으로 처리되었습니다.");

            return new SettlementResponse(date, totalCount, totalAmount, "SUCCESS");

        } catch (Exception e) {
            log.error("[정산 실패] 시스템 오류 발생", e);
            return new SettlementResponse(date, 0, BigDecimal.ZERO, "FAILED_SYSTEM_ERROR");
        }
    }
}