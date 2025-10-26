package com.gym.backend.presenter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gym.backend.business.services.EjercicioService;
import com.gym.backend.business.services.SessionExerciseService;
import com.gym.backend.business.services.SessionService;
import com.gym.backend.business.services.UserService;
import com.gym.backend.dto.SessionExerciseCreateDTO;
import com.gym.backend.model.Ejercicio;
import com.gym.backend.model.Session;
import com.gym.backend.model.SessionExercise;
import com.gym.backend.model.User;
import com.gym.backend.response.Response;

@RestController
@RequestMapping("api/sessions-exercises")
public class SessionExercisePresenter {

    @Autowired
    private SessionExerciseService service;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private EjercicioService ejercicioService;

    @Autowired
    private UserService userService;

    // 🔹 Obtener todos los ejercicios de sesión del usuario autenticado
    @GetMapping
    public ResponseEntity<Object> encontrarTodas() {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado.");

        return Response.ok(service.findAllByUserId(user.getId()));
    }

    // 🔹 Obtener una SessionExercise específica
    @GetMapping("/id/{id}")
    public ResponseEntity<Object> encontrarById(@PathVariable("id") long id) {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado.");

        SessionExercise sessionExercise = service.findById(id);
        if (sessionExercise == null)
            return Response.notFound("No se encontró la SessionExercise con ID " + id + ".");

        // Verificar ownership
        if (!sessionExercise.getSession().getUser().getId().equals(user.getId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para acceder a esta sesión.");

        return Response.ok(sessionExercise);
    }

    // 🔹 Crear nueva SessionExercise
    @PostMapping
    public ResponseEntity<Object> crear(@RequestBody SessionExerciseCreateDTO dto) {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado.");

        if (dto.getSessionId() == null || dto.getExerciseId() == null)
            return Response.dbError("La SessionExercise debe tener sesión y ejercicio asignados.");

        // Verificar que la sesión pertenece al usuario
        Session session = sessionService.findById(dto.getSessionId());
        if (session == null)
            return Response.notFound("No se encontró la sesión con ID " + dto.getSessionId() + ".");
        if (!session.getUser().getId().equals(user.getId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para esta sesión.");

        // Verificar que el ejercicio pertenece al usuario
        Ejercicio exercise = ejercicioService.obtenerPorId(dto.getExerciseId()).orElse(null);
        if (exercise == null)
            return Response.notFound("No se encontró el ejercicio con ID " + dto.getExerciseId() + ".");
        if (exercise.getUser() != null && !exercise.getUser().getId().equals(user.getId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para usar este ejercicio.");

        // Crear objeto SessionExercise
        SessionExercise se = new SessionExercise();
        se.setSets(dto.getSets());
        se.setReps(dto.getReps());
        se.setSession(session);
        se.setExercise(exercise);

        try {
            SessionExercise created = service.save(se);
            return Response.ok(created);
        } catch (DataIntegrityViolationException e) {
            return Response.dbError("Este ejercicio ya está asignado a la sesión.");
        } catch (IllegalStateException e) {
            return Response.dbError(e.getMessage());
        }
    }

    // 🔹 Actualizar una SessionExercise
    @PutMapping
    public ResponseEntity<Object> actualizar(@RequestBody SessionExercise sessionExercise) {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado.");

        if (sessionExercise.getId() == null || sessionExercise.getId() <= 0)
            return Response.dbError("La SessionExercise tiene un ID inválido.");
        if (sessionExercise.getSession() == null || sessionExercise.getExercise() == null)
            return Response.dbError("La SessionExercise debe tener sesión y ejercicio.");

        // Verificar ownership de la sesión
        Session dbSession = sessionService.findById(sessionExercise.getSession().getId());
        if (dbSession == null)
            return Response.notFound("No se encontró la sesión con ID " + sessionExercise.getSession().getId() + ".");
        if (!dbSession.getUser().getId().equals(user.getId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para esta sesión.");

        // Verificar ownership del ejercicio
        Ejercicio exercise = ejercicioService.obtenerPorId(sessionExercise.getExercise().getId()).orElse(null);
        if (exercise == null)
            return Response
                    .notFound("No se encontró el ejercicio con ID " + sessionExercise.getExercise().getId() + ".");
        if (exercise.getUser() != null && !exercise.getUser().getId().equals(user.getId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para usar este ejercicio.");

        sessionExercise.setExercise(exercise);

        try {
            SessionExercise updated = service.save(sessionExercise);
            return Response.ok(updated);
        } catch (DataIntegrityViolationException e) {
            return Response.dbError("Este ejercicio ya está asignado a la sesión.");
        } catch (IllegalStateException e) {
            return Response.dbError(e.getMessage());
        }
    }

    // 🔹 Eliminar una SessionExercise
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminar(@PathVariable("id") long id) {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado.");

        SessionExercise se = service.findById(id);
        if (se == null)
            return Response.notFound("No se encontró la SessionExercise con ID " + id + ".");

        if (!se.getSession().getUser().getId().equals(user.getId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para eliminar este recurso.");

        service.delete(id);
        return Response.ok("La SessionExercise con ID " + id + " fue eliminada correctamente.");
    }
}
