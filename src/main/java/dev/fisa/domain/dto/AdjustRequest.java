package dev.fisa.domain.dto;

import java.math.BigDecimal;

public record AdjustRequest(
        String bankCode,
        BigDecimal netAmount // +면 입금, -면 출금
) {}