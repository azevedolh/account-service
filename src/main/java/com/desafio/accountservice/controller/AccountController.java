package com.desafio.accountservice.controller;

import com.desafio.accountservice.dto.*;
import com.desafio.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Log4j2
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO> getAll(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "_sort", required = false) String sort,
            @RequestParam(value = "customer", required = true) String customerId) {
        return new ResponseEntity<>(accountService.getAccounts(UUID.fromString(customerId), page, size, sort), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PostResponseDTO> create(
            @RequestBody @Valid CreateAccountRequestDTO createAccountRequest) {
        AccountResponseDTO createdAccount = accountService.create(createAccountRequest);

        URI locationResource = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdAccount.getId())
                .toUri();
        log.info("Successfully created Account with ID: " + createdAccount.getId());
        return ResponseEntity.created(locationResource).body(PostResponseDTO.builder().id(createdAccount.getId()).build());
    }

    @PostMapping("/update-balance")
    public ResponseEntity<StatusResponseDTO> updateBalance(
            @RequestBody @Valid UpdateBalanceRequestDTO updateBalanceRequest) {
        accountService.updateBalance(updateBalanceRequest);

        return new ResponseEntity<>(StatusResponseDTO.builder().status("SUCESSO").build(), HttpStatus.OK);
    }

    @PostMapping("/transfer")
    public ResponseEntity<StatusResponseDTO> transfer(
            @RequestBody @Valid TransferRequestDTO transferRequestDTO) {
        accountService.transfer(transferRequestDTO);

        return new ResponseEntity<>(StatusResponseDTO.builder().status("SUCESSO").build(), HttpStatus.OK);
    }
}
