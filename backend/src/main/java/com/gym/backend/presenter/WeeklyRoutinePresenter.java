package com.gym.backend.presenter;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.gym.backend.business.services.WeeklyRoutineService;
import com.gym.backend.model.WeeklyRoutine;

@RestController
@RequestMapping("/api/weekly-routines")
public class WeeklyRoutinePresenter {

    @Autowired
    private WeeklyRoutineService service;

    @GetMapping
    public ResponseEntity<Object> encontrarTodas() {
        return Response.ok(service.findAll());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Object> encontrarById(@PathVariable("id") long id) {
        WeeklyRoutine routine = service.findById(id);
        return (routine != null)
            ? Response.ok(routine)
            : Response.notFound("No se encontró la rutina semanal con ID " + id + ".");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Object> encontrarByUserId(@PathVariable("userId") long userId) {
        return Response.ok(service.findByUserId(userId));
    }

    @PutMapping
    public ResponseEntity<Object> actualizar(@RequestBody WeeklyRoutine routine) {
        if (routine.getId() <= 0) {
            return Response.dbError("La rutina semanal tiene un ID no positivo, y no debe.");
        }
        if (routine.getName() == null || routine.getName().isEmpty()) {
            return Response.dbError("La rutina semanal no puede tener un nombre vacío.");
        }
        WeeklyRoutine updated = service.save(routine);
        return (updated != null)
            ? Response.ok(updated)
            : Response.dbError("No se pudo actualizar la rutina semanal con ID " + routine.getId() + ".");
    }

    @PostMapping
    public ResponseEntity<Object> crear(@RequestBody WeeklyRoutine routine){
        if (routine.getName() == null || routine.getName().isEmpty()){
            return Response.dbError("La rutina no puede tener un nombre vacío.");
        }
        WeeklyRoutine created = service.save(routine);
        return Response.ok(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminar(@PathVariable("id") long id) {
        service.delete(id);
        return Response.ok("La rutina semanal con ID " + id + " fue eliminada.");
    }
    
}
