package dev.fisa.domain.controller;

import dev.fisa.domain.dto.SettlementRequest;
import dev.fisa.domain.dto.SettlementResponse;
import dev.fisa.domain.service.SettlementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settlement")
public class SettlementController {

    private final SettlementService settlementService;

    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @PostMapping("/execute")
    public ResponseEntity<SettlementResponse> executeSettlement(@RequestBody SettlementRequest request) {
        SettlementResponse response = settlementService.executeSettlement(request.settlementDate());

        if (response.status().startsWith("FAILED")) {
            return ResponseEntity.internalServerError().body(response);
        }
        return ResponseEntity.ok(response);
    }
}