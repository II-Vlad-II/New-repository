package com.vlad.healthbeauty.repository;

import com.vlad.healthbeauty.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
