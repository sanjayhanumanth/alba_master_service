package com.alba.master.repository;

import com.alba.master.model.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findAllByTransportPartnerIdAndIsActiveTrue(Long transportPartnerId);
    Page<Vehicle> findAllByIsActiveTrue(Pageable pageable);
    Optional<Vehicle> findByVehicleNumberAndIsActiveTrue(String vehicleNumber);
    boolean existsByVehicleNumber(String vehicleNumber);
}
