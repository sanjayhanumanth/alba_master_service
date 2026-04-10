package com.alba.master.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class VehicleRequest {

    @NotNull(message = "Transport partner is required")
    private Long transportPartnerId;

    @NotBlank(message = "Vehicle number is required")
    @Size(max = 50)
    private String vehicleNumber;

    @Size(max = 50)
    private String vehicleType;             // TRUCK, VAN, CONTAINER

    @DecimalMin(value = "0.01", message = "Capacity must be greater than 0")
    private BigDecimal capacityKg;
}
