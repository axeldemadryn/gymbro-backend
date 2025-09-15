package com.gym.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "musculos")
public class Musculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre; // Ejemplo: "Pectoral mayor", "Dorsal ancho", "Bíceps"

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
