package com.alba.master.service;

import com.alba.master.dto.request.CustomerRequest;
import com.alba.master.dto.response.CustomerResponse;
import com.alba.master.dto.response.PaginationResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CustomerService {


    CustomerResponse create(@Valid CustomerRequest request);



    CustomerResponse getById(Long id);

    CustomerResponse getByCode(String code);

    CustomerResponse update(Long id, @Valid CustomerRequest request);

    void delete(Long id);

    PaginationResponse<CustomerResponse> getAll(int page, int size);
}
