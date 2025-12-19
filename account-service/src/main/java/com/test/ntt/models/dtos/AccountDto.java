package com.test.ntt.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDto {

    private String number;

    private String accountType;

    private BigDecimal initialBalance;

    private Boolean status;

    private Long client;


}

