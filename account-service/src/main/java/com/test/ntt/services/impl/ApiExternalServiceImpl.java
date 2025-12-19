package com.test.ntt.services.impl;

import com.test.ntt.messages.JsonResponse;
import com.test.ntt.models.dtos.CustomerData;
import com.test.ntt.services.interfaces.ApiExtenalService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
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
                .bodyToMono(new ParameterizedTypeReference<JsonResponse<CustomerData>>() {})
                .filter(JsonResponse::isSuccess)
                .map(JsonResponse::getData);
    }
}
