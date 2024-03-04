package com.desafio.accountservice.repository;

import com.desafio.accountservice.model.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    Page<Account> findAllByCustomerIdAndIsActive(Pageable pageable, UUID customerId, Boolean isActive);

    Optional<Account> findByCustomerIdAndIsActiveAndAgency(UUID customerId, Boolean isActive, String agency);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT MAX(b.account) FROM Account b GROUP BY b.account ORDER BY b.account DESC")
    //Optional<Account> findTopByOrderByAccountDescAndLock();
    Optional<Long> findLastAccountAndLock();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Account> findByAccountAndAgencyAndIsActive(Long account, String agency, Boolean isActive);
}
