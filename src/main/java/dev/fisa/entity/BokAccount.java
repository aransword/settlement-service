package dev.fisa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BokAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String bankCode;

    private String bankName;
    private BigDecimal balance;

    public void adjustBalance(BigDecimal netAmount) {
        this.balance = this.balance.add(netAmount);
    }
}