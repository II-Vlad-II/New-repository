package com.vlad.healthbeauty.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Value("${unsplash.access-key:}")
    private String unsplashAccessKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping
    @Operation(summary = "Proxy product image", description = "Access: Public. Redirects to Unsplash or fallback image.")
    public ResponseEntity<Void> proxyImage(@RequestParam("query") String query) {
        // If no Unsplash key configured, fall back to picsum directly
        if (unsplashAccessKey == null || unsplashAccessKey.isBlank()) {
            String fallback = "https://picsum.photos/seed/" + query + "/300/200";
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(fallback))
                    .build();
        }

        try {
            String apiUrl = "https://api.unsplash.com/photos/random?orientation=landscape&content_filter=high&query="
                    + query + "&client_id=" + unsplashAccessKey;

            Map<?, ?> body = restTemplate.getForObject(apiUrl, Map.class);
            if (body != null && body.get("urls") instanceof Map<?, ?> urlsMap) {
                Object url = urlsMap.get("small");
                if (url != null) {
                    return ResponseEntity.status(HttpStatus.FOUND)
                            .location(URI.create(url.toString()))
                            .build();
                }
            }
        } catch (Exception ignored) {
        }

        // Final fallback if Unsplash fails
        String fallback = "https://picsum.photos/seed/" + query + "/300/200";
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(fallback))
                .build();
    }
}

