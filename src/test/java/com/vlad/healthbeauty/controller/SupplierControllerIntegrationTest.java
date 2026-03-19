package com.vlad.healthbeauty.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vlad.healthbeauty.model.Role;
import com.vlad.healthbeauty.model.Supplier;
import com.vlad.healthbeauty.model.User;
import com.vlad.healthbeauty.repository.RoleRepository;
import com.vlad.healthbeauty.repository.SupplierRepository;
import com.vlad.healthbeauty.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SupplierControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        supplierRepository.deleteAll();
        ensureUserWithRole("admin", "admin2026", "ROLE_ADMIN");
        ensureUserWithRole("staff", "staff2026", "ROLE_STAFF");
        ensureUserWithRole("viewer", "viewer2026", "ROLE_USER");
    }

    @Test
    void getSuppliers_withoutAuth_shouldReturn401() throws Exception {
        mockMvc.perform(get("/api/suppliers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getSuppliers_withStaff_shouldReturn200() throws Exception {
        seedSupplier("Main Supplier");

        mockMvc.perform(get("/api/suppliers")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("staff", "staff2026")))
                .andExpect(status().isOk());
    }

    @Test
    void getSuppliers_withViewer_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/suppliers")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("viewer", "viewer2026")))
                .andExpect(status().isForbidden());
    }

    @Test
    void createSupplier_withStaff_shouldReturn403() throws Exception {
        mockMvc.perform(post("/api/suppliers")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("staff", "staff2026"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validSupplierPayload("Blocked Supplier")))
                .andExpect(status().isForbidden());
    }

    @Test
    void createSupplier_withAdmin_shouldReturn201() throws Exception {
        mockMvc.perform(post("/api/suppliers")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "admin2026"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validSupplierPayload("Approved Supplier")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Approved Supplier"));
    }

    @Test
    void updateSupplier_withAdmin_shouldReturn200() throws Exception {
        Supplier supplier = seedSupplier("Old Supplier");
        String payload = validSupplierPayload("Updated Supplier");

        mockMvc.perform(put("/api/suppliers/{id}", supplier.getId())
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "admin2026"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Supplier"));
    }

    @Test
    void deleteSupplier_withAdmin_shouldReturn204() throws Exception {
        Supplier supplier = seedSupplier("Delete Supplier");

        mockMvc.perform(delete("/api/suppliers/{id}", supplier.getId())
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "admin2026")))
                .andExpect(status().isNoContent());
    }

    @Test
    void createSupplier_withBlankName_shouldReturn400() throws Exception {
        String invalidPayload = objectMapper.writeValueAsString(Map.of(
                "name", "   ",
                "contactName", "Contact",
                "email", "mail@test.com",
                "phone", "0712345678"
        ));

        mockMvc.perform(post("/api/suppliers")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "admin2026"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.name").value("Supplier name is required"));
    }

    @Test
    void createSupplier_withInvalidEmail_shouldReturn400() throws Exception {
        String invalidPayload = objectMapper.writeValueAsString(Map.of(
                "name", "Valid Name",
                "contactName", "Contact",
                "email", "invalid-email",
                "phone", "0712345678"
        ));

        mockMvc.perform(post("/api/suppliers")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "admin2026"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.email").value("Email format is invalid"));
    }

    private void ensureUserWithRole(String username, String rawPassword, String roleName) {
        Role role = roleRepository.findByName(roleName).orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName(roleName);
            return roleRepository.save(newRole);
        });

        User user = userRepository.findByUsername(username).orElseGet(User::new);
        user.setUsername(username);
        user.setEnabled(true);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRoles(Set.of(role));
        userRepository.save(user);
    }

    private Supplier seedSupplier(String name) {
        Supplier supplier = new Supplier();
        supplier.setName(name);
        supplier.setContactName("Contact");
        supplier.setEmail("contact@test.com");
        supplier.setPhone("123456789");
        return supplierRepository.save(supplier);
    }

    private String validSupplierPayload(String name) throws Exception {
        return objectMapper.writeValueAsString(Map.of(
                "name", name,
                "contactName", "Contact",
                "email", "mail@test.com",
                "phone", "0712345678"
        ));
    }
}
