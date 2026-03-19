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

    public List<Product> findOutOfStock() {
        return productRepository.findByStockQuantityEquals(0);
    }

    public List<ProductDTO> findOutOfStockDtos() {
        return findOutOfStock().stream().map(this::toDto).toList();
    }

    public ProductDTO toDto(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setBrand(product.getBrand());
        dto.setCategory(product.getCategory());
        dto.setPrice(product.getPrice() != null ? product.getPrice().doubleValue() : 0);
        dto.setQuantity(product.getStockQuantity());
        dto.setSupplierId(product.getSupplier() != null ? product.getSupplier().getId() : null);
        dto.setSupplierName(product.getSupplier() != null ? product.getSupplier().getName() : null);
        return dto;
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public Product save(ProductDTO productDTO) {
        Product product = mapDtoToProduct(productDTO);
        if (productDTO.getSupplierId() != null) {
            supplierRepository.findById(productDTO.getSupplierId())
                    .ifPresent(product::setSupplier);  // Only set if exists – no error
        }
        return productRepository.save(product);
    }

    public Product update(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (productDTO.getName() != null) {
            product.setName(productDTO.getName());
        }
        if (productDTO.getBrand() != null) {
            product.setBrand(productDTO.getBrand());
        }
        if (productDTO.getCategory() != null) {
            product.setCategory(productDTO.getCategory());
        }

        if (productDTO.getPrice() > 0) {
            product.setPrice(BigDecimal.valueOf(productDTO.getPrice()));
        }

        if (productDTO.getQuantity() >= 0) {
            product.setStockQuantity(productDTO.getQuantity());
        }

        if (productDTO.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(productDTO.getSupplierId())
                    .orElseThrow(() -> new RuntimeException("Supplier not found"));
            product.setSupplier(supplier);
        }

        return productRepository.save(product);
    }

    public Product updateStock(Long id, int newQuantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setStockQuantity(newQuantity);
        return productRepository.save(product);
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }

    private static Product mapDtoToProduct(ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setBrand(dto.getBrand());
        product.setCategory(dto.getCategory());
        product.setPrice(BigDecimal.valueOf(dto.getPrice()));
        product.setStockQuantity(dto.getQuantity());
        product.setLowStockThreshold(0);
        return product;
    }
}
