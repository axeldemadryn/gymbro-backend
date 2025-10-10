package com.gym.backend.presenter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gym.backend.Response;
import com.gym.backend.business.services.SessionExerciseService;
import com.gym.backend.dto.SessionExerciseCreateDTO;
import com.gym.backend.model.Ejercicio;
import com.gym.backend.model.Session;
import com.gym.backend.model.SessionExercise;

@RestController
@RequestMapping("api/sessions-exercises")
public class SessionExercisePresenter {
    @Autowired
    private SessionExerciseService service;

    @GetMapping
    public ResponseEntity<Object> encontrarTodas() {
        return Response.ok(service.findAll());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Object> encontrarById(@PathVariable("id") long id) {
        SessionExercise sessionExercise = service.findById(id);
        return (sessionExercise != null)
                ? Response.ok(sessionExercise)
                : Response.notFound("No se encontró a la SessionExercise con ID " + id + ".");
    }

    @PutMapping
    public ResponseEntity<Object> actualizar(@RequestBody SessionExercise sessionExercise) {
        if (sessionExercise.getId() <= 0) {
            return Response.dbError("La SessionExercise tiene un ID no positivo, y no debe.");
        }
        if (sessionExercise.getSession() == null || sessionExercise.getExercise() == null) {
            return Response.dbError("La SessionExercise no puede tener un nombre vacío.");
        }

        try {
            SessionExercise updated = service.save(sessionExercise);
            return (updated != null)
                    ? Response.ok(updated)
                    : Response.dbError(
                            "No se pudo actualizar a la SessionExercise con ID " + sessionExercise.getId() + ".");
        } catch (DataIntegrityViolationException e) {
            return Response.dbError("Este ejercicio ya está asignado a la sesión.");

        }
    }

    @PostMapping
    public ResponseEntity<Object> crear(@RequestBody SessionExerciseCreateDTO dto) {
        // Validación
        if (dto.getSessionId() == null || dto.getExerciseId() == null) {
            return Response.dbError("La SessionExercise debe tener sesión y ejercicio asignados.");
        }

        // Crear SessionExercise a partir del DTO
        SessionExercise se = new SessionExercise();
        se.setSets(dto.getSets());
        se.setReps(dto.getReps());

        Session session = new Session();
        session.setId(dto.getSessionId());
        se.setSession(session);

        Ejercicio exercise = new Ejercicio();
        exercise.setId(dto.getExerciseId());
        se.setExercise(exercise);

        try {
            SessionExercise created = service.save(se);
            return Response.ok(created);
        } catch (DataIntegrityViolationException e) {
            return Response.dbError("Este ejercicio ya está asignado a la sesión.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminar(@PathVariable("id") long id) {
        service.delete(id);
        return Response.ok("La SessionExercise con ID " + id + " fue eliminada.");
    }
}
