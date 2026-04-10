package com.alba.master.serviceimpl;

import com.alba.master.dto.request.VehicleRequest;
import com.alba.master.dto.response.PaginationResponse;
import com.alba.master.dto.response.VehicleResponse;
import com.alba.master.exception.DuplicateResourceException;
import com.alba.master.exception.ResourceNotFoundException;
import com.alba.master.model.TransportPartner;
import com.alba.master.model.Vehicle;
import com.alba.master.repository.TransportPartnerRepository;
import com.alba.master.repository.VehicleRepository;
import com.alba.master.security.EmployeeContextHolder;
import com.alba.master.service.VehicleService;
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
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final TransportPartnerRepository transportPartnerRepository;

    @Transactional
    public VehicleResponse create(VehicleRequest request) {
        if (vehicleRepository.existsByVehicleNumber(request.getVehicleNumber())) {
            throw new DuplicateResourceException(
                    "Vehicle number already exists: " + request.getVehicleNumber());
        }

        TransportPartner partner = transportPartnerRepository.findById(request.getTransportPartnerId())
                .filter(p -> Boolean.TRUE.equals(p.getIsActive()))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Transport partner not found with id: " + request.getTransportPartnerId()));

        Vehicle vehicle = Vehicle.builder()
                .transportPartner(partner)
                .vehicleNumber(request.getVehicleNumber().toUpperCase().trim())
                .vehicleType(request.getVehicleType())
                .capacityKg(request.getCapacityKg())
                .isActive(true)
                .createdBy(EmployeeContextHolder.getEmployeeId())
                .build();

        Vehicle saved = vehicleRepository.save(vehicle);
        log.info("Vehicle created: {} under partner: {}",
                saved.getVehicleNumber(), partner.getPartnerCode());
        return toResponse(saved);
    }

    @Override
    public PaginationResponse<VehicleResponse> getAll(int page, int size) {
        int pageIndex = (page <= 0) ? 0 : page - 1;

        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by("id").ascending());

        Page<Vehicle> customerPage =
                vehicleRepository.findAllByIsActiveTrue(pageable);

        List<VehicleResponse> data = customerPage.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        PaginationResponse<VehicleResponse> response = new PaginationResponse<>();
        response.setData(data);
        response.setCurrentPage(page);
        response.setTotalPage(customerPage.getTotalPages());
        response.setTotalElement(customerPage.getTotalElements());
        response.setHasNext(customerPage.hasNext());
        response.setHasPrevious(customerPage.hasPrevious());

        return response;
    }

//    public List<VehicleResponse> getAll() {
//        return vehicleRepository.findAllByIsActiveTrue()
//                .stream().map(this::toResponse).toList();
//    }

    public VehicleResponse getById(Long id) {
        return toResponse(findById(id));
    }

    // get vehicles for a specific transport partner — used in transport assignment
    public List<VehicleResponse> getByTransportPartner(Long partnerId) {
        return vehicleRepository.findAllByTransportPartnerIdAndIsActiveTrue(partnerId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public VehicleResponse update(Long id, VehicleRequest request) {
        Vehicle vehicle = findById(id);

        if (!vehicle.getVehicleNumber().equals(request.getVehicleNumber().toUpperCase())) {
            if (vehicleRepository.existsByVehicleNumber(request.getVehicleNumber())) {
                throw new DuplicateResourceException(
                        "Vehicle number already exists: " + request.getVehicleNumber());
            }
            vehicle.setVehicleNumber(request.getVehicleNumber().toUpperCase().trim());
        }

        if (!vehicle.getTransportPartner().getId().equals(request.getTransportPartnerId())) {
            TransportPartner partner = transportPartnerRepository
                    .findById(request.getTransportPartnerId())
                    .filter(p -> Boolean.TRUE.equals(p.getIsActive()))
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Transport partner not found: " + request.getTransportPartnerId()));
            vehicle.setTransportPartner(partner);
        }

        vehicle.setVehicleType(request.getVehicleType());
        vehicle.setCapacityKg(request.getCapacityKg());
        vehicle.setUpdatedBy(EmployeeContextHolder.getEmployeeId());

        Vehicle saved = vehicleRepository.save(vehicle);
        log.info("Vehicle updated: {}", saved.getVehicleNumber());
        return toResponse(saved);
    }


    @Transactional
    public void delete(Long id) {
        Vehicle vehicle = findById(id);
        vehicle.setIsActive(false);
        vehicle.setUpdatedBy(EmployeeContextHolder.getEmployeeId());
        vehicleRepository.save(vehicle);
        log.info("Vehicle deactivated: {}", vehicle.getVehicleNumber());
    }

    @Override
    public List<VehicleResponse> getAllVehicles() {
        return vehicleRepository.findAll()
                .stream().map(this::toResponse).toList();
    }

    private Vehicle findById(Long id) {
        return vehicleRepository.findById(id)
                .filter(v -> Boolean.TRUE.equals(v.getIsActive()))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vehicle not found with id: " + id));
    }

    public VehicleResponse toResponse(Vehicle v) {
        return VehicleResponse.builder()
                .id(v.getId())
                .transportPartnerId(v.getTransportPartner().getId())
                .transportPartnerName(v.getTransportPartner().getPartnerName())
                .vehicleNumber(v.getVehicleNumber())
                .vehicleType(v.getVehicleType())
                .capacityKg(v.getCapacityKg())
                .isActive(v.getIsActive())
                .createdAt(v.getCreatedAt())
                .updatedAt(v.getUpdatedAt())
                .build();
    }
}
