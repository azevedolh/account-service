package com.desafio.accountservice.mapper;

import com.desafio.accountservice.dto.CreateAccountRequestDTO;
import com.desafio.accountservice.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountRequestMapper extends EntityMapper<CreateAccountRequestDTO, Account> {

    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "balance", constant = "0")
    Account toEntity(CreateAccountRequestDTO dto);
}
