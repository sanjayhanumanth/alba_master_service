package com.alba.master.repository;

import com.alba.master.model.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    Optional<Warehouse> findByWarehouseCodeAndIsActiveTrue(String warehouseCode);
    Page<Warehouse> findAllByIsActiveTrue(Pageable pageable);
    boolean existsByWarehouseCode(String warehouseCode);

    Optional<Warehouse> findTopByOrderByIdDesc();
}
