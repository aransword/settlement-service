package dev.fisa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Settlement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String settlementId;

    @Column(nullable = false)
    private LocalDate settlementDate;

    private int totalTransactionCount;
    private BigDecimal totalTransactionAmount;

    @Enumerated(EnumType.STRING)
    private SettlementStatus status; // PENDING, PROCESSING, COMPLETED, FAILED

    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}