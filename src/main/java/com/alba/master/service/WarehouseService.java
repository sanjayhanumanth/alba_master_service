package com.alba.master.service;

import com.alba.master.dto.request.WarehouseRequest;
import com.alba.master.dto.response.PaginationResponse;
import com.alba.master.dto.response.WarehouseResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface WarehouseService {
    WarehouseResponse create(@Valid WarehouseRequest request);

    PaginationResponse<WarehouseResponse> getAll(int page,int size);

    WarehouseResponse getById(Long id);

    WarehouseResponse update(Long id, @Valid WarehouseRequest request);

    void delete(Long id);

    List<WarehouseResponse> getAllWarehouses();
}
