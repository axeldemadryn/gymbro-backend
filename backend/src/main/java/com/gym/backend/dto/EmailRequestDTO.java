package com.gym.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class EmailRequestDTO {

    @NotNull(message = "El e-mail no puede ser nulo.")
    @NotBlank(message = "El e-mail no puede estar vacío.")
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$", message = "Por favor, escriba un e-mail válido.")
    private String email;

    // Getters y Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
