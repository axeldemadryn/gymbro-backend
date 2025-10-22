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
import com.gym.backend.business.services.UserService;
import com.gym.backend.model.Session;
import com.gym.backend.model.User;

@RestController
@RequestMapping("api/sessions")
public class SessionPresenter {
    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserService userService;

    // 🔹 GET: todas las sesiones (opcional: filtrar por usuario)
    @GetMapping
    public ResponseEntity<Object> encontrarTodas(
            @RequestParam(value = "userId", required = false) Long userId) {

        if (userId != null) {
            return Response.ok(sessionService.findByUserId(userId));
        }
        return Response.ok(sessionService.findAll());
    }

    // 🔹 GET: por ID
    @GetMapping("/id/{id}")
    public ResponseEntity<Object> encontrarById(@PathVariable("id") long id) {
        Session session = sessionService.findById(id);
        return (session != null)
                ? Response.ok(session)
                : Response.notFound("No se encontró la sesión con ID " + id + ".");
    }

    // 🔹 GET: por nombre (solo si no repetís nombres globalmente)
    @GetMapping("/by-name")
    public ResponseEntity<Object> encontrarByName(@RequestParam String name,
            @RequestParam(required = false) Long userId) {
        Session session = (userId != null)
                ? sessionService.findByNameAndUserId(name, userId)
                : sessionService.findByName(name);

        return (session != null)
                ? Response.ok(session)
                : Response.notFound("No se encontró la sesión con nombre " + name + ".");
    }

    // 🔹 POST: crear sesión (requiere ID de usuario)
    @PostMapping
    public ResponseEntity<Object> crear(@RequestBody Session session) {
        if (session.getName() == null || session.getName().isEmpty()) {
            return Response.dbError("La sesión no puede tener un nombre vacío.");
        }

        if (session.getUser() == null || session.getUser().getId() == null) {
            return Response.dbError("Debe especificarse el usuario dueño de la sesión.");
        }

        User user = userService.findById(session.getUser().getId());
        if (user == null) {
            return Response.notFound("Usuario no encontrado con ID " + session.getUser().getId() + ".");
        }

        session.setUser(user);

        try {
            Session created = sessionService.save(session);
            return Response.ok(created);
        } catch (DataIntegrityViolationException e) {
            return Response.dbError("Ya existe una sesión con el mismo nombre para este usuario.");
        }
    }

    // 🔹 PUT: actualizar sesión
    @PutMapping
    public ResponseEntity<Object> actualizar(@RequestBody Session session) {
        if (session.getId() == null || session.getId() <= 0) {
            return Response.dbError("La sesión tiene un ID no válido.");
        }
        if (session.getName() == null || session.getName().isEmpty()) {
            return Response.dbError("La sesión no puede tener un nombre vacío.");
        }

        try {
            Session updated = sessionService.save(session);
            return Response.ok(updated);
        } catch (DataIntegrityViolationException e) {
            return Response.dbError("Ya existe otra sesión con ese nombre para el mismo usuario.");
        }
    }

    // 🔹 DELETE: eliminar sesión
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminar(@PathVariable("id") long id) {
        sessionService.delete(id);
        return Response.ok("La sesión con ID " + id + " fue eliminada.");
    }
}
