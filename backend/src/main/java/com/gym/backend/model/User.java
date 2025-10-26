package com.gym.backend.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El e-mail no puede estar vacío")
    @NotNull(message = "El e-mail no puede ser nulo")
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$", message = "Por favor, escriba un e-mail válido")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Column(nullable = false)
    private String password;

/*
    @NotBlank(message = "La contraseña no puede estar vacía")
@Pattern(
    regexp = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&+=!]).{8,}$",
    message = "La contraseña debe tener al menos 8 caracteres, incluir mayúscula, minúscula, número y carácter especial"
)
@Column(nullable = false)
private String password;
*/

    @Column(nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    private LocalDateTime lastLogout;

    private boolean activo;
}
