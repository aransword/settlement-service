package dev.fisa.domain.dto;

import java.math.BigDecimal;

public record TransactionDto(
        String accountNumber,
        String bankCode,
        String transactionType, // "WITHDRAWAL" or "DEPOSIT"
        BigDecimal amount
) {}