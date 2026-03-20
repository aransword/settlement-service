package dev.fisa.repository;

import dev.fisa.entity.BokAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BokAccountRepository extends JpaRepository<BokAccount, Long> {
    Optional<BokAccount> findByBankCode(String bankCode);
}