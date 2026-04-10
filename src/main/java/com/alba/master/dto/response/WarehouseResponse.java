package com.alba.master.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class WarehouseResponse {
    private Long id;
    private String warehouseCode;
    private String warehouseName;
    private String location;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
