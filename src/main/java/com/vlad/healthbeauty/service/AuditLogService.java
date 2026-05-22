package com.vlad.healthbeauty.service;

import com.vlad.healthbeauty.model.AuditLog;
import com.vlad.healthbeauty.model.User;
import com.vlad.healthbeauty.repository.AuditLogRepository;
import com.vlad.healthbeauty.repository.UserRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditLogService(AuditLogRepository auditLogRepository, UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    public void log(String action, String entityType, Long entityId, String details) {
        AuditLog log = new AuditLog();
        log.setUsername(resolveCurrentUsername());
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDetails(details);
        auditLogRepository.save(log);
    }

    public List<AuditLog> getRecentLogs() {
        return auditLogRepository.findTop200ByOrderByCreatedAtDesc();
    }

    private String resolveCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return "anonymous";
        }
        String username = authentication.getName();
        
        // Look up the user to get their full name
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && user.get().getFullName() != null) {
            return user.get().getFullName() + " (" + username + ")";
        }
        
        return username;
    }
}
