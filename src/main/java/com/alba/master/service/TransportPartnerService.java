package com.alba.master.service;

import com.alba.master.dto.request.TransportPartnerRequest;
import com.alba.master.dto.response.PaginationResponse;
import com.alba.master.dto.response.TransportPartnerResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TransportPartnerService {
    TransportPartnerResponse create(@Valid TransportPartnerRequest request);

//    List<TransportPartnerResponse> getAll();

    PaginationResponse<TransportPartnerResponse> getAll(int page,int size);

    TransportPartnerResponse getById(Long id);

    List<TransportPartnerResponse> getPartnersWithoutPortal();

    TransportPartnerResponse update(Long id, @Valid TransportPartnerRequest request);

    void delete(Long id);
}
