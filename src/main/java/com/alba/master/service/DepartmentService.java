package com.alba.master.service;

import com.alba.master.dto.request.DepartmentRequest;
import com.alba.master.dto.response.DepartmentResponse;
import com.alba.master.dto.response.PaginationResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DepartmentService {
    DepartmentResponse create(@Valid DepartmentRequest request);

    PaginationResponse<DepartmentResponse> getAll(int page,int size);

    DepartmentResponse getById(Long id);

    DepartmentResponse update(Long id, @Valid DepartmentRequest request);

    void delete(Long id);
}
