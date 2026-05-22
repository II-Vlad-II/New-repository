package com.vlad.healthbeauty.config;

import com.vlad.healthbeauty.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Static/front-end
                        .requestMatchers("/", "/index.html", "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()

                        // Public APIs
                        .requestMatchers(HttpMethod.GET, "/api/products", "/api/products/low-stock").authenticated()
                        .requestMatchers("/api/images/**").permitAll()
                        .requestMatchers("/api/swagger-ui.html", "/api/swagger-ui/**", "/swagger-ui/**", "/api/v3/api-docs/**").permitAll()

                        // Product operations
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasAnyRole("ADMIN", "MANAGER", "SUPERVISOR")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAnyRole("ADMIN", "MANAGER", "SUPERVISOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasAnyRole("ADMIN", "MANAGER")

                        // Supplier management (Admin and Manager only)
                        .requestMatchers("/api/suppliers/**").hasAnyRole("ADMIN", "MANAGER")

                        // User registration (Admin only)
                        .requestMatchers("/api/auth/register").hasRole("ADMIN")

                        // Audit logs (Admin and Manager only)
                        .requestMatchers("/api/audit/**").hasAnyRole("ADMIN", "MANAGER")

                        // Everything else
                        .anyRequest().authenticated()
                )
                .httpBasic(httpBasic -> httpBasic
                        .realmName("Inventory")
                        .authenticationEntryPoint((request, response, authException) ->
                                response.setStatus(HttpStatus.UNAUTHORIZED.value()))
                )
                .userDetailsService(userDetailsService);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
