package com.vlad.healthbeauty;

import com.vlad.healthbeauty.model.Role;
import com.vlad.healthbeauty.model.Supplier;
import com.vlad.healthbeauty.model.User;
import com.vlad.healthbeauty.repository.RoleRepository;
import com.vlad.healthbeauty.repository.SupplierRepository;
import com.vlad.healthbeauty.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@org.springframework.context.annotation.Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(RoleRepository roleRepo,
                               UserRepository userRepo,
                               SupplierRepository supplierRepo,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            Role adminRole = roleRepo.findByName("ROLE_ADMIN")
                    .orElseGet(() -> {
                        Role r = new Role();
                        r.setName("ROLE_ADMIN");
                        return roleRepo.save(r);
                    });

            Role staffRole = roleRepo.findByName("ROLE_STAFF")
                    .orElseGet(() -> {
                        Role r = new Role();
                        r.setName("ROLE_STAFF");
                        return roleRepo.save(r);
                    });

            if (userRepo.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRoles(Set.of(adminRole));
                userRepo.save(admin);
            }

            if (userRepo.findByUsername("staff").isEmpty()) {
                User staff = new User();
                staff.setUsername("staff");
                staff.setPassword(passwordEncoder.encode("staff123"));
                staff.setRoles(Set.of(staffRole));
                userRepo.save(staff);
            }

            if (supplierRepo.count() == 0) {
                Supplier s1 = new Supplier();
                s1.setName("L'Oreal Distributor");
                s1.setContactName("Maria Pop");
                s1.setEmail("sales@loreal.test");
                s1.setPhone("0712345678");
                supplierRepo.save(s1);

                Supplier s2 = new Supplier();
                s2.setName("Nivea Wholesale");
                s2.setContactName("Andrei Ionescu");
                s2.setEmail("orders@nivea.test");
                s2.setPhone("0722334455");
                supplierRepo.save(s2);
            }
        };
    }
}

