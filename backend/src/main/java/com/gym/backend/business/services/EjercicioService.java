package com.gym.backend.business.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gym.backend.business.repositories.EjercicioRepository;
import com.gym.backend.model.Ejercicio;
import com.gym.backend.model.TipoEjercicio;

@Service
public class EjercicioService {

    @Autowired
    private EjercicioRepository ejercicioRepository;

    // Guardar un ejercicio con validación de nombre único
    @Transactional
    public Ejercicio guardar(Ejercicio ejercicio) {
        if (ejercicioRepository.existsByNombreIgnoreCase(ejercicio.getNombre())) {
            throw new IllegalArgumentException(
                    "Ya existe un ejercicio con el mismo nombre: " + ejercicio.getNombre());
        }
        return ejercicioRepository.save(ejercicio);
    }

    // Obtener todos los ejercicios con máquinas y músculos
    public List<Ejercicio> obtenerTodos() {
        return ejercicioRepository.findAllConDetalles();
    }

    // Obtener ejercicio por ID con detalles
    public Optional<Ejercicio> obtenerPorId(Long id) {
        return ejercicioRepository.findByIdConDetalles(id);
    }

    // Buscar ejercicios por tipo
    public List<Ejercicio> buscarPorTipo(TipoEjercicio tipo) {
        return ejercicioRepository.findByTipo(tipo);
    }

    // Borrar un ejercicio por ID
    @Transactional
    public void eliminar(Long id) {
        ejercicioRepository.deleteById(id);
    }

    public List<Ejercicio> buscarPorNombre(String nombre) {
        return ejercicioRepository.findByNombreContainingIgnoreCase(nombre);
    }

}
