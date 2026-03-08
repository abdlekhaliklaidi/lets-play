package com.letsplay.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class UserDto {

    @Data
    public static class UpdateRequest {
        @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
        private String name;

        @Email(message = "Email must be valid")
        private String email;

        @Size(min = 8, message = "Password must be at least 8 characters")
        private String password;
    }
}
