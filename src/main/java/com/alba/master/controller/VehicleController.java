package com.alba.master.controller;

import com.alba.master.dto.request.VehicleRequest;
import com.alba.master.dto.response.ApiResponse;
import com.alba.master.dto.response.PaginationResponse;
import com.alba.master.dto.response.VehicleResponse;
import com.alba.master.service.VehicleService;
import com.alba.master.serviceimpl.VehicleServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
@Tag(name = "Vehicles", description = "Vehicle master data management")
@SecurityRequirement(name = "bearerAuth")
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR')")
    @Operation(summary = "Create a new vehicle")
    public ResponseEntity<ApiResponse<VehicleResponse>> create(
            @Valid @RequestBody VehicleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Vehicle created successfully",
                        vehicleService.create(request)));
    }

    @GetMapping
    @Operation(summary = "Get all active vehicles")
    public PaginationResponse<VehicleResponse> getAll( @RequestParam(defaultValue = "1") int page,
                                                       @RequestParam(defaultValue = "10") int size) {
        return vehicleService.getAll(page,size);
    }

    @GetMapping("/dropdown")
    @Operation(summary = "Get all active vehicles dropdown")
    public ResponseEntity<ApiResponse<List<VehicleResponse>>> getAllVehicles() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Vehicles retrieved successfully",
                        vehicleService.getAllVehicles()
                )
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vehicle by ID")
    public ResponseEntity<ApiResponse<VehicleResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Vehicle retrieved", vehicleService.getById(id)));
    }

    @GetMapping("/by-partner/{partnerId}")
    @Operation(summary = "Get all vehicles for a transport partner — used in loadout transport assignment")
    public ResponseEntity<ApiResponse<List<VehicleResponse>>> getByPartner(
            @PathVariable Long partnerId) {
        return ResponseEntity.ok(
                ApiResponse.success("Vehicles retrieved",
                        vehicleService.getByTransportPartner(partnerId)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR')")
    @Operation(summary = "Update vehicle")
    public ResponseEntity<ApiResponse<VehicleResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody VehicleRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Vehicle updated successfully",
                        vehicleService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete vehicle")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        vehicleService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Vehicle deactivated successfully"));
    }
}
