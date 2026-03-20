package dev.fisa.repository;

import dev.fisa.entity.SettlementDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SettlementDetailRepository extends JpaRepository<SettlementDetail, Long> {
    List<SettlementDetail> findBySettlement_SettlementId(String settlementId);
}