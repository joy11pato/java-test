package com.test.ntt.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerData {
    private Long id;
    private String name;
    private String gender;
    private String idCard;
    private String address;
    private String phone;
    private boolean active;
}
