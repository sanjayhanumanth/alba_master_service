package com.alba.master.serviceimpl;

import com.alba.master.dto.request.DepartmentRequest;
import com.alba.master.dto.response.CustomerResponse;
import com.alba.master.dto.response.DepartmentResponse;
import com.alba.master.dto.response.PaginationResponse;
import com.alba.master.exception.DuplicateResourceException;
import com.alba.master.exception.ResourceNotFoundException;
import com.alba.master.model.Department;
import com.alba.master.repository.DepartmentRepository;
import com.alba.master.security.EmployeeContextHolder;
import com.alba.master.service.DepartmentService;
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
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {

        String departmentCode=generateDepartmentCode();

        Department department = Department.builder()
                .departmentCode(departmentCode)
                .departmentName(request.getDepartmentName().trim())
                .description(request.getDescription())
                .isActive(true)
                .createdBy(EmployeeContextHolder.getEmployeeId())
                .build();

        Department saved = departmentRepository.save(department);
        log.info("Department created: {}", saved.getDepartmentCode());
        return toResponse(saved);
    }

    @Override
    public PaginationResponse<DepartmentResponse> getAll(int page, int size) {

        int pageIndex = (page <= 0) ? 0 : page - 1;

        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by("id").ascending());

        Page<Department> customerPage =
                departmentRepository.findAllByIsActiveTrue(pageable);

        List<DepartmentResponse> data = customerPage.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        PaginationResponse<DepartmentResponse> response = new PaginationResponse<>();
        response.setData(data);
        response.setCurrentPage(page);
        response.setTotalPage(customerPage.getTotalPages());
        response.setTotalElement(customerPage.getTotalElements());
        response.setHasNext(customerPage.hasNext());
        response.setHasPrevious(customerPage.hasPrevious());

        return response;

    }

    private String generateDepartmentCode() {

        Optional<Department> lastDepartment =
                departmentRepository.findTopByOrderByIdDesc();

        if (lastDepartment.isPresent()) {
            String lastCode = lastDepartment.get().getDepartmentCode(); // DEP001

            int number = Integer.parseInt(lastCode.substring(3)); // remove "DEP"
            number++;

            return String.format("DEP%03d", number); // DEP002
        } else {
            return "DEP001";
        }
    }

//    public List<DepartmentResponse> getAll() {
//        return departmentRepository.findAllByIsActiveTrue()
//                .stream().map(this::toResponse).toList();
//    }

    public DepartmentResponse getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public DepartmentResponse update(Long id, DepartmentRequest request) {
        Department department = findById(id);

        department.setDepartmentName(request.getDepartmentName().trim());
        department.setDescription(request.getDescription());
        department.setUpdateAt(LocalDateTime.now());
        department.setUpdatedBy(EmployeeContextHolder.getEmployeeId());

        Department saved = departmentRepository.save(department);
        log.info("Department updated: {}", saved.getDepartmentCode());
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        Department department = findById(id);
        department.setIsActive(false);
        departmentRepository.save(department);
        log.info("Department deactivated: {}", department.getDepartmentCode());
    }

    private Department findById(Long id) {
        return departmentRepository.findById(id)
                .filter(d -> Boolean.TRUE.equals(d.getIsActive()))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Department not found with id: " + id));
    }

    public DepartmentResponse toResponse(Department d) {
        return DepartmentResponse.builder()
                .id(d.getId())
                .departmentCode(d.getDepartmentCode())
                .departmentName(d.getDepartmentName())
                .description(d.getDescription())
                .isActive(d.getIsActive())
                .createdAt(d.getCreatedAt())
                .build();
    }
}
