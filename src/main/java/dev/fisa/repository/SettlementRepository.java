package dev.fisa.repository;

import dev.fisa.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    Optional<Settlement> findBySettlementId(String settlementId);
    Optional<Settlement> findBySettlementDate(LocalDate date);
}