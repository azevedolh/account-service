package com.desafio.accountservice.service.impl;

import com.desafio.accountservice.dto.*;
import com.desafio.accountservice.enumerator.OperationEnum;
import com.desafio.accountservice.exception.CustomBusinessException;
import com.desafio.accountservice.mapper.AccountRequestMapper;
import com.desafio.accountservice.mapper.AccountResponseMapper;
import com.desafio.accountservice.mapper.PageableMapper;
import com.desafio.accountservice.model.Account;
import com.desafio.accountservice.repository.AccountRepository;
import com.desafio.accountservice.service.AccountService;
import com.desafio.accountservice.util.MessageUtil;
import com.desafio.accountservice.util.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.desafio.accountservice.util.ConstantUtil.SORT_BY_CREATED_AT;


@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final PageableMapper pageableMapper;
    private final AccountResponseMapper accountResponseMapper;
    private final AccountRequestMapper accountRequestMapper;

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository,
                              PageableMapper pageableMapper,
                              AccountResponseMapper accountResponseMapper,
                              AccountRequestMapper accountRequestMapper) {
        this.accountRepository = accountRepository;
        this.pageableMapper = pageableMapper;
        this.accountResponseMapper = accountResponseMapper;
        this.accountRequestMapper = accountRequestMapper;
    }

    @Override
    public PageResponseDTO getAccounts(UUID customerId, Integer page, Integer size, String sort) {
        Sort sortProperties = PaginationUtil.getSort(sort, Sort.Direction.DESC, SORT_BY_CREATED_AT);

        PageRequest pageRequest = PageRequest.of(page - 1, size, sortProperties);
        PageResponseDTO pageResponseDTO = new PageResponseDTO();
        Page<Account> accountPage = accountRepository.findAllByCustomerIdAndIsActive(pageRequest, customerId, Boolean.TRUE);

        if (accountPage != null) {
            pageResponseDTO.set_pageable(pageableMapper.toDto(accountPage));
            pageResponseDTO.set_content(accountResponseMapper.toDto(accountPage.getContent()));
        }

        return pageResponseDTO;
    }

    @Override
    @Transactional
    public AccountResponseDTO create(CreateAccountRequestDTO accountRequest) {
        validateIfCustomerHasAccountWithSameAgency(
                accountRequest.getCustomerId(),
                accountRequest.getAgency()
        );

        Account account = accountRequestMapper.toEntity(accountRequest);
        account.setAccount(generateAccountNumber());
        account = accountRepository.save(account);
        return accountResponseMapper.toDto(account);
    }

    private void validateIfCustomerHasAccountWithSameAgency(UUID customerId, String agency) {
        Optional<Account> existingAccount = accountRepository.findByCustomerIdAndIsActiveAndAgency(
                customerId,
                Boolean.TRUE,
                agency
        );

        if (existingAccount.isPresent()) {
            String message = MessageUtil.getMessage("account.with.same.agency.exists", agency);
            throw new CustomBusinessException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private Long generateAccountNumber() {
        Optional<Long> account = accountRepository.findLastAccountAndLock();

        return account.map(value -> value + 1L).orElse(1L);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void updateBalance(UpdateBalanceRequestDTO updateBalanceRequestDTO) throws CustomBusinessException {

        OperationEnum operationEnum = validateOperation(updateBalanceRequestDTO.getOperation());
        validateAmount(updateBalanceRequestDTO.getAmount());

        Account account = getAccountByAgencyAndAccount(
                updateBalanceRequestDTO.getAgency(),
                updateBalanceRequestDTO.getAccount()
        );

        validateAndUpdateBalance(
                operationEnum,
                updateBalanceRequestDTO.getAmount(),
                account
        );

        accountRepository.save(account);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void transfer(TransferRequestDTO transferRequestDTO) {
        validateAmount(transferRequestDTO.getAmount());

        Account originAccount = getAccountByAgencyAndAccount(
                transferRequestDTO.getOriginAgency(),
                transferRequestDTO.getOriginAccount()
        );

        Account destinationAccount = getAccountByAgencyAndAccount(
                transferRequestDTO.getDestinationAgency(),
                transferRequestDTO.getDestinationAccount()
        );

        validateAndUpdateBalance(
                OperationEnum.DEBITO,
                transferRequestDTO.getAmount(),
                originAccount
        );

        validateAndUpdateBalance(
                OperationEnum.CREDITO,
                transferRequestDTO.getAmount(),
                destinationAccount
        );

        accountRepository.saveAll(List.of(originAccount, destinationAccount));
    }

    private static void validateAndUpdateBalance(OperationEnum operation, BigDecimal amount, Account account) {
        if (OperationEnum.DEBITO == operation) {
            if (amount.compareTo(account.getBalance()) > 0) {
                String message = MessageUtil.getMessage("account.not.enough.balance");
                throw new CustomBusinessException(message);
            }

            account.setBalance(account.getBalance().subtract(amount));
            return;
        }

        account.setBalance(account.getBalance().add(amount));
    }

    private static void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(new BigDecimal("0")) <= 0) {
            String message = MessageUtil.getMessage("account.amount.invalid");
            String details = MessageUtil.getMessage("account.amount.invalid.details");
            throw new CustomBusinessException(message, details);
        }
    }

    private static OperationEnum validateOperation(String operation) {
        if (operation == null) {
            String message = MessageUtil.getMessage("account.operation.not.informed");
            throw new CustomBusinessException(message);
        }

        if (!OperationEnum.isValid(operation)) {
            String message = MessageUtil.getMessage("account.operation.type.details");
            throw new CustomBusinessException(message);
        }

        return OperationEnum.valueOf(operation);
    }
    private Account getAccountByAgencyAndAccount(String agency, Long account) {
        Optional<Account> accountOptional = accountRepository.findAccountsByAccountAndAgencyAndLock(
                account,
                agency,
                Boolean.TRUE
        );

        if (accountOptional.isEmpty()) {
            String message = MessageUtil.getMessage("account.not.found", agency, account.toString());
            throw new CustomBusinessException(HttpStatus.NOT_FOUND, message);
        }

        return accountOptional.get();
    }
}
