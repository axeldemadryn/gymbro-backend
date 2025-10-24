package com.gym.backend.presenter;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gym.backend.Response;
import com.gym.backend.business.services.RoutineDayService;
import com.gym.backend.business.services.UserService;
import com.gym.backend.model.DiaDeSemana;
import com.gym.backend.model.RoutineDay;
import com.gym.backend.model.User;

@RestController
@RequestMapping("api/routine-days")
public class RoutineDayPresenter {

    @Autowired
    private RoutineDayService service;

    @Autowired
    private UserService userService;

    // 🔹 Obtener todas las days del user autenticado
    @GetMapping
    public ResponseEntity<Object> encontrarTodas() {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");

        return Response.ok(service.findAllByUser(user.getId()));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Object> encontrarById(@PathVariable("id") long id) {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");

        RoutineDay day = service.findById(id);
        if (day == null)
            return Response.notFound("No se encontró el día de rutina con ID " + id + ".");

        if (!day.getRoutine().getUser().getId().equals(user.getId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes acceder a este día de rutina");

        return Response.ok(day);
    }

    @GetMapping("/by-day-and-weekly-routine-dates")
    public ResponseEntity<Object> findByDayAndWeeklyRoutineDates(
            @RequestParam("day") DiaDeSemana day,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate) {
        try {
            User user = userService.getAuthenticatedUser();
            if (user == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");

            RoutineDay routineDay = service.findByDayAndWeeklyRoutineDatesAndUser(day, startDate, endDate,
                    user.getId());
            return routineDay != null
                    ? Response.ok(routineDay)
                    : Response.notFound("No se encontró el día de rutina en el día " + day
                            + " en la rutina semanal que dura de " + startDate + " a " + endDate + ".");
        } catch (Exception e) {
            return Response.error(e, e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<Object> actualizar(@RequestBody RoutineDay routineDay) {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");

        if (!routineDay.getRoutine().getUser().getId().equals(user.getId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes actualizar este día de rutina");

        if (routineDay.getId() <= 0)
            return Response.dbError("El día de rutina tiene un ID no positivo, y no debe.");

        if (routineDay.getDay() == null || routineDay.getDay().name().isEmpty())
            return Response.dbError("El día de rutina no puede tener un día vacío.");

        try {
            RoutineDay updated = service.save(routineDay);
            return (updated != null) ? Response.ok(updated)
                    : Response.dbError("No se pudo actualizar el día de rutina con ID " + routineDay.getId() + ".");
        } catch (DataIntegrityViolationException e) {
            return Response.dbError("Ya existe un día de rutina con esa combinación de rutina y día (y/o sesión).");
        }
    }

    @PutMapping("/completada")
    public ResponseEntity<Object> marcarCompletada(@RequestBody RoutineDay routineDay) {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");

        if (!routineDay.getRoutine().getUser().getId().equals(user.getId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes modificar este día de rutina");

        if (routineDay.getId() <= 0)
            return Response.dbError("El día de rutina tiene un ID no positivo, y no debe.");

        if (routineDay.getDay() == null || routineDay.getDay().name().isEmpty())
            return Response.dbError("El día de rutina no puede tener un día vacío.");

        try {
            RoutineDay updated = service.marcarCompletada(routineDay);
            return (updated != null) ? Response.ok(updated)
                    : Response.dbError("No se pudo actualizar el día de rutina con ID " + routineDay.getId() + ".");
        } catch (DataIntegrityViolationException e) {
            return Response.dbError("Ya existe un día de rutina con esa combinación de rutina y día (y/o sesión).");
        }
    }

    @PostMapping
    public ResponseEntity<Object> crear(@RequestBody RoutineDay day) {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");

        if (day.getRoutine() == null || day.getRoutine().getId() == null)
            return Response.dbError("Debe asignar una rutina válida.");

        if (!day.getRoutine().getUser().getId().equals(user.getId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes crear un día en esta rutina");

        if (day.getSession() == null || day.getSession().getId() == null)
            return Response.dbError("Debe asignar una sesión válida.");

        if (day.getDay() == null || day.getDay().name().isEmpty())
            return Response.dbError("El día de la semana es obligatorio.");

        try {
            RoutineDay created = service.save(day);
            return Response.ok(created);
        } catch (DataIntegrityViolationException e) {
            return Response.dbError("Ya existe un día de rutina con esa combinación de rutina y día (y/o sesión).");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminar(@PathVariable("id") long id) {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");

        RoutineDay day = service.findById(id);
        if (day == null)
            return Response.notFound("No se encontró el día de rutina con ID " + id + ".");

        if (!day.getRoutine().getUser().getId().equals(user.getId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes eliminar este día de rutina");

        service.delete(id);
        return Response.ok("El día de rutina con ID " + id + " fue eliminado.");
    }
}