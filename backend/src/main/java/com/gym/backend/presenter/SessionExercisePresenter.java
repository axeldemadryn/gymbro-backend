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
import com.gym.backend.business.services.SessionExerciseService;
import com.gym.backend.model.SessionExercise;

@RestController
@RequestMapping("sessions_exercises")
public class SessionExercisePresenter {
    @Autowired
    SessionExerciseService service;

    @GetMapping
    public ResponseEntity<Object> encontrarTodas() {
        return Response.ok(service.findAll());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Object> encontrarById(@PathVariable("id") long id){
        SessionExercise sessionExercise = service.findById(id);
        return (sessionExercise != null)
            ? Response.ok(sessionExercise)
            : Response.notFound("No se encontró a la SessionExercise con ID " + id + ".");
    }

    @PutMapping
    public ResponseEntity<Object> actualizar(@RequestBody SessionExercise sessionExercise){
        if (sessionExercise.getId() <= 0){
            return Response.dbError("La SessionExercise tiene un ID no positivo, y no debe.");
        }
        if (sessionExercise.getSession() == null || sessionExercise.getExercise() == null){
            return Response.dbError("La SessionExercise no puede tener un nombre vacío.");
        }
        SessionExercise updated = service.save(sessionExercise);
        return (updated != null)
            ? Response.ok(updated)
            : Response.dbError("No se pudo actualizar a la SessionExercise con ID " + sessionExercise.getId() + ".");
    }

    @PostMapping
    public ResponseEntity<Object> crear(@RequestBody SessionExercise sessionExercise){
        if (sessionExercise.getId() != 0){
            return Response.dbError("La SessionExercise tiene un ID no nulo (autogenerado).");
        }
        if (sessionExercise.getSession() == null || sessionExercise.getExercise() == null){
            return Response.dbError("La SessionExercise no puede tener un nombre vacío.");
        }
        SessionExercise created = service.save(sessionExercise);
        return (created != null)
            ? Response.ok(created)
            : Response.dbError("No se pudo crear la SessionExercise que vincula a " + sessionExercise.getSession().getName()
                + " con " + sessionExercise.getExercise().getNombre() + ".");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminar(@PathVariable("id") long id) {
        service.delete(id);
        return Response.ok("La SessionExercise con ID " + id + " fue eliminada.");
    }
}
