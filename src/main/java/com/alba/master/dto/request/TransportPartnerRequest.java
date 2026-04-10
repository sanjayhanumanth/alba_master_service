package com.alba.master.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransportPartnerRequest {

    @NotBlank(message = "Partner name is required")
    @Size(max = 255)
    private String partnerName;

    @Size(max = 100)
    private String contactPerson;

    @Email(message = "Invalid email format")
    @Size(max = 255)
    private String email;

    @Size(max = 20)
    private String phone;

    private Boolean hasPortal = false;

    @Size(max = 500)
    private String portalUrl;

    private Boolean hasChepAccount = false;

    @Size(max = 100)
    private String chepAccountNumber;
}
