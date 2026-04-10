package com.alba.master.serviceimpl;

import com.alba.master.dto.request.CustomerRequest;
import com.alba.master.dto.response.CustomerResponse;
import com.alba.master.dto.response.PaginationResponse;
import com.alba.master.exception.DuplicateResourceException;
import com.alba.master.exception.ResourceNotFoundException;
import com.alba.master.model.Customer;
import com.alba.master.repository.CustomerRepository;
import com.alba.master.security.EmployeeContextHolder;
import com.alba.master.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
    public CustomerResponse create(CustomerRequest request) {

        // ✅ Basic validation
        if (request.getCompanyName() == null || request.getCompanyName().trim().isEmpty()) {
            throw new IllegalArgumentException("Company name is required");
        }

        String companyName = request.getCompanyName().trim();
        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : null;

        if (email != null && customerRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email already exists: " + email);
        }

        if (customerRepository.existsByCustomerCode(companyName)) {
            throw new DuplicateResourceException("Company name already exists: " + companyName);
        }

        String customerCode = generateCustomerCode();

        Long employeeId = EmployeeContextHolder.getEmployeeId();
        if (employeeId == null) {
            throw new ResourceNotFoundException("Invalid token: employeeId missing");
        }

        int requiresBooking = request.getRequiresBooking() != null ? request.getRequiresBooking() : 0;

        // ✅ Build entity
        Customer customer = Customer.builder()
                .customerCode(customerCode)
                .companyName(companyName)
                .contactPerson(request.getContactPerson())
                .email(email)
                .phone(request.getPhone())
                .address(request.getAddress())
                .requiresBooking(requiresBooking == 1) // 👈 new field
                .isActive(true)
                .createdBy(employeeId)
                .build();

        Customer saved = customerRepository.save(customer);

        log.info("Customer created: {} by employee: {}", customerCode, employeeId);

        return toResponse(saved);
    }

    private String generateCustomerCode() {
        String prefix = "CUST";
        long count = customerRepository.count() + 1;
        return prefix + String.format("%05d", count);
    }

    // ── Read all active ───────────────────────────────────────
//    public List<CustomerResponse> getAll() {
//        return customerRepository.findAllByIsActiveTrue()
//                .stream().map(this::toResponse).toList();
//    }

    // ── Read by id ────────────────────────────────────────────
    public CustomerResponse getById(Long id) {
        return toResponse(findById(id));
    }

    // ── Read by code ──────────────────────────────────────────
    public CustomerResponse getByCode(String code) {
        Customer customer = customerRepository
                .findByCustomerCodeAndIsActiveTrue(code.toUpperCase())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with code: " + code));
        return toResponse(customer);
    }

    @Transactional
    public CustomerResponse update(Long id, CustomerRequest request) {

        Customer customer = findById(id);

        String companyName = request.getCompanyName().trim();
        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : null;

        if (email != null &&
                customerRepository.existsByEmailAndIdNot(email, id)) {
            throw new DuplicateResourceException("Email already exists: " + email);
        }

        if (customerRepository.existsByCompanyNameIgnoreCaseAndIdNot(companyName, id)) {
            throw new DuplicateResourceException("Company name already exists: " + companyName);
        }

        customer.setCompanyName(companyName);
        customer.setContactPerson(request.getContactPerson());
        customer.setEmail(email);
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());

        int requiresBooking = request.getRequiresBooking() != null
                ? request.getRequiresBooking()
                : 0;

        customer.setRequiresBooking(requiresBooking == 1);

        customer.setUpdatedBy(EmployeeContextHolder.getEmployeeId());

        Customer saved = customerRepository.save(customer);

        log.info("Customer updated: {}", saved.getCustomerCode());

        return toResponse(saved);
    }


    // ── Soft delete ───────────────────────────────────────────
    @Transactional
    public void delete(Long id) {
        Customer customer = findById(id);
        customer.setIsActive(false);
        customer.setUpdatedBy(EmployeeContextHolder.getEmployeeId());
        customerRepository.save(customer);
        log.info("Customer deactivated: {}", customer.getCustomerCode());
    }

    @Override
    public PaginationResponse<CustomerResponse> getAll(int page, int size) {
        int pageIndex = (page <= 0) ? 0 : page - 1;

        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by("id").ascending());

        Page<Customer> customerPage =
                customerRepository.findAllByIsActiveTrue(pageable);

        List<CustomerResponse> data = customerPage.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        PaginationResponse<CustomerResponse> response = new PaginationResponse<>();
        response.setData(data);
        response.setCurrentPage(page);
        response.setTotalPage(customerPage.getTotalPages());
        response.setTotalElement(customerPage.getTotalElements());
        response.setHasNext(customerPage.hasNext());
        response.setHasPrevious(customerPage.hasPrevious());

        return response;
    }

    // ── Helper ────────────────────────────────────────────────
    private Customer findById(Long id) {
        return customerRepository.findById(id)
                .filter(c -> Boolean.TRUE.equals(c.getIsActive()))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + id));
    }

    public CustomerResponse toResponse(Customer c) {
        return CustomerResponse.builder()
                .id(c.getId())
                .customerCode(c.getCustomerCode())
                .companyName(c.getCompanyName())
                .contactPerson(c.getContactPerson())
                .email(c.getEmail())
                .phone(c.getPhone())
                .requiresBooking(c.getRequiresBooking())
                .address(c.getAddress())
                .isActive(c.getIsActive())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
