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
public class SettlementDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "settlement_id", referencedColumnName = "settlementId")
    private Settlement settlement;

    private String bankCode;
    private BigDecimal totalDebit;  // 출금 합계
    private BigDecimal totalCredit; // 입금 합계
    private BigDecimal netAmount;   // 순수취액 (Credit - Debit)

    private boolean settled;        // 한은 결제 완료 여부
}