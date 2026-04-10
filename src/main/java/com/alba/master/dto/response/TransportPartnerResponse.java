package com.alba.master.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TransportPartnerResponse {
    private Long id;
    private String partnerCode;
    private String partnerName;
    private String contactPerson;
    private String email;
    private String phone;
    private Boolean hasPortal;
    private String portalUrl;
    private Boolean hasChepAccount;
    private String chepAccountNumber;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
