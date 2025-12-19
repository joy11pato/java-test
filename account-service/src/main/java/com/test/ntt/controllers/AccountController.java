package com.test.ntt.controllers;

import com.test.ntt.messages.AccountRequest;
import com.test.ntt.messages.AccountUpdateRequest;
import com.test.ntt.messages.JsonResponse;
import com.test.ntt.models.dtos.AccountDto;
import com.test.ntt.services.interfaces.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService service;

    @GetMapping
    public Mono<ResponseEntity<JsonResponse<List<AccountDto>>>> listAccountsAllUsers(){
        return service.listAccountsAllUsers();
    }

    @GetMapping("/client/{id}")
    public Mono<ResponseEntity<JsonResponse<List<AccountDto>>>> listAccountsByClientId(@PathVariable Long id){
        return service.listAccountsByClientId(id);
    }

    @GetMapping("/number/{number}")
    public Mono<ResponseEntity<JsonResponse<List<AccountDto>>>> listAccountsByAccountNumber(@PathVariable String number){
        return service.listAccountsByAccountNumber(number);
    }

    @PostMapping
    public Mono<ResponseEntity<JsonResponse<AccountDto>>> createAccount(@Valid @RequestBody AccountRequest request) {
        return service.createAccount(request);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<JsonResponse<AccountDto>>> updateAccount(@PathVariable Long id, @Valid @RequestBody AccountUpdateRequest request) {
        return service.updateAccount(id, request);
    }


}
