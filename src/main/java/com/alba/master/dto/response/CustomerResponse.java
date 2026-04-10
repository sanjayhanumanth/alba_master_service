package com.alba.master.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CustomerResponse {
    private Long id;
    private String customerCode;
    private String companyName;
    private String contactPerson;
    private String email;
    private String phone;
    private Boolean requiresBooking;
    private String address;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
