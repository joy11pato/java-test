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
public class MovementRequest {

    private Long accountId;

    private String movementType;

    private BigDecimal debAmount;

}
