package dev.fisa.controller;

import dev.fisa.dto.SettlementRequest;
import dev.fisa.dto.SettlementResponse;
import dev.fisa.entity.BokAccount;
import dev.fisa.entity.Settlement;
import dev.fisa.repository.BokAccountRepository;
import dev.fisa.repository.SettlementRepository;
import dev.fisa.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/settlement")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;
    private final SettlementRepository settlementRepository;
    private final BokAccountRepository bokAccountRepository;

    @PostMapping("/execute")
    public ResponseEntity<SettlementResponse> executeSettlement(
            @RequestBody SettlementRequest request) { // DTO로 변경
        // Request DTO에서 날짜를 꺼내 서비스로 전달
        SettlementResponse response = settlementService.executeSettlement(request.getSettlementDate());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{settlementId}")
    public ResponseEntity<Settlement> getSettlement(@PathVariable String settlementId) {
        return settlementRepository.findBySettlementId(settlementId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/bok-accounts")
    public ResponseEntity<List<BokAccount>> getAllBokAccounts() {
        return ResponseEntity.ok(bokAccountRepository.findAll());
    }
}