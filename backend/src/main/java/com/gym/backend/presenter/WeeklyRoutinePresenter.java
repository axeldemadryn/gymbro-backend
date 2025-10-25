package com.gym.backend.presenter;

import java.time.LocalDate;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gym.backend.Response;
import com.gym.backend.business.services.RoutineDayService;
import com.gym.backend.business.services.UserService;
import com.gym.backend.business.services.WeeklyRoutineService;
import com.gym.backend.model.User;
import com.gym.backend.model.WeeklyRoutine;

@RestController
@RequestMapping("/api/weekly-routines")
public class WeeklyRoutinePresenter {

    @Autowired
    private WeeklyRoutineService routineService;

    @Autowired
    private RoutineDayService routineDayService;

    @Autowired
    private UserService userService;

    // 🔹 GET: listar rutinas del usuario autenticado
    @GetMapping
    public ResponseEntity<Object> listarDelUsuario() {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");

        List<WeeklyRoutine> routines = routineService.findByUserId(user.getId());
        if (routines.isEmpty())
            return Response.notFound("No hay rutinas semanales para este usuario.");

        return Response.ok(routines);
    }

    // 🔹 GET: obtener rutina por ID
    @GetMapping("/id/{id}")
    public ResponseEntity<Object> obtenerPorId(@PathVariable long id) {
        WeeklyRoutine routine = routineService.findById(id);
        if (routine == null)
            return Response.notFound("No se encontró la rutina con ID " + id);

        // Seguridad: evitar que vea rutinas de otros
        User user = userService.getAuthenticatedUser();
        if (!routine.getUser().getId().equals(user.getId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tiene permiso para acceder a esta rutina.");

        return Response.ok(routine);
    }

    // 🔹 POST: crear nueva rutina semanal
    @PostMapping
    public ResponseEntity<Object> crear(@RequestBody WeeklyRoutine routine) {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");

        if (routine.getName() == null || routine.getName().isEmpty())
            return Response.dbError("La rutina no puede tener un nombre vacío.");

        routine.setUser(user);

        try {
            WeeklyRoutine created = routineService.save(routine);
            return Response.ok(created);
        } catch (DataIntegrityViolationException e) {
            return Response.dbError("Ya existe una rutina semanal con ese nombre.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.dbError(e.getMessage());
        }
    }

    // 🔹 PUT: actualizar rutina semanal
    @PutMapping
    public ResponseEntity<Object> actualizar(@RequestBody WeeklyRoutine routine) {
        if (routine.getId() == null || routine.getId() <= 0)
            return Response.dbError("La rutina tiene un ID inválido.");

        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");

        WeeklyRoutine existente = routineService.findById(routine.getId());
        if (existente == null)
            return Response.notFound("No se encontró la rutina con ID " + routine.getId());

        if (!existente.getUser().getId().equals(user.getId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tiene permiso para modificar esta rutina.");

        try {
            routine.setUser(user); // asegurar que sigue siendo del mismo usuario
            WeeklyRoutine updated = routineService.save(routine);
            return Response.ok(updated);
        } catch (DataIntegrityViolationException e) {
            return Response.dbError("Ya existe una rutina semanal con ese nombre.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.dbError(e.getMessage());
        }
    }

    // 🔹 DELETE: eliminar rutina semanal del usuario autenticado
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminar(@PathVariable long id) {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");

        WeeklyRoutine routine = routineService.findById(id);
        if (routine == null)
            return Response.notFound("No se encontró la rutina con ID " + id);

        if (!routine.getUser().getId().equals(user.getId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tiene permiso para eliminar esta rutina.");

        // Validación: existe al menos un RoutineDay asociado a esta rutina
        if (routineDayService.existsByWeeklyRoutineId(id)) {
            return Response.dbError("No se puede eliminar la rutina semanal porque tiene días de rutina asociados.");
        }

        routineService.delete(id);
        return Response.ok("Rutina semanal eliminada correctamente.");
    }

    // Buscar rutina entre fechas (solo del usuario autenticado)
    @GetMapping("/by-dates")
    public ResponseEntity<Object> findByDates(
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) {

        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");

        WeeklyRoutine routine = routineService.findByDatesAndUserId(startDate, endDate, user.getId());

        return (routine != null)
                ? Response.ok(routine)
                : Response.notFound("No se encontró una rutina semanal con esas fechas para el usuario.");
    }

    // Clonar rutina semanal
    @PostMapping("/clone")
    public ResponseEntity<Object> clonar(
            @RequestParam("startDate") LocalDate startDate,
            @RequestBody WeeklyRoutine routine) {
        if (routine.getId() == null || routine.getId() <= 0)
            return Response.dbError("La rutina tiene un ID inválido.");

        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");

        if (routine.getName() == null || routine.getName().isEmpty())
            return Response.dbError("La rutina no puede tener un nombre vacío.");

        WeeklyRoutine existente = routineService.findById(routine.getId());
        if (existente == null)
            return Response.notFound("No se encontró la rutina con ID " + routine.getId());

        if (!existente.getUser().getId().equals(user.getId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("No tiene permiso para modificar esta rutina.");

        try {
            return Response.ok(routineService.clone(routine, startDate));
        } catch (DataIntegrityViolationException e) {
            return Response.dbError("Ya existe una rutina semanal con ese nombre.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Response.dbError(e.getMessage());
        }
    }
}
