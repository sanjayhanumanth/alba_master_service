package com.alba.master.repository;

import com.alba.master.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository
        extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findByProductCodeAndIsActiveTrue(String productCode);
    Page<Product> findAllByIsActiveTrue(Pageable pageable);
    boolean existsByProductCode(String productCode);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND " +
           "LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchByName(String keyword);

    Optional<Product> findTopByOrderByIdDesc();
}
