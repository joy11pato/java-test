package com.test.ntt;


import com.test.ntt.models.dtos.CustomerData;
import com.test.ntt.models.entities.Account;
import com.test.ntt.models.entities.Movement;
import com.test.ntt.repositories.AccountRepository;
import com.test.ntt.repositories.MovementRepository;
import com.test.ntt.services.impl.ApiExternalServiceImpl;
import com.test.ntt.services.interfaces.MovementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class ReportServiceIntegrationTest {
    @Autowired
    private MovementService movementService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MovementRepository movementRepository;

    @MockitoBean
    private ApiExternalServiceImpl externalService;

    @BeforeEach
    void setUp() {
        // Limpiamos la BD antes de cada test para evitar datos basura
        movementRepository.deleteAll();
        accountRepository.deleteAll();
    }

    @Test
    void integration_AccountReport_DeberiaTraerDatosRealesDeH2() {

        Account account = new Account();
        account.setNumber("47854");
        account.setInitialBalance(new BigDecimal("500.00"));
        account.setStatus(true);

        Account savedAccount = accountRepository.save(account);

        Movement movement = new Movement();
        movement.setDebAmount(new BigDecimal("50.00"));
        movement.setCurrentBalance(new BigDecimal("450.00"));
        movement.setCreatedAt(LocalDateTime.now());

        movement.setAccount(savedAccount);

        movementRepository.save(movement);

        Long clienteId = 1L;
        CustomerData customerMock = new CustomerData();
        customerMock.setId(clienteId);
        customerMock.setName("Jose Perez");

        when(externalService.getCustomerByIdAsync(clienteId))
                .thenReturn(Mono.just(customerMock));


        LocalDate startDate = LocalDate.now().minusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(1);

        var resultadoMono = movementService.accountReport(clienteId, startDate, endDate);

        StepVerifier.create(resultadoMono)
                .assertNext(responseEntity -> {
                    var body = responseEntity.getBody();
                    assertNotNull(body);
                    assertTrue(body.isSuccess());
                    assertFalse(body.getData().isEmpty());

                    var dto = body.getData().get(0);

                    assertEquals("47854", dto.getNumber());

                    assertEquals(0, new BigDecimal("50.00").compareTo(dto.getMovement()));
                    assertEquals("Jose Perez", dto.getClient());
                })
                .verifyComplete();
    }
}
