package com.vlad.healthbeauty.repository;

import com.vlad.healthbeauty.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStockQuantityLessThan(int stockQuantity);
}
