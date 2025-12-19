package com.test.ntt.controllers;

import com.test.ntt.messages.JsonResponse;
import com.test.ntt.messages.MovementRequest;
import com.test.ntt.models.dtos.MovementsReportsDto;
import com.test.ntt.models.dtos.MovementDto;
import com.test.ntt.services.interfaces.MovementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/movements")
@RequiredArgsConstructor
public class MovementController {

    private final MovementService service;


    @PostMapping
    public Mono<ResponseEntity<JsonResponse<MovementDto>>> registerMovement(@Valid @RequestBody MovementRequest request) {
        return service.registerMovement(request);
    }

    @GetMapping("/client/{clientId}")
    public Mono<ResponseEntity<JsonResponse<List<MovementsReportsDto>>>> accountsReport(@PathVariable Long clientId,

                                                                                        @RequestParam("startDate")
                                                                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

                                                                                        @RequestParam("endDate")
                                                                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return service.accountReport(clientId, startDate, endDate);
    }

    @GetMapping("/report-download/{clientId}")
    public Mono<ResponseEntity<Resource>> downloadReport(
            @PathVariable Long clientId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {

        return service.exportAccountReport(clientId, startDate, endDate)
                .map(resource -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_movimientos.xlsx")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .body(resource));
    }
}
