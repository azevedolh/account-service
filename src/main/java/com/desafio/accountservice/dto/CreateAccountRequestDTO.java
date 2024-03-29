package com.desafio.accountservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccountRequestDTO {

    @NotNull
    private UUID customerId;

    @NotNull
    private String agency;
}
