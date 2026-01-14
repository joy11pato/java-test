package com.test.ntt.services.impl;

import com.test.ntt.mappers.MapperConfig;
import com.test.ntt.messages.AccountRequest;
import com.test.ntt.messages.AccountUpdateRequest;
import com.test.ntt.messages.JsonResponse;
import com.test.ntt.models.dtos.AccountDto;
import com.test.ntt.models.dtos.MovementDto;
import com.test.ntt.models.entities.Account;
import com.test.ntt.models.entities.Catalog;
import com.test.ntt.repositories.AccountRepository;
import com.test.ntt.repositories.CatalogRepository;
import com.test.ntt.services.interfaces.AccountService;
import com.test.ntt.utils.FunctionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.stream.Collectors;

import static com.test.ntt.mappers.MapperConfig.mapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final ApiExternalServiceImpl externalService;

    private final AccountRepository accountRepository;

    private final CatalogRepository catalogRepository;

    @Override
    public Mono<ResponseEntity<JsonResponse<List<AccountDto>>>> listAccountsAllUsers() {
        return Mono.fromCallable(() -> {

                    return accountRepository.findAll();
                })

                .subscribeOn(Schedulers.boundedElastic())

                .map(entityList -> {
                    return entityList.stream()
                            .map(account -> {

                                AccountDto dto = MapperConfig.mapper.toAccountDto(account);
                                return dto;
                            })
                            .collect(Collectors.toList());
                })

                // 4. Construimos la respuesta final
                .map(dtoList -> {
                    JsonResponse<List<AccountDto>> response = new JsonResponse<>(true, "success", dtoList);
                    return ResponseEntity.ok(response);
                });
    }

    @Override
    public Mono<ResponseEntity<JsonResponse<List<AccountDto>>>> listAccountsByAccountNumber(String numberAccount) {
        return Mono.fromCallable(() -> {
                    return accountRepository.findByNumber(numberAccount);
                })

                .subscribeOn(Schedulers.boundedElastic())

                .map(entityList -> {
                    return entityList.stream()
                            .map(account -> {

                                AccountDto dto = MapperConfig.mapper.toAccountDto(account);

                                return dto;
                            })
                            .collect(Collectors.toList());
                })

                // 4. Construimos la respuesta final
                .map(dtoList -> {
                    JsonResponse<List<AccountDto>> response = new JsonResponse<>(true, "success", dtoList);
                    return ResponseEntity.ok(response);
                });
    }

    @Override
    public Mono<ResponseEntity<JsonResponse<List<AccountDto>>>> listAccountsByClientId(Long id) {

        return externalService.getCustomerByIdAsync(id)

                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado")))
                .flatMap(customer -> {
                    log.info("Cliente encontrado: {}", customer.getId());


                    return Mono.fromCallable(() -> accountRepository.findByIdClient(customer.getId()))
                            .subscribeOn(Schedulers.boundedElastic());
                })
                .map(accountList -> {

                    if (accountList.isEmpty()) {
                        log.warn("El cliente con ID {} existe pero no tiene cuentas registradas.", id);
                    }

                    return accountList.stream()
                            .map(account -> {

                                String tipoCuenta = (account.getAccountType() != null)
                                        ? account.getAccountType().getValue()
                                        : "Desconocido";

                                return MapperConfig.mapper.toAccountDto(account);
                            })
                            .collect(Collectors.toList());
                })
                .map(dtoList -> {
                    JsonResponse<List<AccountDto>> response = new JsonResponse<>(true, "success", dtoList);
                    return ResponseEntity.ok(response);
                })
                .onErrorResume(e -> {
                    log.error("Error al listar cuentas: ", e);

                    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
                    String message = "Ocurrió un error inesperado al consultar las cuentas";


                    if (e instanceof ResponseStatusException) {
                        ResponseStatusException rse = (ResponseStatusException) e;
                        status = HttpStatus.valueOf(rse.getStatusCode().value());
                        message = rse.getReason();
                    }

                    JsonResponse<List<AccountDto>> errorResponse = new JsonResponse<>(false, message, null);

                    return Mono.just(ResponseEntity.status(status).body(errorResponse));
                });

    }

    @Override
    public Mono<ResponseEntity<JsonResponse<AccountDto>>> createAccount(AccountRequest request) {

        return externalService.getCustomerByIdAsync(request.getClientId())

                .switchIfEmpty(Mono.error(new RuntimeException("Client not Found")))

                .flatMap(customer -> {

                    Catalog accountType = catalogRepository.findByMnemonic(request.getAccountType());
                    Account account = new Account(FunctionUtils.generateRandomNumber(),
                            accountType,
                            request.getInitialBalance(),
                            customer.getId(),
                            customer.getName());

                    return Mono.fromCallable(() -> accountRepository.save(account))
                            .subscribeOn(Schedulers.boundedElastic());
                })

                .map(savedAccount -> {
                    AccountDto dto = mapper.toAccountDto(savedAccount);
                    JsonResponse<AccountDto> response = new JsonResponse<>(true, "Account created", dto);
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                });
    }

    @Override
    public Mono<ResponseEntity<JsonResponse<AccountDto>>> updateAccount(Long id, AccountUpdateRequest dto) {
        return Mono.fromCallable(() -> {

            return accountRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Client not found"));
        })
                .subscribeOn(Schedulers.boundedElastic())
                .map(c -> {
                    c.setNumber(dto.getNumber());
                    c.setInitialBalance(dto.getInitialBalance());
                    c.setStatus(dto.getStatus());
                    c.setIdClient(dto.getIdClient());

                    accountRepository.save(c);

                    AccountDto clientDto = MapperConfig.mapper.toAccountDto(c);

                    JsonResponse<AccountDto> resp = new JsonResponse<>(true, "success", clientDto);
                    return ResponseEntity.ok(resp);
                });
    }

    @Override
    public Mono<ResponseEntity<JsonResponse>> deleteAccount(Long id) {
        return Mono.fromCallable(() -> {

                    return accountRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Client not found"));
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(c -> {

                    accountRepository.delete(c);

                    JsonResponse resp = new JsonResponse<>(true, "success", null);
                    return ResponseEntity.ok(resp);
                });
    }
}
