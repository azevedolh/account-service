package com.desafio.accountservice.util;

import com.desafio.accountservice.dto.PageableResponseDTO;
import com.desafio.accountservice.model.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestUtils {
    public static PageableResponseDTO generatePageable() {
        return PageableResponseDTO.builder()
                ._limit(10)
                ._offset(0L)
                ._pageNumber(1)
                ._pageElements(1)
                ._totalPages(1)
                ._totalElements(1L)
                ._moreElements(false)
                .build();
    };

    public static List<Account> generateListOfAccounts() {
        List<Account> accountList = new ArrayList<>();

        Account account1 = Account.builder()
                .id(UUID.randomUUID())
                .agency("1234")
                .balance(new BigDecimal(10))
                .isActive(Boolean.TRUE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .customerId(UUID.randomUUID())
                .build();

        Account account2 = Account.builder()
                .id(UUID.randomUUID())
                .agency("1235")
                .balance(new BigDecimal(10))
                .isActive(Boolean.TRUE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .customerId(UUID.randomUUID())
                .build();

        Account account3 = Account.builder()
                .id(UUID.randomUUID())
                .agency("1236")
                .balance(new BigDecimal(10))
                .isActive(Boolean.TRUE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .customerId(UUID.randomUUID())
                .build();

        accountList.add(account1);
        accountList.add(account2);
        accountList.add(account3);
        return accountList;
    }
}
