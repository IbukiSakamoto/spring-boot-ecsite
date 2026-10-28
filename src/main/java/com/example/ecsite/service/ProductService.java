package com.example.ecsite.service;

import com.example.ecsite.model.Product;
import com.example.ecsite.repository.ProductRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getProducts(String keyword, String sortType) {
        Sort sort = switch (sortType != null ? sortType : "") {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "price");
            default -> Sort.by(Sort.Direction.ASC, "id");
        };

        if (keyword != null && !keyword.trim().isEmpty()) {
            return productRepository.searchByKeyword(keyword.trim(), sort);
        }
        return productRepository.findAll(sort);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional
    public Product save(Product product) {
        return productRepository.save(product);
    }
}
