package com.gym.backend.presenter;

import java.time.LocalDate;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gym.backend.Response;
import com.gym.backend.business.services.RoutineDayService;
import com.gym.backend.model.DiaDeSemana;
import com.gym.backend.model.RoutineDay;

@RestController
@RequestMapping("api/routine-days")
public class RoutineDayPresenter {
    @Autowired
    private RoutineDayService service;

    @GetMapping
    public ResponseEntity<Object> encontrarTodas() {
        return Response.ok(service.findAll());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Object> encontrarById(@PathVariable("id") long id) {
        RoutineDay day = service.findById(id);
        return (day != null)
                ? Response.ok(day)
                : Response.notFound("No se encontró el día de rutina con ID " + id + ".");
    }

    @GetMapping("/by-day-and-weekly-routine-dates")
    public ResponseEntity<Object> findByDayAndWeeklyRoutineDates(
        @RequestParam("day") DiaDeSemana day,
        @RequestParam("startDate") LocalDate startDate,
        @RequestParam("endDate") LocalDate endDate
    ){
        try {
            RoutineDay routineDay = service.findByDayAndWeeklyRoutineDates(day, startDate, endDate);
            return routineDay != null
                ? Response.ok(routineDay)
                : Response.notFound("No se encontró el día de rutina en el día " + day + " en la rutina semanal que dura de " + startDate + " a " + endDate + ".");
        } catch (Exception e) {
            return Response.error(e, e.getMessage());
        }
    }

    @PutMapping
    public ResponseEntity<Object> actualizar(@RequestBody RoutineDay routineDay) {
        if (routineDay.getId() <= 0) {
            return Response.dbError("El día de rutina tiene un ID no positivo, y no debe.");
        }
        if (routineDay.getDay() == null || routineDay.getDay().name().isEmpty()) {
            return Response.dbError("El día de rutina no puede tener un día vacío.");
        }

        try {
            RoutineDay updated = service.save(routineDay);
            return (updated != null)
                    ? Response.ok(updated)
                    : Response.dbError("No se pudo actualizar el día de rutina con ID " + routineDay.getId() + ".");
        } catch (DataIntegrityViolationException e) {
            return Response.dbError("Ya existe un día de rutina con esa combinación de rutina y día (y/o sesión).");
        }
    }

    @PutMapping("/completada")
    public ResponseEntity<Object> marcarCompletada(@RequestBody RoutineDay routineDay){
        if (routineDay.getId() <= 0) {
            return Response.dbError("El día de rutina tiene un ID no positivo, y no debe.");
        }
        if (routineDay.getDay() == null || routineDay.getDay().name().isEmpty()) {
            return Response.dbError("El día de rutina no puede tener un día vacío.");
        }

        try {
            RoutineDay updated = service.marcarCompletada(routineDay);
            return (updated != null)
                    ? Response.ok(updated)
                    : Response.dbError("No se pudo actualizar el día de rutina con ID " + routineDay.getId() + ".");
        } catch (DataIntegrityViolationException e) {
            return Response.dbError("Ya existe un día de rutina con esa combinación de rutina y día (y/o sesión).");
        }
    }

    @PostMapping
    public ResponseEntity<Object> crear(@RequestBody RoutineDay day) {
        if (day.getDay() == null || day.getDay().name().isEmpty()){
         return Response.dbError("El día de la semana es obligatorio.");
        }
        
        if (day.getRoutine() == null || day.getRoutine().getId() == null) {
            return Response.dbError("Debe asignar una rutina válida.");
        }
        if (day.getSession() == null || day.getSession().getId() == null) {
            return Response.dbError("Debe asignar una sesión válida.");
        }
        try {
            RoutineDay created = service.save(day);
            return Response.ok(created);
        } catch (DataIntegrityViolationException e) {
            return Response.dbError("Ya existe un día de rutina con esa combinación de rutina y día (y/o sesión).");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminar(@PathVariable("id") long id) {
        service.delete(id);
        return Response.ok("El día de rutina con ID " + id + " fue eliminado.");
    }
}
