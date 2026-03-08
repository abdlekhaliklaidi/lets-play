package com.letsplay.service;

import com.letsplay.dto.ProductDto;
import com.letsplay.exception.ResourceNotFoundException;
import com.letsplay.exception.UnauthorizedException;
import com.letsplay.model.Product;
import com.letsplay.model.User;
import com.letsplay.repository.ProductRepository;
import com.letsplay.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public List<Product> getProductsByUserId(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return productRepository.findByUserId(userId);
    }

    public Product createProduct(ProductDto.CreateRequest request, String ownerEmail) {
        User user = userRepository.findByEmail(ownerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .userId(user.getId())
                .build();

        return productRepository.save(product);
    }

    public Product updateProduct(String id, ProductDto.UpdateRequest request, String requesterEmail, boolean isAdmin) {
        Product product = getProductById(id);
        assertOwnerOrAdmin(product, requesterEmail, isAdmin, "update");

        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }

        return productRepository.save(product);
    }

    public void deleteProduct(String id, String requesterEmail, boolean isAdmin) {
        Product product = getProductById(id);
        assertOwnerOrAdmin(product, requesterEmail, isAdmin, "delete");
        productRepository.deleteById(id);
    }

    private void assertOwnerOrAdmin(Product product, String requesterEmail, boolean isAdmin, String action) {
        if (isAdmin) return;

        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!product.getUserId().equals(requester.getId())) {
            throw new UnauthorizedException("You are not authorized to " + action + " this product");
        }
    }
}
