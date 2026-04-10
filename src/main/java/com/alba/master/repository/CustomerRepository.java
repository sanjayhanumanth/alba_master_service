package com.alba.master.repository;

import com.alba.master.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository
        extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {

    Optional<Customer> findByCustomerCodeAndIsActiveTrue(String customerCode);
    Page<Customer> findAllByIsActiveTrue(Pageable pageable);
    boolean existsByCustomerCode(String customerCode);
    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByCompanyNameIgnoreCaseAndIdNot(String companyName, Long id);
}
