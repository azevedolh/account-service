package com.desafio.accountservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccountRequestDTO {

    @NotNull
    private String agency;

    @NotNull
    private BigDecimal balance;
}
