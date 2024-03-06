package com.desafio.accountservice.service;

import com.desafio.accountservice.dto.*;
import com.desafio.accountservice.enumerator.OperationEnum;
import com.desafio.accountservice.exception.CustomBusinessException;
import com.desafio.accountservice.model.Account;

import java.math.BigDecimal;
import java.util.UUID;

public interface AccountService {

    PageResponseDTO getAccounts(UUID customerId, Integer page, Integer size, String sort);

    AccountResponseDTO create(CreateAccountRequestDTO account);

    void updateBalance(UpdateBalanceRequestDTO updateBalanceRequestDTO) throws CustomBusinessException;

    void transfer(TransferRequestDTO transferRequestDTO);
}
