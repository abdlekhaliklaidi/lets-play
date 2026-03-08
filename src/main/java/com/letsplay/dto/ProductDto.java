package com.letsplay.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class ProductDto {

    @Data
    public static class CreateRequest {
        @NotBlank(message = "Product name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        private String name;

        @Size(max = 500, message = "Description must not exceed 500 characters")
        private String description;

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        private Double price;
    }

    @Data
    public static class UpdateRequest {
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        private String name;

        @Size(max = 500, message = "Description must not exceed 500 characters")
        private String description;

        @Positive(message = "Price must be positive")
        private Double price;
    }
}
