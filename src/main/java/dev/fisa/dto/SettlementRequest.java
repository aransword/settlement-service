package dev.fisa.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class SettlementRequest {
    // JSON 필드명: "settlementDate": "2026-03-20"
    private LocalDate settlementDate;
}