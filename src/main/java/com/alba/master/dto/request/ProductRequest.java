package com.alba.master.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {


    @NotBlank(message = "Product name is required")
    @Size(max = 255)
    private String productName;

    @Size(max = 500)
    private String description;

    @NotBlank(message = "Unit of measure is required")
    @Size(max = 20)
    private String uom;

    @DecimalMin(value = "0.001", message = "Pack size must be greater than 0")
    private BigDecimal packSize;
}
