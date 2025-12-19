package com.test.ntt.services.interfaces;

import com.test.ntt.models.dtos.CustomerData;
import reactor.core.publisher.Mono;

public interface ApiExtenalService {
    Mono<CustomerData> getCustomerByIdAsync(Long idCard);
}
