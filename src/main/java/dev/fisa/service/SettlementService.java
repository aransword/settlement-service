package dev.fisa.service;

import dev.fisa.client.BankClientService;
import dev.fisa.dto.SettlementResponse;
import dev.fisa.dto.TransactionDto;
import dev.fisa.entity.Settlement;
import dev.fisa.entity.BokAccount;
import dev.fisa.entity.SettlementDetail;
import dev.fisa.repository.BokAccountRepository;
import dev.fisa.repository.SettlementDetailRepository;
import dev.fisa.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final SettlementDetailRepository settlementDetailRepository;
    private final BokAccountRepository bokAccountRepository;
    private final BankClientService bankClientService;

    @Transactional
    public SettlementResponse executeSettlement(LocalDate date) {
        // 1. 이미 정산되었는지 확인
        if (settlementRepository.findBySettlementDate(date).isPresent()) {
            throw new IllegalStateException("해당 날짜의 정산이 이미 존재합니다.");
        }

        String settlementId = "SETTLE-" + date.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-001";

        // 2. Settlement 생성 (PENDING)
        Settlement settlement = Settlement.builder()
                .settlementId(settlementId)
                .settlementDate(date)
                .status(SettlementStatus.PENDING)
                .build();
        settlementRepository.save(settlement);

        try {
            // 3. bank-service에서 거래 내역 조회
            List<TransactionDto> transactions = bankClientService.getTransactionsByDate(date);

            settlement.setStatus(SettlementStatus.PROCESSING);

            int totalCount = transactions.size();
            BigDecimal totalAmount = transactions.stream()
                    .map(TransactionDto::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 4. 은행 코드별로 거래 내역 그룹화 (Netting 계산)
            Map<String, List<TransactionDto>> groupedByBank = transactions.stream()
                    .collect(Collectors.groupingBy(TransactionDto::getBankCode));

            for (Map.Entry<String, List<TransactionDto>> entry : groupedByBank.entrySet()) {
                String bankCode = entry.getKey();
                List<TransactionDto> bankTxs = entry.getValue();

                BigDecimal totalDebit = BigDecimal.ZERO;  // 출금 (지급해야 할 돈)
                BigDecimal totalCredit = BigDecimal.ZERO; // 입금 (받아야 할 돈)

                for (TransactionDto tx : bankTxs) {
                    if ("WITHDRAWAL".equals(tx.getTransactionType())) {
                        totalDebit = totalDebit.add(tx.getAmount());
                    } else if ("DEPOSIT".equals(tx.getTransactionType())) {
                        totalCredit = totalCredit.add(tx.getAmount());
                    }
                }

                BigDecimal netAmount = totalCredit.subtract(totalDebit);

                // 5. 한국은행 최종 결제 (당좌예금 가산/차감)
                BokAccount bokAccount = bokAccountRepository.findByBankCode(bankCode)
                        .orElseThrow(() -> new RuntimeException("한국은행 계좌를 찾을 수 없습니다. BankCode: " + bankCode));

                bokAccount.adjustBalance(netAmount);
                bokAccountRepository.save(bokAccount);

                // 정산 상세 내역 저장
                SettlementDetail detail = SettlementDetail.builder()
                        .settlement(settlement)
                        .bankCode(bankCode)
                        .totalDebit(totalDebit)
                        .totalCredit(totalCredit)
                        .netAmount(netAmount)
                        .settled(true)
                        .build();
                settlementDetailRepository.save(detail);
            }

            // 6. 상태 완료로 업데이트
            settlement.setTotalTransactionCount(totalCount);
            settlement.setTotalTransactionAmount(totalAmount);
            settlement.setStatus(SettlementStatus.COMPLETED);
            settlement.setCompletedAt(LocalDateTime.now());

        } catch (Exception e) {
            log.error("정산 중 오류 발생: ", e);
            settlement.setStatus(SettlementStatus.FAILED);
            throw new RuntimeException("정산 처리 중 오류가 발생했습니다.", e);
        }

        // 7. 결과 반환
        return SettlementResponse.builder()
                .settlementId(settlement.getSettlementId())
                .settlementDate(settlement.getSettlementDate())
                .totalTransactionCount(settlement.getTotalTransactionCount())
                .totalTransactionAmount(settlement.getTotalTransactionAmount())
                .status(settlement.getStatus().name())
                .build();
    }
}