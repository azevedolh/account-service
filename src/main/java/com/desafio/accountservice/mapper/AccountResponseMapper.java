package com.desafio.accountservice.mapper;

import com.desafio.accountservice.dto.AccountResponseDTO;
import com.desafio.accountservice.model.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountResponseMapper extends EntityMapper<AccountResponseDTO, Account> {
}
