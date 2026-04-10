package com.alba.master.serviceimpl;

import com.alba.master.dto.request.ProductRequest;
import com.alba.master.dto.response.PaginationResponse;
import com.alba.master.dto.response.ProductResponse;
import com.alba.master.exception.DuplicateResourceException;
import com.alba.master.exception.ResourceNotFoundException;
import com.alba.master.model.Product;
import com.alba.master.model.Warehouse;
import com.alba.master.repository.ProductRepository;
import com.alba.master.security.EmployeeContextHolder;
import com.alba.master.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse create(ProductRequest request) {

        String productCode=generateProductCode();

        Product product = Product.builder()
                .productCode(productCode)
                .productName(request.getProductName().trim())
                .description(request.getDescription())
                .uom(request.getUom().toUpperCase().trim())
                .packSize(request.getPackSize())
                .isActive(true)
                .createdBy(EmployeeContextHolder.getEmployeeId())
                .build();

        Product saved = productRepository.save(product);
        log.info("Product created: {}", saved.getProductCode());
        return toResponse(saved);
    }

    @Override
    public PaginationResponse<ProductResponse> getAll(int page, int size) {
        int pageIndex = (page <= 0) ? 0 : page - 1;

        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by("id").ascending());

        Page<Product> customerPage =
                productRepository.findAllByIsActiveTrue(pageable);

        List<ProductResponse> data = customerPage.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        PaginationResponse<ProductResponse> response = new PaginationResponse<>();
        response.setData(data);
        response.setCurrentPage(page);
        response.setTotalPage(customerPage.getTotalPages());
        response.setTotalElement(customerPage.getTotalElements());
        response.setHasNext(customerPage.hasNext());
        response.setHasPrevious(customerPage.hasPrevious());

        return response;

    }


    private String generateProductCode() {

        Optional<Product> lastProduct =
                productRepository.findTopByOrderByIdDesc();

        if (lastProduct.isPresent()) {
            String lastCode = lastProduct.get().getProductCode();

            int number = Integer.parseInt(lastCode.substring(1));
            number++;

            return String.format("P%03d", number);
        } else {
            return "P001";
        }
    }



//    public List<ProductResponse> getAll() {
//        return productRepository.findAllByIsActiveTrue()
//                .stream().map(this::toResponse).toList();
//    }

    public ProductResponse getById(Long id) {
        return toResponse(findById(id));
    }

    public List<ProductResponse> search(String keyword) {
        if (!StringUtils.hasText(keyword));
        return productRepository.searchByName(keyword)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findById(id);
        product.setProductName(request.getProductName().trim());
        product.setDescription(request.getDescription());
        product.setUom(request.getUom().toUpperCase().trim());
        product.setPackSize(request.getPackSize());
        product.setUpdatedBy(EmployeeContextHolder.getEmployeeId());

        Product saved = productRepository.save(product);
        log.info("Product updated: {}", saved.getProductCode());
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        Product product = findById(id);
        product.setIsActive(false);
        product.setUpdatedBy(EmployeeContextHolder.getEmployeeId());
        productRepository.save(product);
        log.info("Product deactivated: {}", product.getProductCode());
    }

    private Product findById(Long id) {
        return productRepository.findById(id)
                .filter(p -> Boolean.TRUE.equals(p.getIsActive()))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id));
    }

    public ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .productCode(p.getProductCode())
                .productName(p.getProductName())
                .description(p.getDescription())
                .uom(p.getUom())
                .packSize(p.getPackSize())
                .isActive(p.getIsActive())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
