package com.gym.backend.presenter;

import java.time.LocalDate;

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

import com.gym.backend.business.services.RoutineDayService;
import com.gym.backend.business.services.SessionService;
import com.gym.backend.business.services.UserService;
import com.gym.backend.business.services.WeeklyRoutineService;
import com.gym.backend.model.DiaDeSemana;
import com.gym.backend.model.RoutineDay;
import com.gym.backend.model.Session;
import com.gym.backend.model.User;
import com.gym.backend.model.WeeklyRoutine;
import com.gym.backend.response.Response;

@RestController
@RequestMapping("api/routine-days")
public class RoutineDayPresenter {

    @Autowired
    private RoutineDayService service;

    @Autowired
    private UserService userService;

    @Autowired
    private WeeklyRoutineService weeklyRoutineService; // ✅ Agregar

    @Autowired
    private SessionService sessionService; // ✅ Agregar

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

        if (routineDay.getId() <= 0)
            return Response.dbError("El día de rutina tiene un ID no positivo, y no debe.");

        if (routineDay.getDay() == null || routineDay.getDay().name().isEmpty())
            return Response.dbError("El día de rutina no puede tener un día vacío.");

        // ✅ NUEVO: Recargar el RoutineDay existente desde BD
        RoutineDay existente = service.findById(routineDay.getId());
        if (existente == null)
            return Response.notFound("No se encontró el día de rutina con ID " + routineDay.getId());

        // ✅ Verificar ownership con el objeto cargado
        if (!existente.getRoutine().getUser().getId().equals(user.getId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes actualizar este día de rutina");

        // ✅ Si se está cambiando la rutina, validar
        if (routineDay.getRoutine() != null && routineDay.getRoutine().getId() != null) {
            WeeklyRoutine routine = weeklyRoutineService.findById(routineDay.getRoutine().getId());
            if (routine == null)
                return Response.notFound("No se encontró la rutina con ID " + routineDay.getRoutine().getId());
            if (!routine.getUser().getId().equals(user.getId()))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes usar esta rutina");
            routineDay.setRoutine(routine);
        } else {
            routineDay.setRoutine(existente.getRoutine());
        }

        // ✅ Si se está cambiando la sesión, validar
        if (routineDay.getSession() != null && routineDay.getSession().getId() != null) {
            Session session = sessionService.findById(routineDay.getSession().getId());
            if (session == null)
                return Response.notFound("No se encontró la sesión con ID " + routineDay.getSession().getId());
            if (!session.getUser().getId().equals(user.getId()))
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes usar esta sesión");
            routineDay.setSession(session);
        } else {
            routineDay.setSession(existente.getSession());
        }

        try {
            RoutineDay updated = service.update(routineDay);
            return (updated != null) ? Response.ok(updated)
                    : Response.dbError("No se pudo actualizar el día de rutina con ID " + routineDay.getId() + ".");
        } catch (DataIntegrityViolationException e) {
            return Response.dbError("Ya existe un día de rutina con esa combinación de rutina y día (y/o sesión).");
        } catch (IllegalArgumentException e) {
            return Response.dbError(e.getMessage());
        } catch (IllegalStateException e) {
            return Response.dbError(e.getMessage());
        } catch (RuntimeException e) {
            return Response.dbError(e.getMessage());
        }
    }

    @PutMapping("/completada")
    public ResponseEntity<Object> marcarCompletada(@RequestBody RoutineDay routineDay) {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");

        if (routineDay.getId() <= 0)
            return Response.dbError("El día de rutina tiene un ID no positivo, y no debe.");

        // ✅ NUEVO: Recargar el RoutineDay existente desde BD
        RoutineDay existente = service.findById(routineDay.getId());
        if (existente == null)
            return Response.notFound("No se encontró el día de rutina con ID " + routineDay.getId());

        // ✅ Verificar ownership con el objeto cargado
        if (!existente.getRoutine().getUser().getId().equals(user.getId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes modificar este día de rutina");

        try {
            RoutineDay updated = service.marcarCompletada(routineDay);
            return (updated != null) ? Response.ok(updated)
                    : Response.dbError("No se pudo actualizar el día de rutina con ID " + routineDay.getId() + ".");
        } catch (DataIntegrityViolationException e) {
            return Response.dbError("Ya existe un día de rutina con esa combinación de rutina y día (y/o sesión).");
        } catch (IllegalArgumentException e) {
            return Response.dbError(e.getMessage());
        } catch (IllegalStateException e) {
            return Response.dbError(e.getMessage());
        } catch (RuntimeException e) {
            return Response.dbError(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Object> crear(@RequestBody RoutineDay day) {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");

        if (day.getRoutine() == null || day.getRoutine().getId() == null)
            return Response.dbError("Debe asignar una rutina válida.");

        if (day.getSession() == null || day.getSession().getId() == null)
            return Response.dbError("Debe asignar una sesión válida.");

        if (day.getDay() == null || day.getDay().name().isEmpty())
            return Response.dbError("El día de la semana es obligatorio.");

        // ✅ NUEVO: Recargar la rutina completa desde la BD
        WeeklyRoutine routine = weeklyRoutineService.findById(day.getRoutine().getId());
        if (routine == null)
            return Response.notFound("No se encontró la rutina con ID " + day.getRoutine().getId());

        // ✅ NUEVO: Verificar que la rutina pertenece al usuario
        if (!routine.getUser().getId().equals(user.getId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes crear un día en esta rutina");

        // ✅ NUEVO: Recargar la sesión completa desde la BD
        Session session = sessionService.findById(day.getSession().getId());
        if (session == null)
            return Response.notFound("No se encontró la sesión con ID " + day.getSession().getId());

        // ✅ NUEVO: Verificar que la sesión pertenece al usuario
        if (!session.getUser().getId().equals(user.getId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No puedes usar esta sesión");

        // ✅ Asignar los objetos completos al RoutineDay
        day.setRoutine(routine);
        day.setSession(session);

        try {
            RoutineDay created = service.create(day);
            return Response.ok(created);
        } catch (DataIntegrityViolationException e) {
            return Response.dbError("Ya existe un día de rutina con esa combinación de rutina y día (y/o sesión).");
        } catch (IllegalArgumentException e) {
            return Response.dbError(e.getMessage());
        } catch (IllegalStateException e) {
            return Response.dbError(e.getMessage());
        } catch (RuntimeException e) {
            return Response.dbError(e.getMessage());
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