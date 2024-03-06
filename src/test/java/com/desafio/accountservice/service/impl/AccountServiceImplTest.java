package com.desafio.accountservice.service.impl;

import com.desafio.accountservice.dto.AccountResponseDTO;
import com.desafio.accountservice.dto.CreateAccountRequestDTO;
import com.desafio.accountservice.dto.PageResponseDTO;
import com.desafio.accountservice.dto.UpdateBalanceRequestDTO;
import com.desafio.accountservice.exception.CustomBusinessException;
import com.desafio.accountservice.mapper.AccountRequestMapperImpl;
import com.desafio.accountservice.mapper.AccountResponseMapperImpl;
import com.desafio.accountservice.mapper.impl.PageableMapperImpl;
import com.desafio.accountservice.model.Account;
import com.desafio.accountservice.repository.AccountRepository;
import com.desafio.accountservice.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Captor
    private ArgumentCaptor<Account> accountCaptor;

    @Mock
    private AccountRepository repository;

    @Spy
    private AccountResponseMapperImpl accountResponseMapper;

    @Spy
    private AccountRequestMapperImpl accountRequestMapper;

    @Spy
    private PageableMapperImpl pageableMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void testShouldReturnAllAccountsOfCustomerWhenInvoked() {
        PageRequest pageRequest = PageRequest.of(1, 10);
        Page<Account> expectedReturn = generatePage(pageRequest);
        when(repository.findAllByCustomerIdAndIsActive(any(), any(), any())).thenReturn(expectedReturn);

        PageResponseDTO<AccountResponseDTO> accounts = accountService.getAccounts(UUID.randomUUID(), 1, 10, null);

        verify(repository).findAllByCustomerIdAndIsActive(any(), any(), any());

        assertEquals(expectedReturn.getContent().size(),
                accounts.get_content().size(),
                "Should be equal");

        assertEquals(expectedReturn.getContent().get(0).getId(),
                accounts.get_content().get(0).getId(),
                "Should be equal");

        assertEquals(expectedReturn.getContent().get(0).getBalance(),
                accounts.get_content().get(0).getBalance(),
                "Should be equal");

        assertEquals(expectedReturn.getContent().get(0).getAgency(),
                accounts.get_content().get(0).getAgency(),
                "Should be equal");

        assertEquals(expectedReturn.getContent().get(0).getIsActive(),
                accounts.get_content().get(0).getIsActive(),
                "Should be equal");

        assertEquals(expectedReturn.getContent().get(0).getCreatedAt(),
                accounts.get_content().get(0).getCreatedAt(),
                "Should be equal");

        assertEquals(expectedReturn.getContent().get(0).getUpdatedAt(),
                accounts.get_content().get(0).getUpdatedAt(),
                "Should be equal");
    }

    @Test
    void testShouldReturnNullContentWhenPageObjectNull() {
        PageResponseDTO<AccountResponseDTO> accounts = accountService.getAccounts(UUID.randomUUID(), 1, 10, null);

        verify(repository).findAllByCustomerIdAndIsActive(any(), any(), any());

        assertNull(accounts.get_content(), "Should be null");
    }

    private Page<Account> generatePage(PageRequest pageRequest) {

        List<Account> accountList = TestUtils.generateListOfAccounts();

        return new PageImpl<>(accountList, pageRequest, accountList.size());
    }

    @Test
    void testShouldCreateAccountWhenMethodIsInvoked() {
        Account expected = Account.builder()
                .id(UUID.fromString("8cdb9a41-69d8-49a9-8f63-82134b355b6e"))
                .build();

        CreateAccountRequestDTO requestDTO = CreateAccountRequestDTO.builder()
                .agency("1234")
                .customerId(UUID.randomUUID())
                .build();

        when(repository.save(any())).thenReturn(expected);
        AccountResponseDTO response = accountService.create(requestDTO);

        verify(repository).save(any());

        assertEquals(expected.getId(), response.getId(), "Should be equal");
    }

    @Test
    void testShouldUpdateBalanceAddingAmountWhenOperationIsCredit() {
        Account account = Account.builder()
                .id(UUID.randomUUID())
                .agency("1234")
                .balance(new BigDecimal(100))
                .customerId(UUID.randomUUID())
                .isActive(Boolean.TRUE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        UpdateBalanceRequestDTO updateBalanceRequestDTO = UpdateBalanceRequestDTO.builder()
                .account(1L)
                .agency("1234")
                .amount(new BigDecimal(10))
                .operation("CREDITO")
                .build();

        when(repository.findAccountsByAccountAndAgencyAndLock(anyLong(), anyString(), anyBoolean())).thenReturn(Optional.of(account));
        accountService.updateBalance(updateBalanceRequestDTO);

        verify(repository).findAccountsByAccountAndAgencyAndLock(anyLong(), anyString(), anyBoolean());
        verify(repository).save(accountCaptor.capture());
        assertEquals(accountCaptor.getValue().getBalance(), new BigDecimal(110), "should be equal");
    }

    @Test
    void testShouldUpdateBalanceSubtractingAmountWhenOperationIsDebit() {
        Account account = Account.builder()
                .id(UUID.randomUUID())
                .agency("1234")
                .balance(new BigDecimal(100))
                .customerId(UUID.randomUUID())
                .isActive(Boolean.TRUE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        UpdateBalanceRequestDTO updateBalanceRequestDTO = UpdateBalanceRequestDTO.builder()
                .account(1L)
                .agency("1234")
                .amount(new BigDecimal(10))
                .operation("DEBITO")
                .build();

        when(repository.findAccountsByAccountAndAgencyAndLock(anyLong(), anyString(), anyBoolean())).thenReturn(Optional.of(account));
        accountService.updateBalance(updateBalanceRequestDTO);

        verify(repository).findAccountsByAccountAndAgencyAndLock(anyLong(), anyString(), anyBoolean());
        verify(repository).save(accountCaptor.capture());
        assertEquals(accountCaptor.getValue().getBalance(), new BigDecimal(90), "should be equal");
    }

    @Test
    void testShouldReturnExceptionWhenOperationNotInformed() {
        Account account = Account.builder()
                .id(UUID.randomUUID())
                .agency("1234")
                .balance(new BigDecimal(100))
                .customerId(UUID.randomUUID())
                .isActive(Boolean.TRUE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        UpdateBalanceRequestDTO updateBalanceRequestDTO = UpdateBalanceRequestDTO.builder()
                .account(1L)
                .agency("1234")
                .amount(new BigDecimal(10))
                .operation(null)
                .build();

        CustomBusinessException exception = assertThrows(
                CustomBusinessException.class,
                () -> accountService.updateBalance(updateBalanceRequestDTO),
                "Should throw an exception");

        assertTrue(exception.getMessage().contains("Operação não informada"),
                "Should be true");
    }

    @Test
    void testShouldReturnExceptionWhenAmountNotInformed() {
        Account account = Account.builder()
                .id(UUID.randomUUID())
                .agency("1234")
                .balance(new BigDecimal(100))
                .customerId(UUID.randomUUID())
                .isActive(Boolean.TRUE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        UpdateBalanceRequestDTO updateBalanceRequestDTO = UpdateBalanceRequestDTO.builder()
                .account(1L)
                .agency("1234")
                .amount(null)
                .operation("CREDITO")
                .build();

        CustomBusinessException exception = assertThrows(
                CustomBusinessException.class,
                () -> accountService.updateBalance(updateBalanceRequestDTO),
                "Should throw an exception");

        assertTrue(exception.getMessage().contains("Valor informado para operação inválido"),
                "Should be true");
    }

    @Test
    void testShouldReturnExceptionWhenAmountIsGreaterThanBalance() {
        Account account = Account.builder()
                .id(UUID.randomUUID())
                .agency("1234")
                .account(1L)
                .balance(new BigDecimal(100))
                .customerId(UUID.randomUUID())
                .isActive(Boolean.TRUE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        UpdateBalanceRequestDTO updateBalanceRequestDTO = UpdateBalanceRequestDTO.builder()
                .account(1L)
                .agency("1234")
                .amount(new BigDecimal(200))
                .operation("DEBITO")
                .build();

        when(repository.findAccountsByAccountAndAgencyAndLock(anyLong(), anyString(), anyBoolean())).thenReturn(Optional.of(account));

        CustomBusinessException exception = assertThrows(
                CustomBusinessException.class,
                () -> accountService.updateBalance(updateBalanceRequestDTO),
                "Should throw an exception");

        assertTrue(exception.getMessage().contains("Conta não possui saldo suficiente para a operação"),
                "Should be true");
    }
}