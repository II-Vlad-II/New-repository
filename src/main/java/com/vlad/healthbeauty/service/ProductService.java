package com.vlad.healthbeauty.service;

import com.vlad.healthbeauty.dto.ProductDTO;
import com.vlad.healthbeauty.model.Product;
import com.vlad.healthbeauty.model.Supplier;
import com.vlad.healthbeauty.repository.ProductRepository;
import com.vlad.healthbeauty.repository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;

    public ProductService(ProductRepository productRepository, SupplierRepository supplierRepository) {
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> findLowStock(int threshold) {
        return productRepository.findByStockQuantityLessThan(threshold);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Product save(ProductDTO productDTO) {
        Product product = mapDtoToProduct(productDTO);
        if (productDTO.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(productDTO.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));
            product.setSupplier(supplier);
        }
        return productRepository.save(product);
    }

    public Product update(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setName(productDTO.getName());
        product.setPrice(BigDecimal.valueOf(productDTO.getPrice()));
        product.setStockQuantity(productDTO.getQuantity());
        if (productDTO.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(productDTO.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));
            product.setSupplier(supplier);
        } else {
            product.setSupplier(null);
        }
        return productRepository.save(product);
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    private static Product mapDtoToProduct(ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(BigDecimal.valueOf(dto.getPrice()));
        product.setStockQuantity(dto.getQuantity());
        product.setLowStockThreshold(0);
        return product;
    }
}
