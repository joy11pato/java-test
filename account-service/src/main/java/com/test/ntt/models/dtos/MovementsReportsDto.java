package com.test.ntt.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovementsReportsDto {

    private LocalDate dateAt;

    private String client;

    private String number;

    private String accountType;

    private BigDecimal initialBalance;

    private Boolean status;

    private BigDecimal movement;

    private BigDecimal currentBalance;


}
