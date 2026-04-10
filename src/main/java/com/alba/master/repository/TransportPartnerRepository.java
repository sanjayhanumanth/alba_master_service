package com.alba.master.repository;

import com.alba.master.dto.response.PaginationResponse;
import com.alba.master.model.TransportPartner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransportPartnerRepository
        extends JpaRepository<TransportPartner, Long>, JpaSpecificationExecutor<TransportPartner> {

    Optional<TransportPartner> findByPartnerCodeAndIsActiveTrue(String partnerCode);
    Page<TransportPartner> findAllByIsActiveTrue(Pageable pageable);
    List<TransportPartner> findAllByIsActiveTrueAndHasPortalFalse();     // needs auto-email
    List<TransportPartner> findAllByIsActiveTrueAndHasChepAccountTrue(); // TRANSFER category
    boolean existsByPartnerCode(String partnerCode);
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);

    Optional<TransportPartner> findTopByOrderByIdDesc();
}
