package com.alba.master.repository;

import com.alba.master.model.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByDepartmentCodeAndIsActiveTrue(String departmentCode);
    Page<Department> findAllByIsActiveTrue(Pageable pageable);
    boolean existsByDepartmentCode(String departmentCode);

    Optional<Department> findTopByOrderByIdDesc();
}
