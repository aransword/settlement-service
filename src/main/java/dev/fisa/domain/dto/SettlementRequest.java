package dev.fisa.domain.dto;

import java.time.LocalDate;

public record SettlementRequest(
        LocalDate settlementDate
) {}