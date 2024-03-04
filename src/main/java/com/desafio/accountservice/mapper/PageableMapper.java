package com.desafio.accountservice.mapper;

import com.desafio.accountservice.dto.PageableResponseDTO;
import org.springframework.data.domain.Page;

public interface PageableMapper {
    PageableResponseDTO toDto(Page<?> page);
}
