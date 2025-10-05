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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gym.backend.Response;
import com.gym.backend.business.services.SessionService;
import com.gym.backend.model.Session;

@RestController
@RequestMapping("api/sessions")
public class SessionPresenter {
    @Autowired
    private SessionService service;

    @GetMapping
    public ResponseEntity<Object> encontrarTodas() {
        return Response.ok(service.findAll());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Object> encontrarById(@PathVariable("id") long id) {
        Session aSession = service.findById(id);
        return (aSession != null)
                ? Response.ok(aSession)
                : Response.notFound("No se encontró a la sesión con ID " + id + ".");
    }

    @GetMapping("/by-name")
    public ResponseEntity<Object> encontrarByName(@RequestParam String name) {
        Session aSession = service.findByName(name);
        return (aSession != null)
                ? Response.ok(aSession)
                : Response.notFound("No se encontró a la sesión con nombre " + name + ".");

    }

    @PutMapping
    public ResponseEntity<Object> actualizar(@RequestBody Session aSession) {
        if (aSession.getId() <= 0) {
            return Response.dbError("La sesión tiene un ID no positivo, y no debe.");
        }
        if (aSession.getName() == null || aSession.getName().isEmpty()) {
            return Response.dbError("La sesión no puede tener un nombre vacío.");
        }

        try {
            Session updated = service.save(aSession);
            return (updated != null)
                    ? Response.ok(updated)
                    : Response.dbError("No se pudo actualizar a la sesión con ID " + aSession.getId() + ".");
        } catch (DataIntegrityViolationException d) { // Nombre duplicado
            return Response.dbError("Error. Ya existe una sesión con nombre " + aSession.getName() + ".");
        }
    }

    @PostMapping
    public ResponseEntity<Object> crear(@RequestBody Session aSession) {
        // No verificamos el ID, se autogenera
        if (aSession.getName() == null || aSession.getName().isEmpty()) {
            return Response.dbError("La sesión no puede tener un nombre vacío.");
        }
        try {
            // Guardamos la sesión, aquí se genera el ID automáticamente
            Session created = service.save(aSession);

            return (created != null)
                    ? Response.ok(created)
                    : Response.dbError("No se pudo crear la sesión con nombre " + aSession.getName() + ".");
        } catch (DataIntegrityViolationException d) { // Nombre duplicado
            return Response.dbError("Error. Ya existe una sesión con nombre " + aSession.getName() + ".");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminar(@PathVariable("id") long id) {
        service.delete(id);
        return Response.ok("La sesión con ID " + id + " fue eliminada.");
    }
}
