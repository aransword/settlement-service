package dev.fisa.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SettlementResponse(
        LocalDate settlementDate,
        int totalTransactionCount,
        BigDecimal totalTransactionAmount,
        String status // "SUCCESS", "FAILED" 등
) {}