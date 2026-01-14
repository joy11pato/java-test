package com.test.ntt.services.impl;

import com.test.ntt.exceptions.ResourceNotFoundException;
import com.test.ntt.messages.JsonResponse;
import com.test.ntt.models.dtos.CustomerData;
import com.test.ntt.services.interfaces.ApiExtenalService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;

@Service
@Slf4j
public class ApiExternalServiceImpl implements ApiExtenalService {

    private final WebClient apiCustomer;

    public ApiExternalServiceImpl( @Qualifier("apiCustomer") WebClient apiCustomer){
        this.apiCustomer = apiCustomer;

    }


    @Override
    public Mono<CustomerData> getCustomerByIdAsync(Long id) {
        return this.apiCustomer
                .get()
                .uri("/{id}", Collections.singletonMap("id", id))
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response -> {
                    if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.error(new ResourceNotFoundException("Cliente con ID " + id + " no encontrado en el sistema externo"));
                    }
                    return response.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(new RuntimeException("Error del cliente: " + errorBody)));
                })
                .onStatus(status -> status.is5xxServerError(), response -> {
                    if (response.statusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR)) {
                        return Mono.error(new RuntimeException("Error en el sistema externo"));
                    }
                    return response.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(new RuntimeException("Error del servicio cliente: " + errorBody)));
                })

                .bodyToMono(new ParameterizedTypeReference<JsonResponse<CustomerData>>() {})
                .timeout(Duration.ofSeconds(3))
                .doOnError(e -> log.error("Fallo al consultar cliente: {}", e.getMessage()))
                .flatMap(resp -> {
                    if (resp.getData() == null) {
                        return Mono.error(new ResourceNotFoundException("La respuesta del cliente no contiene datos"));
                    }
                    return Mono.just(resp.getData());
                });
    }
}
