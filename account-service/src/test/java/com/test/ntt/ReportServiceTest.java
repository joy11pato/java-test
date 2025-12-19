package com.test.ntt;

import com.test.ntt.messages.JsonResponse;
import com.test.ntt.models.dtos.CustomerData;
import com.test.ntt.models.dtos.MovementsReportsDto;
import com.test.ntt.models.entities.Account;
import com.test.ntt.models.entities.Movement;
import com.test.ntt.repositories.AccountRepository;
import com.test.ntt.repositories.MovementRepository;
import com.test.ntt.services.impl.ApiExternalServiceImpl;
import com.test.ntt.services.interfaces.MovementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {

    @Mock
    private ApiExternalServiceImpl externalService; // Devuelve Mono

    @Mock
    private AccountRepository accountRepository; // Devuelve List (Bloqueante)

    @Mock
    private MovementRepository movementRepository; // Devuelve List (Bloqueante)

    @InjectMocks
    private MovementService movementService; // Tu clase de servicio

    @Test
    void accountReport_DeberiaRetornarListaDeMovimientos_CuandoClienteExiste() {

        Long clientId = 1L;
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();


        CustomerData customerMock = new CustomerData();
        customerMock.setId(clientId);
        customerMock.setName("Jose Perez");


        Account accountMock = new Account();
        accountMock.setId(100L);
        accountMock.setNumber("47854");
        accountMock.setInitialBalance(BigDecimal.valueOf(500.0));
        accountMock.setStatus(true);
        // Asumiendo que AccountType es un Enum o clase
        // accountMock.setAccountType(...);


        Movement movementMock = new Movement();
        movementMock.setId(999L);
        movementMock.setDebAmount(BigDecimal.valueOf(50));
        movementMock.setCurrentBalance(BigDecimal.valueOf(450));
        movementMock.setCreatedAt(LocalDateTime.now());


        when(externalService.getCustomerByIdAsync(clientId))
                .thenReturn(Mono.just(customerMock));


        when(accountRepository.getAccounts(clientId))
                .thenReturn(List.of(accountMock));

        when(movementRepository.findByAccountIdAndDateRange(eq(100L), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(movementMock));


        Mono<ResponseEntity<JsonResponse<List<MovementsReportsDto>>>> resultMono =
                movementService.accountReport(clientId, startDate, endDate);

        StepVerifier.create(resultMono)
                .assertNext(responseEntity -> {

                    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
                    assertNotNull(responseEntity.getBody());


                    JsonResponse<List<MovementsReportsDto>> body = responseEntity.getBody();
                    assertTrue(body.isSuccess());
                    assertFalse(body.getData().isEmpty());


                    MovementsReportsDto dto = body.getData().get(0);
                    assertEquals("Jose Perez", dto.getClient());
                    assertEquals("47854", dto.getNumber());
                    assertEquals(50.0, dto.getMovement());
                })
                .verifyComplete();
    }
}
