package com.test.ntt.messages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountUpdateRequest {

    private String number;

    private String accountType;

    private BigDecimal initialBalance;

    private Boolean status;

    private Long idClient;

}

