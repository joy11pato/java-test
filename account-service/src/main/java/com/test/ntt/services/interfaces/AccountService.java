package com.test.ntt.services.interfaces;

import com.test.ntt.messages.AccountRequest;
import com.test.ntt.messages.AccountUpdateRequest;
import com.test.ntt.messages.JsonResponse;
import com.test.ntt.models.dtos.AccountDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AccountService {

    Mono<ResponseEntity<JsonResponse<List<AccountDto>>>> listAccountsAllUsers();

    Mono<ResponseEntity<JsonResponse<List<AccountDto>>>> listAccountsByAccountNumber(String numberAccount);

    Mono<ResponseEntity<JsonResponse<List<AccountDto>>>> listAccountsByClientId(Long idcard);

    Mono<ResponseEntity<JsonResponse<AccountDto>>> createAccount(AccountRequest request);

    Mono<ResponseEntity<JsonResponse<AccountDto>>> updateAccount(Long id, AccountUpdateRequest dto);

    Mono<ResponseEntity<JsonResponse>> deleteAccount(Long id);


}
