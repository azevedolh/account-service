package com.desafio.accountservice.enumerator;

import java.util.Arrays;

public enum OperationEnum {
    DEBITO(1l, "Débito"),
    CREDITO(2l, "Crédito");

    private final Long code;
    private final String description;

    public static Boolean isValid(String operation) {
        return Arrays.stream(OperationEnum.values()).anyMatch(item -> item.name().equals(operation));
    }

    OperationEnum(Long code, String description) {
        this.code = code;
        this.description = description;
    }
}
