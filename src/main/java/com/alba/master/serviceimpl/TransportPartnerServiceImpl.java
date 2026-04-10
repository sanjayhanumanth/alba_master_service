package com.alba.master.serviceimpl;

import com.alba.master.dto.request.TransportPartnerRequest;
import com.alba.master.dto.response.PaginationResponse;
import com.alba.master.dto.response.TransportPartnerResponse;
import com.alba.master.exception.DuplicateResourceException;
import com.alba.master.exception.ResourceNotFoundException;
import com.alba.master.model.TransportPartner;
import com.alba.master.repository.TransportPartnerRepository;
import com.alba.master.security.EmployeeContextHolder;
import com.alba.master.service.TransportPartnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransportPartnerServiceImpl implements TransportPartnerService {

    private final TransportPartnerRepository transportPartnerRepository;

    @Transactional
    public TransportPartnerResponse create(TransportPartnerRequest request) {

        String transportCode=generateTransportPartnerCode();

        if(transportPartnerRepository.existsByPhone(request.getPhone())){
            throw new DuplicateResourceException(
                    "Phone number already exists: "+request.getPhone());
        }

        if(transportPartnerRepository.existsByEmail(request.getEmail())){
            throw new DuplicateResourceException(
                    "Email already exists: "+request.getEmail());
        }

        TransportPartner partner = TransportPartner.builder()
                .partnerCode(transportCode)
                .partnerName(request.getPartnerName().trim())
                .contactPerson(request.getContactPerson())
                .email(request.getEmail())
                .phone(request.getPhone())
                .hasPortal(request.getHasPortal() != null ? request.getHasPortal() : false)
                .portalUrl(request.getPortalUrl())
                .hasChepAccount(request.getHasChepAccount() != null ? request.getHasChepAccount() : false)
                .chepAccountNumber(request.getChepAccountNumber())
                .isActive(true)
                .createdBy(EmployeeContextHolder.getEmployeeId())
                .build();

        TransportPartner saved = transportPartnerRepository.save(partner);
        log.info("Transport partner created: {}", saved.getPartnerCode());
        return toResponse(saved);
    }



    private String generateTransportPartnerCode() {

        Optional<TransportPartner> lastPartner =
                transportPartnerRepository.findTopByOrderByIdDesc();

        if (lastPartner.isPresent()) {
            String lastCode = lastPartner.get().getPartnerCode(); // TP001

            int number = Integer.parseInt(lastCode.substring(2)); // remove "TP"
            number++;

            return String.format("TP%03d", number); // TP002
        } else {
            return "TP001";
        }
    }
//    public List<TransportPartnerResponse> getAll() {
//        return transportPartnerRepository.findAllByIsActiveTrue()
//                .stream().map(this::toResponse).toList();
//    }

    @Override
    public PaginationResponse<TransportPartnerResponse> getAll(int page, int size) {

        int pageIndex = (page <= 0) ? 0 : page - 1;

        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by("id").ascending());

        Page<TransportPartner> customerPage =
                transportPartnerRepository.findAllByIsActiveTrue(pageable);

        List<TransportPartnerResponse> data = customerPage.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        PaginationResponse<TransportPartnerResponse> response = new PaginationResponse<>();
        response.setData(data);
        response.setCurrentPage(page);
        response.setTotalPage(customerPage.getTotalPages());
        response.setTotalElement(customerPage.getTotalElements());
        response.setHasNext(customerPage.hasNext());
        response.setHasPrevious(customerPage.hasPrevious());

        return response;

    }

    public TransportPartnerResponse getById(Long id) {
        return toResponse(findById(id));
    }

    // used by logic service transport assignment — only no-portal partners
    public List<TransportPartnerResponse> getPartnersWithoutPortal() {
        return transportPartnerRepository.findAllByIsActiveTrueAndHasPortalFalse()
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public TransportPartnerResponse update(Long id, TransportPartnerRequest request) {
        TransportPartner partner = findById(id);

        partner.setPartnerName(request.getPartnerName().trim());
        partner.setContactPerson(request.getContactPerson());
        partner.setEmail(request.getEmail());
        partner.setPhone(request.getPhone());
        partner.setHasPortal(request.getHasPortal() != null ? request.getHasPortal() : false);
        partner.setPortalUrl(request.getPortalUrl());
        partner.setHasChepAccount(request.getHasChepAccount() != null ? request.getHasChepAccount() : false);
        partner.setChepAccountNumber(request.getChepAccountNumber());
        partner.setUpdatedBy(EmployeeContextHolder.getEmployeeId());

        TransportPartner saved = transportPartnerRepository.save(partner);
        log.info("Transport partner updated: {}", saved.getPartnerCode());
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        TransportPartner partner = findById(id);
        partner.setIsActive(false);
        partner.setUpdatedBy(EmployeeContextHolder.getEmployeeId());
        transportPartnerRepository.save(partner);
        log.info("Transport partner deactivated: {}", partner.getPartnerCode());
    }

    private TransportPartner findById(Long id) {
        return transportPartnerRepository.findById(id)
                .filter(p -> Boolean.TRUE.equals(p.getIsActive()))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transport partner not found with id: " + id));
    }

    public TransportPartnerResponse toResponse(TransportPartner p) {
        return TransportPartnerResponse.builder()
                .id(p.getId())
                .partnerCode(p.getPartnerCode())
                .partnerName(p.getPartnerName())
                .contactPerson(p.getContactPerson())
                .email(p.getEmail())
                .phone(p.getPhone())
                .hasPortal(p.getHasPortal())
                .portalUrl(p.getPortalUrl())
                .hasChepAccount(p.getHasChepAccount())
                .chepAccountNumber(p.getChepAccountNumber())
                .isActive(p.getIsActive())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
