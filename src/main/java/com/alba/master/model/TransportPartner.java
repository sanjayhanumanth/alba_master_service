package com.alba.master.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transport_partners", catalog = "business", schema = "dbo")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TransportPartner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "partner_code", nullable = false, unique = true, length = 50)
    private String partnerCode;

    @Column(name = "partner_name", nullable = false, length = 255)
    private String partnerName;

    @Column(name = "contact_person", length = 100)
    private String contactPerson;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    // drives portal vs email branch in flow
    @Column(name = "has_portal")
    private Boolean hasPortal = false;

    @Column(name = "portal_url", length = 500)
    private String portalUrl;

    // drives PICK_UP vs TRANSFER watermark category
    @Column(name = "has_chep_account")
    private Boolean hasChepAccount = false;

    @Column(name = "chep_account_number", length = 100)
    private String chepAccountNumber;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
