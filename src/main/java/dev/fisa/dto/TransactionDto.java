package dev.fisa.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class TransactionDto {
    private String accountNumber;
    private String bankCode;
    private String transactionType; // "WITHDRAWAL" or "DEPOSIT"
    private BigDecimal amount;
}