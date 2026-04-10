package com.alba.master.controller;

import com.alba.master.dto.request.TransportPartnerRequest;
import com.alba.master.dto.response.ApiResponse;
import com.alba.master.dto.response.PaginationResponse;
import com.alba.master.dto.response.TransportPartnerResponse;
import com.alba.master.service.TransportPartnerService;
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

@RestController
@RequestMapping("/api/transport-partners")
@RequiredArgsConstructor
@Tag(name = "Transport Partners", description = "Transport partner master data management")
@SecurityRequirement(name = "bearerAuth")
public class TransportPartnerController {

    private final TransportPartnerService transportPartnerService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR','ACCOUNTS')")
    @Operation(summary = "Create a new transport partner")
    public ResponseEntity<ApiResponse<TransportPartnerResponse>> create(
            @Valid @RequestBody TransportPartnerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Transport partner created successfully",
                        transportPartnerService.create(request)));
    }

    @GetMapping
    @Operation(summary = "Get all active transport partners")
    public PaginationResponse<TransportPartnerResponse> getAll( @RequestParam(defaultValue = "1") int page,
                                                                @RequestParam(defaultValue = "10") int size) {
        return transportPartnerService.getAll(page,size);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transport partner by ID")
    public ResponseEntity<ApiResponse<TransportPartnerResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Transport partner retrieved",
                        transportPartnerService.getById(id)));
    }

    @GetMapping("/no-portal")
    @Operation(summary = "Get partners without portal — system uses this to trigger auto email")
    public ResponseEntity<ApiResponse<List<TransportPartnerResponse>>> getPartnersWithoutPortal() {
        return ResponseEntity.ok(
                ApiResponse.success("Partners without portal retrieved",
                        transportPartnerService.getPartnersWithoutPortal()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','SUPERVISOR')")
    @Operation(summary = "Update transport partner")
    public ResponseEntity<ApiResponse<TransportPartnerResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody TransportPartnerRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Transport partner updated successfully",
                        transportPartnerService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft delete transport partner")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        transportPartnerService.delete(id);
        return ResponseEntity.ok(
                ApiResponse.success("Transport partner deactivated successfully"));
    }
}
