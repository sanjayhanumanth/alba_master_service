package com.alba.master.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DepartmentResponse {
    private Long id;
    private String departmentCode;
    private String departmentName;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
