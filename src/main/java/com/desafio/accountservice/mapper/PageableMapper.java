package com.desafio.accountservice.mapper;

import br.com.teste.accountmanagement.dto.response.PageableResponseDTO;
import org.springframework.data.domain.Page;

public interface PageableMapper {
    PageableResponseDTO toDto(Page<?> page);
}
