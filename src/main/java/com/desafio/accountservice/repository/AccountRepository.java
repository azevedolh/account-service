package com.desafio.accountservice.repository;

import com.desafio.accountservice.model.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    Page<Account> findAllByCustomerIdAndIsActive(Pageable pageable, UUID customerId, Boolean isActive);

    Optional<Account> findByCustomerIdAndIsActiveAndAgency(UUID customerId, Boolean isActive, String agency);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT MAX(a.account) FROM Account a GROUP BY a.account ORDER BY a.account DESC")
    Optional<Long> findLastAccountAndLock();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a from Account a WHERE a.account = :account AND a.agency = :agency AND a.isActive = :isActive")
    Optional<Account> findAccountsByAccountAndAgencyAndLock(
            @Param("account") Long account,
            @Param("agency") String agency,
            @Param("isActive") Boolean isActive
    );
}
