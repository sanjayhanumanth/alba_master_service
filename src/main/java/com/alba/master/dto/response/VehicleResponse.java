package com.alba.master.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class VehicleResponse {
    private Long id;
    private Long transportPartnerId;
    private String transportPartnerName;
    private String vehicleNumber;
    private String vehicleType;
    private BigDecimal capacityKg;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
