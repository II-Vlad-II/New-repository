package com.vlad.healthbeauty.controller;

import com.vlad.healthbeauty.model.AuditLog;
import com.vlad.healthbeauty.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get recent audit logs", description = "Access: ROLE_ADMIN. Returns latest 200 critical action logs.")
    public List<AuditLog> getRecentLogs() {
        return auditLogService.getRecentLogs();
    }
}
