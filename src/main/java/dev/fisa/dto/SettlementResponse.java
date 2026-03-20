package dev.fisa.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class SettlementResponse {
    private String settlementId;
    private LocalDate settlementDate;
    private int totalTransactionCount;
    private BigDecimal totalTransactionAmount;
    private String status;
}