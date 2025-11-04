package com.gym.backend.business.services;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.backend.business.repositories.MaquinaRepository;
import com.gym.backend.business.repositories.RoutineDayRepository;
import com.gym.backend.business.repositories.SessionExerciseRepository;
import com.gym.backend.business.repositories.SessionRepository;
import com.gym.backend.dto.EjercicioDTO;
import com.gym.backend.dto.MaquinaDTO;
import com.gym.backend.dto.MusculoDTO;
import com.gym.backend.model.Maquina;
import com.gym.backend.model.RoutineDay;
import com.gym.backend.model.Session;
import com.gym.backend.model.SessionExercise;
import com.gym.backend.model.SessionStatus;
import com.gym.backend.model.User;
import com.gym.backend.model.WeeklyRoutine;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class SessionService {
    @Autowired
    private SessionRepository repository;

    @Autowired
    private SessionExerciseRepository sessionExerciseRepository;

    @Autowired
    private SessionExerciseService sessionExerciseService;

    @Autowired
    private RoutineDayRepository routineDayRepository;

    @Autowired
    private MaquinaRepository maquinaRepository;

    private final ZoneId zoneId;

    public SessionService(ZoneId zoneId) {
        this.zoneId = zoneId; // Spring inyecta el bean
    }

    public Session findById(long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Session> findByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    public Session findByNameAndUserId(String name, Long userId) {
        return repository.findByNameAndUserId(name, userId).orElse(null);
    }

    public List<Session> findAll() {
        List<Session> result = new ArrayList<>();
        repository.findAll().forEach(aSession -> result.add(aSession));
        return result;
    }

    @Transactional
    public Session save(Session aSession) {

        // 🔹 Si la sesión ya existe (actualización)
        if (aSession.getId() != null) {

            // Verificamos si está asociada a rutinas diarias
            List<RoutineDay> diasAsociados = routineDayRepository.findBySessionId(aSession.getId());

            if (!diasAsociados.isEmpty()) {

                boolean tieneDiaPasado = diasAsociados.stream().anyMatch(rd -> {
                    WeeklyRoutine weeklyRoutine = rd.getRoutine(); // se obtiene la rutina semanal

                    // Fecha real del RoutineDay (asumiendo que la semana empieza lunes)
                    LocalDate fechaDia = weeklyRoutine.getStartDate()
                            .plusDays(rd.getDay().getDia().getValue() - 1);

                    // Bloqueamos si ya pasó la fecha o el estado indica que se completó/no se
                    // completó
                    return fechaDia.isBefore(LocalDate.now(zoneId)) ||
                            rd.getStatus() == SessionStatus.COMPLETADA ||
                            rd.getStatus() == SessionStatus.NO_COMPLETADA;
                });

                if (tieneDiaPasado) {
                    throw new IllegalArgumentException(
                            "No se puede modificar esta sesión, porque está asociada a una rutina diaria que ya pasó o se completó.");
                }
            }
        }

        // 🔹 Mantener la relación bidireccional (sesión ↔ ejercicios)
        if (aSession.getSessionExercises() != null) {
            for (SessionExercise se : aSession.getSessionExercises()) {
                se.setSession(aSession);
            }
        }

        return repository.save(aSession);
    }

    @Transactional
    public void delete(long sessionId) {
        long count = routineDayRepository.countBySessionId(sessionId);
        if (count > 0) {
            throw new IllegalArgumentException(
                    "No se puede eliminar esta sesión, porque está asociada a rutinas diarias.");
        }

        repository.deleteById(sessionId);
    }

    public List<MaquinaDTO> obtenerMaquinasPorIdDeSesion(Long sessionId) {
        List<Maquina> maquinas = maquinaRepository.findMaquinasBySessionId(sessionId);

        // Mapear cada Maquina a MaquinaDTO, incluyendo la lista de MusculoDTO y,
        // opcionalmente, EjercicioDTO
        return maquinas.stream().map(m -> {
            MaquinaDTO dto = new MaquinaDTO();
            dto.setNombre(m.getNombre());
            dto.setTipoEquipo(m.getTipoEquipo() != null ? m.getTipoEquipo().name() : null);
            dto.setDescripcion(m.getDescripcion());
            dto.setImagen(m.getImagenUrl());

            // Musculos (requerido)
            if (m.getMusculos() != null) {
                dto.setMusculos(m.getMusculos().stream()
                        .map(mu -> new MusculoDTO(mu.getNombre()))
                        .toList());
            }

            // Ejercicios (opcional, pero añadimos la información básica si está cargada)
            if (m.getEjercicios() != null) {
                dto.setEjercicios(m.getEjercicios().stream().map(e -> {
                    EjercicioDTO ed = new EjercicioDTO();
                    ed.setNombre(e.getNombre());
                    ed.setTipo(e.getTipo() != null ? e.getTipo().name() : null);
                    ed.setDescripcion(e.getDescripcion());
                    ed.setVideoUrl(e.getVideoUrl());
                    // musculosPrincipales en el DTO de ejercicio: mapeamos si existen
                    if (e.getMusculos() != null) {
                        ed.setMusculosPrincipales(e.getMusculos().stream()
                                .map(mu -> new MusculoDTO(mu.getNombre()))
                                .toList());
                    }
                    return ed;
                }).toList());
            }

            return dto;
        }).toList();
    }

    @Transactional
    public Session clone(Session original, User user) {
        // Verificar que existe
        Session base = repository.findById(original.getId())
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la sesión a clonar."));

        // Verificar que la sesión pertenece al usuario autenticado
        if (!base.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("No tienes permiso para clonar esta sesión.");
        }

        // Crear nueva sesión
        Session nueva = new Session();
        nueva.setName(generarNombreUnico(base.getName(), user.getId()));
        nueva.setDescription(base.getDescription());
        nueva.setUser(user);

        // Guardar la nueva sesión
        Session guardada = save(nueva);

        // Clonar los ejercicios asociados
        List<SessionExercise> ejercicios = sessionExerciseRepository.findAllBySessionId(base.getId());
        for (SessionExercise se : ejercicios) {
            SessionExercise nuevo = new SessionExercise();
            nuevo.setSession(guardada);
            nuevo.setExercise(se.getExercise());
            nuevo.setReps(se.getReps());
            nuevo.setSets(se.getSets());
            sessionExerciseService.save(nuevo);
        }

        return guardada;
    }

    private String generarNombreUnico(String baseName, Long userId) {
        // Si el nombre ya contiene "(copia", lo limpiamos para evitar duplicar
        String baseLimpio = baseName.replaceAll("\\s*\\(copia(\\s*\\d+)?\\)", "").trim();

        String nuevoNombre = baseLimpio + " (copia)";
        int contador = 1;

        while (repository.existsByNameAndUserId(nuevoNombre, userId)) {
            contador++;
            nuevoNombre = baseLimpio + " (copia " + contador + ")";
        }

        return nuevoNombre;
    }

}
