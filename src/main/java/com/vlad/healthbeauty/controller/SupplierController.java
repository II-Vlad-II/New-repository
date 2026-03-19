package com.vlad.healthbeauty.controller;

import com.vlad.healthbeauty.model.Supplier;
import com.vlad.healthbeauty.service.AuditLogService;
import com.vlad.healthbeauty.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;
    private final AuditLogService auditLogService;

    public SupplierController(SupplierService supplierService, AuditLogService auditLogService) {
        this.supplierService = supplierService;
        this.auditLogService = auditLogService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Get all suppliers", description = "Access: ROLE_ADMIN or ROLE_STAFF")
    public ResponseEntity<List<Supplier>> findAll() {
        return ResponseEntity.ok(supplierService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    @Operation(summary = "Get supplier by id", description = "Access: ROLE_ADMIN or ROLE_STAFF")
    public ResponseEntity<Supplier> findById(@PathVariable Long id) {
        return supplierService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create supplier", description = "Access: ROLE_ADMIN")
    public ResponseEntity<Supplier> create(@Valid @RequestBody Supplier supplier) {
        Supplier saved = supplierService.save(supplier);
        auditLogService.log(
                "CREATE",
                "SUPPLIER",
                saved.getId(),
                "Created supplier: " + saved.getName()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update supplier", description = "Access: ROLE_ADMIN")
    public ResponseEntity<Supplier> update(@PathVariable Long id, @Valid @RequestBody Supplier supplier) {
        if (supplierService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        supplier.setId(id);
        Supplier updated = supplierService.save(supplier);
        auditLogService.log(
                "UPDATE",
                "SUPPLIER",
                updated.getId(),
                "Updated supplier: " + updated.getName()
        );
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete supplier", description = "Access: ROLE_ADMIN")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        var existing = supplierService.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        supplierService.deleteById(id);
        auditLogService.log(
                "DELETE",
                "SUPPLIER",
                id,
                "Deleted supplier: " + existing.get().getName()
        );
        return ResponseEntity.noContent().build();
    }
}
