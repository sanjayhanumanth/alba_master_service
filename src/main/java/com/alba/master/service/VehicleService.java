package com.alba.master.service;

import com.alba.master.dto.request.VehicleRequest;
import com.alba.master.dto.response.PaginationResponse;
import com.alba.master.dto.response.VehicleResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VehicleService {
    VehicleResponse create(@Valid VehicleRequest request);

    PaginationResponse<VehicleResponse> getAll(int page,int size);

    VehicleResponse getById(Long id);

    List<VehicleResponse> getByTransportPartner(Long partnerId);

    VehicleResponse update(Long id, @Valid VehicleRequest request);

    void delete(Long id);

    List<VehicleResponse> getAllVehicles();

}
