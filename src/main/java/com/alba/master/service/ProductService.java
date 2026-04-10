package com.alba.master.service;

import com.alba.master.dto.request.ProductRequest;
import com.alba.master.dto.response.PaginationResponse;
import com.alba.master.dto.response.ProductResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {
    ProductResponse create(@Valid ProductRequest request);

    PaginationResponse<ProductResponse> getAll(int page,int size);

    ProductResponse getById(Long id);

    List<ProductResponse> search(String keyword);

    ProductResponse update(Long id, @Valid ProductRequest request);

    void delete(Long id);
}
