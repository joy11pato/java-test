package com.test.ntt.services.impl;

import com.test.ntt.component.ExcelGenerator;
import com.test.ntt.mappers.MapperConfig;
import com.test.ntt.messages.JsonResponse;
import com.test.ntt.messages.MovementRequest;
import com.test.ntt.models.dtos.MovementsReportsDto;
import com.test.ntt.models.dtos.AccountDto;
import com.test.ntt.models.dtos.MovementDto;
import com.test.ntt.models.entities.Account;
import com.test.ntt.models.entities.Catalog;
import com.test.ntt.models.entities.Movement;
import com.test.ntt.repositories.AccountRepository;
import com.test.ntt.repositories.CatalogRepository;
import com.test.ntt.repositories.MovementRepository;
import com.test.ntt.services.interfaces.MovementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MovementServiceImpl implements MovementService {

    private final MovementRepository movementRepository;

    private final AccountRepository accountRepository;

    private final ApiExternalServiceImpl externalService;

    private final CatalogRepository catalogRepository;

    private final ExcelGenerator excelGenerator;;

    @Override
    public Mono<ResponseEntity<JsonResponse<MovementDto>>> registerMovement(MovementRequest request) {

        return Mono.fromCallable(() -> {


                    Account account = accountRepository.findById(request.getAccountId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));

                    if (request.getDebAmount().compareTo(BigDecimal.ZERO) < 0) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El valor del movimiento debe ser mayor que Cero");
                    }

                    Catalog movementType = catalogRepository.findByMnemonic(request.getMovementType());
                    if (movementType == null) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de movimiento no válido: " + request.getMovementType());
                    }

                    BigDecimal amount = request.getDebAmount();

                    if ("WITHDRAW".equalsIgnoreCase(movementType.getMnemonic())) {
                        amount = amount.negate();
                    }

                    Optional<Movement> lastMovement = movementRepository.findTopByAccountIdOrderByIdDesc(account.getId());

                    BigDecimal currentBalance = lastMovement
                            .map(Movement::getCurrentBalance)
                            .orElse(account.getInitialBalance());


                    BigDecimal newBalance = currentBalance.add(amount);
                    log.info("Intento transacción: Monto {}, Nuevo Saldo {}", amount, newBalance);

                    if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo No disponible");
                    }

                    Movement m = new Movement(movementType, amount, newBalance, account);

                    return movementRepository.save(m);
                })
                .subscribeOn(Schedulers.boundedElastic())

                .map(movementRegistered -> {
                    MovementDto dto = MapperConfig.mapper.toMovementDto(movementRegistered);
                    JsonResponse<MovementDto> response = new JsonResponse<>(true, "Movement registered", dto);
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                })

                .onErrorResume(e -> {
                    log.error("Error en registerMovement: {}", e.getMessage());

                    String errorMessage = e.getMessage();
                    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // Default

                    if (e instanceof ResponseStatusException) {
                        ResponseStatusException rse = (ResponseStatusException) e;
                        status = HttpStatus.valueOf(rse.getStatusCode().value());
                        errorMessage = rse.getReason();
                    }

                    JsonResponse<MovementDto> response = new JsonResponse<>(false, errorMessage, null);
                    return Mono.just(ResponseEntity.status(status).body(response));
                });
    }

    @Override
    public Mono<ResponseEntity<JsonResponse<List<MovementsReportsDto>>>> accountReport(Long clientId, LocalDate startDate, LocalDate endDate) {

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        return externalService.getCustomerByIdAsync(clientId)
                .switchIfEmpty(Mono.error(new RuntimeException("Cliente no encontrado")))
                .flatMap(customer -> {
                    log.info("customer: {}", customer.getName());
                    return Mono.fromCallable(() -> accountRepository.getAccounts(customer.getId()))
                            .subscribeOn(Schedulers.boundedElastic())


                            .flatMapIterable(accounts -> accounts)
                            .flatMap(account -> {
                                log.info("account Id: {}", account.getId());
                                return Mono.fromCallable(() ->
                                                movementRepository.findByAccountIdAndDateRange(
                                                        account.getId(),
                                                        startDateTime,
                                                        endDateTime
                                                )
                                        )
                                        .subscribeOn(Schedulers.boundedElastic())
                                        .flatMapIterable(movements -> movements)
                                        .map(movement -> {
                                            log.info("movement: {}", movement.getId());

                                            return MovementsReportsDto.builder()
                                                    .dateAt(movement.getCreatedAt().toLocalDate())
                                                    .client(customer.getName())
                                                    .number(account.getNumber())
                                                    .accountType(account.getAccountType() != null ? account.getAccountType().getValue() : "N/A")
                                                    .initialBalance(account.getInitialBalance())
                                                    .status(account.getStatus())
                                                    .movement(movement.getDebAmount())
                                                    .currentBalance(movement.getCurrentBalance())
                                                    .build();
                                        });

                            })

                            .collectList();

                })

                .map(dtoList -> {
                    JsonResponse<List<MovementsReportsDto>> response = new JsonResponse<>(true, "success", dtoList);
                    return ResponseEntity.ok(response);
                });

    }

    @Override
    public Mono<Resource> exportAccountReport(Long clientId, LocalDate startDate, LocalDate endDate) {

        return this.getMovementsDataInternal(clientId, startDate, endDate)
                .flatMap(dtoList -> {

                    return Mono.fromCallable(() -> excelGenerator.generateExcel(dtoList))
                            .subscribeOn(Schedulers.boundedElastic());
                })
                .map(InputStreamResource::new);
    }

    private Mono<List<MovementsReportsDto>> getMovementsDataInternal(Long clientId, LocalDate startDate, LocalDate endDate) {

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        return externalService.getCustomerByIdAsync(clientId)
                .switchIfEmpty(Mono.error(new RuntimeException("Cliente no encontrado")))
                .flatMap(customer -> {

                    log.info("Procesando reporte para cliente: {}", customer.getName());

                    return Mono.fromCallable(() -> accountRepository.getAccounts(customer.getId()))
                            .subscribeOn(Schedulers.boundedElastic())


                            .flatMapIterable(accounts -> accounts)

                            .flatMap(account -> { // <--- [APERTURA SCOPE CUENTA]

                                log.info("Procesando cuenta Id: {}", account.getId());

                                return Mono.fromCallable(() ->
                                                movementRepository.findByAccountIdAndDateRange(
                                                        account.getId(),
                                                        startDateTime,
                                                        endDateTime
                                                )
                                        )
                                        .subscribeOn(Schedulers.boundedElastic())


                                        .flatMapIterable(movements -> movements)

                                        .map(movement -> { // <--- [APERTURA SCOPE MOVIMIENTO]

                                            log.debug("Mapeando movimiento: {}", movement.getId());


                                            return MovementsReportsDto.builder()
                                                    .dateAt(movement.getCreatedAt().toLocalDate())
                                                    .client(customer.getName())
                                                    .number(account.getNumber())
                                                    .accountType(account.getAccountType() != null ? account.getAccountType().getValue() : "N/A")
                                                    .initialBalance(account.getInitialBalance())
                                                    .status(account.getStatus())
                                                    .movement(movement.getDebAmount())
                                                    .currentBalance(movement.getCurrentBalance())
                                                    .build();
                                        });

                            })

                            .collectList();

                });
    }
}
