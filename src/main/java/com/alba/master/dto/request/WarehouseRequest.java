package com.alba.master.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarehouseRequest {



    @NotBlank(message = "Warehouse name is required")
    @Size(max = 255)
    private String warehouseName;

    @Size(max = 255)
    private String location;
}
