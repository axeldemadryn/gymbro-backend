package com.gym.backend.business.services;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gym.backend.business.repositories.EjercicioRepository;
import com.gym.backend.business.repositories.MusculoRepository;
import com.gym.backend.business.repositories.SessionExerciseRepository;
import com.gym.backend.model.Ejercicio;
import com.gym.backend.model.Musculo;
import com.gym.backend.model.TipoEjercicio;

@Service
public class EjercicioService {

    @Autowired
    private EjercicioRepository ejercicioRepository;

    @Autowired
    private MusculoRepository musculoRepository;

    @Autowired
    private SessionExerciseRepository sessionExerciseRepository;

    @Transactional
    public Ejercicio guardar(Ejercicio ejercicio) {
        if (ejercicio.getMusculos() == null || ejercicio.getMusculos().isEmpty()) {
            throw new IllegalArgumentException("El ejercicio debe tener al menos un músculo asociado.");
        }

        // Evitar músculos duplicados
        Set<Musculo> musculosUnicos = new HashSet<>();
        for (Musculo m : ejercicio.getMusculos()) {
            Musculo musculoDB = musculoRepository.findById(m.getId())
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró el músculo con ID: " + m.getId()));
            if (!musculosUnicos.add(musculoDB)) {
                throw new IllegalArgumentException("El ejercicio contiene músculos repetidos.");
            }
        }

        return ejercicioRepository.save(ejercicio);
    }

    // Obtener todos los ejercicios con máquinas y músculos
    public List<Ejercicio> obtenerTodos() {
        return ejercicioRepository.findAllConDetalles();
    }

    public List<Ejercicio> obtenerPorUserId(Long userId) {
        return ejercicioRepository.findByUserId(userId);
    }

    // Obtener ejercicio por ID con detalles
    public Optional<Ejercicio> obtenerPorId(Long id) {
        return ejercicioRepository.findByIdConDetalles(id);
    }

    // Obtener ejercicio por ID con detalles
    public Ejercicio obtenerPorNombre(String nombre) {
        return ejercicioRepository.findByNombre(nombre).orElse(null);
    }

    // Buscar ejercicios por tipo
    public List<Ejercicio> buscarPorTipo(TipoEjercicio tipo) {
        return ejercicioRepository.findByTipo(tipo);
    }

    // Borrar un ejercicio por ID
    @Transactional
    public void eliminar(Long id) {
        boolean estaEnUso = sessionExerciseRepository.existsByExerciseId(id);

        if (estaEnUso) {
            throw new IllegalStateException(
                    "No se puede eliminar este ejercicio porque está siendo usado en una sesión.");
        }
        ejercicioRepository.deleteById(id);
    }

    public boolean existeEjercicioGlobalPorNombre(String nombre) {
        return ejercicioRepository.existsByNombreAndUserIsNull(nombre); // ejercicio global
    }

    public Ejercicio buscarPorNombreYUserOGlobal(String nombre, Long userId) {
        return ejercicioRepository.findByNombreAndUserIdOrGlobal(nombre, userId).orElse(null);
    }

    public List<Ejercicio> buscarPorNombre(String nombre, Long userId) {
        return ejercicioRepository.findByNombreContainingIgnoreCaseAndUserIsNullOrUserId(nombre, userId);
    }

    public List<Ejercicio> obtenerGlobalesYDelUsuario(Long userId) {
        return ejercicioRepository.findByUserIsNullOrUserId(userId);
    }

    public List<Ejercicio> buscarPorTipoYUserOGlobal(TipoEjercicio tipo, Long userId) {
        return ejercicioRepository.findByTipoAndUserOrGlobal(tipo, userId);
    }

}
