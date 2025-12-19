package com.test.ntt.services.interfaces;

import com.test.ntt.messages.JsonResponse;
import com.test.ntt.messages.MovementRequest;
import com.test.ntt.models.dtos.MovementsReportsDto;
import com.test.ntt.models.dtos.MovementDto;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

public interface MovementService {

    Mono<ResponseEntity<JsonResponse<MovementDto>>> registerMovement(MovementRequest request);

    Mono<ResponseEntity<JsonResponse<List<MovementsReportsDto>>>> accountReport(Long clientId,
                                                                                LocalDate startDate,
                                                                                LocalDate endDate);

    Mono<Resource> exportAccountReport(Long clientId, LocalDate startDate, LocalDate endDate);
}
