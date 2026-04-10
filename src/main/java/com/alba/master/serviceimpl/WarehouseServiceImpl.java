package com.alba.master.serviceimpl;

import com.alba.master.dto.request.WarehouseRequest;
import com.alba.master.dto.response.PaginationResponse;
import com.alba.master.dto.response.WarehouseResponse;
import com.alba.master.exception.DuplicateResourceException;
import com.alba.master.exception.ResourceNotFoundException;
import com.alba.master.model.Warehouse;
import com.alba.master.repository.WarehouseRepository;
import com.alba.master.security.EmployeeContextHolder;
import com.alba.master.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Transactional
    public WarehouseResponse create(WarehouseRequest request) {
       String wareHouseCode=generateWarehouseCode();

        Warehouse warehouse = Warehouse.builder()
                .warehouseCode(wareHouseCode)
                .warehouseName(request.getWarehouseName().trim())
                .location(request.getLocation())
                .isActive(true)
                .createdBy(EmployeeContextHolder.getEmployeeId())
                .build();

        Warehouse saved = warehouseRepository.save(warehouse);
        log.info("Warehouse created: {}", saved.getWarehouseCode());
        return toResponse(saved);
    }

    @Override
    public PaginationResponse<WarehouseResponse> getAll(int page, int size) {
        int pageIndex = (page <= 0) ? 0 : page - 1;

        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by("id").ascending());

        Page<Warehouse> customerPage =
                warehouseRepository.findAllByIsActiveTrue(pageable);

        List<WarehouseResponse> data = customerPage.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        PaginationResponse<WarehouseResponse> response = new PaginationResponse<>();
        response.setData(data);
        response.setCurrentPage(page);
        response.setTotalPage(customerPage.getTotalPages());
        response.setTotalElement(customerPage.getTotalElements());
        response.setHasNext(customerPage.hasNext());
        response.setHasPrevious(customerPage.hasPrevious());

        return response;
    }

    private String generateWarehouseCode() {

        Optional<Warehouse> lastWarehouse =
                warehouseRepository.findTopByOrderByIdDesc();

        if (lastWarehouse.isPresent()) {
            String lastCode = lastWarehouse.get().getWarehouseCode();

            int number = Integer.parseInt(lastCode.substring(2));
            number++;

            return String.format("WH%03d", number);
        } else {
            return "WH001";
        }
    }
//    public List<WarehouseResponse> getAll() {
//        return warehouseRepository.findAllByIsActiveTrue()
//                .stream().map(this::toResponse).toList();
//    }

    public WarehouseResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public WarehouseResponse update(Long id, WarehouseRequest request) {
        Warehouse warehouse = findById(id);

        warehouse.setWarehouseName(request.getWarehouseName().trim());
        warehouse.setLocation(request.getLocation());
        warehouse.setUpdatedBy(EmployeeContextHolder.getEmployeeId());
        warehouse.setUpdatedAt(LocalDateTime.now());

        Warehouse saved = warehouseRepository.save(warehouse);
        log.info("Warehouse updated: {}", saved.getWarehouseCode());
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        Warehouse warehouse = findById(id);
        warehouse.setIsActive(false);
        warehouseRepository.save(warehouse);
        log.info("Warehouse deactivated: {}", warehouse.getWarehouseCode());
    }

    @Override
    public List<WarehouseResponse> getAllWarehouses() {
        return warehouseRepository.findAll()
                .stream().map(this::toResponse).toList();
    }

    private Warehouse findById(Long id) {
        return warehouseRepository.findById(id)
                .filter(w -> Boolean.TRUE.equals(w.getIsActive()))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Warehouse not found with id: " + id));
    }

    public WarehouseResponse toResponse(Warehouse w) {
        return WarehouseResponse.builder()
                .id(w.getId())
                .warehouseCode(w.getWarehouseCode())
                .warehouseName(w.getWarehouseName())
                .location(w.getLocation())
                .isActive(w.getIsActive())
                .createdAt(w.getCreatedAt())
                .build();
    }
}
