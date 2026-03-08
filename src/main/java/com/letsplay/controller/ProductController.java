package com.letsplay.controller;

import com.letsplay.dto.ProductDto;
import com.letsplay.model.Product;
import com.letsplay.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // Public endpoint
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Product>> getProductsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(productService.getProductsByUserId(userId));
    }

    // Authenticated endpoints
    @PostMapping
    public ResponseEntity<Product> createProduct(
            @Valid @RequestBody ProductDto.CreateRequest request,
            Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.createProduct(request, auth.getName()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable String id,
            @Valid @RequestBody ProductDto.UpdateRequest request,
            Authentication auth) {
        boolean isAdmin = isAdmin(auth);
        return ResponseEntity.ok(productService.updateProduct(id, request, auth.getName(), isAdmin));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable String id,
            Authentication auth) {
        boolean isAdmin = isAdmin(auth);
        productService.deleteProduct(id, auth.getName(), isAdmin);
        return ResponseEntity.noContent().build();
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(a -> a.equals("ROLE_ADMIN"));
    }
}
