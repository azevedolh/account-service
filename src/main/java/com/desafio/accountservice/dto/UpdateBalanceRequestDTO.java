package com.desafio.accountservice.dto;

import com.desafio.accountservice.enumerator.OperationEnum;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateBalanceRequestDTO {

    @NotNull
    private Long account;

    @NotNull
    private String agency;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private String operation;
}
