package com.alba.master.security;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmployeeContext {
    private Long   employeeId;
    private String employeeName;
    private String email;
    private String employeeCode;
    private Long   roleId;
    private String roleName;
    private Long   departmentId;
}
