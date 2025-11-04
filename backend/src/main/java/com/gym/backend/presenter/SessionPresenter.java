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

import com.gym.backend.business.services.RecomendacionService;
import com.gym.backend.business.services.SessionService;
import com.gym.backend.business.services.UserService;
import com.gym.backend.model.Session;
import com.gym.backend.model.User;
import com.gym.backend.response.Response;

@RestController
@RequestMapping("api/sessions")
public class SessionPresenter {

    @Autowired
    private SessionService sessionService;

    @Autowired
    private UserService userService;

    @Autowired
    private RecomendacionService recomendacionService;

    // 🔹 GET: listar todas las sesiones del usuario autenticado
    @GetMapping
    public ResponseEntity<Object> encontrarTodas() {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");

        return Response.ok(sessionService.findByUserId(user.getId()));
    }

    // 🔹 GET: obtener una sesión específica (del usuario autenticado)
    @GetMapping("/id/{id}")
    public ResponseEntity<Object> encontrarById(@PathVariable("id") long id) {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");

        Session session = sessionService.findById(id);
        if (session == null)
            return Response.notFound("No se encontró la sesión con ID " + id + ".");

        if (!session.getUser().getId().equals(user.getId()))
            return Response.dbError("No puede acceder a una sesión que no le pertenece.");

        return Response.ok(session);
    }

    @GetMapping("maquinas-asociadas-a/{id}")
    public ResponseEntity<Object> encontrarMaquinasPorIdDeSesion(@PathVariable long id) {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");

        Session existente = sessionService.findById(id);
        if (existente == null)
            return Response.notFound("No se encontró la sesión con ID " + id + ".");

        if (!existente.getUser().getId().equals(user.getId()))
            return Response.dbError("No puede modificar una sesión que no le pertenece.");

        try {
            return Response.ok(recomendacionService.obtenerRecomendacionesPorSesion(id));
        } catch (Exception e) {
            return Response.error(e, e.getMessage());
        }
    }

    // 🔹 POST: crear sesión para el usuario autenticado
    @PostMapping
    public ResponseEntity<Object> crear(@RequestBody Session session) {
        if (session.getName() == null || session.getName().isEmpty()) {
            return Response.dbError("La sesión no puede tener un nombre vacío.");
        }

        User user = userService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");

        }

        session.setUser(user);

        try {
            Session created = sessionService.save(session);
            return Response.ok(created);
        } catch (DataIntegrityViolationException e) {
            return Response.dbError("Ya existe una sesión con el mismo nombre para este usuario.");
        }
    }

    // 🔹 PUT
    @PutMapping
    public ResponseEntity<Object> actualizar(@RequestBody Session session) {
        if (session.getId() == null || session.getId() <= 0)
            return Response.dbError("La sesión tiene un ID no válido.");

        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");

        Session existente = sessionService.findById(session.getId());
        if (existente == null)
            return Response.notFound("No se encontró la sesión con ID " + session.getId() + ".");

        if (!existente.getUser().getId().equals(user.getId()))
            return Response.dbError("No puede modificar una sesión que no le pertenece.");

        // mantener usuario original
        session.setUser(existente.getUser());

        try {
            Session updated = sessionService.save(session);
            return Response.ok(updated);
        } catch (DataIntegrityViolationException e) {
            return Response.dbError("Ya existe otra sesión con ese nombre para el mismo usuario.");
        }
    }

    // 🔹 DELETE: eliminar sesión del usuario autenticado
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminar(@PathVariable("id") long id) {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");

        Session session = sessionService.findById(id);
        if (session == null)
            return Response.notFound("No se encontró la sesión con ID " + id + ".");

        if (!session.getUser().getId().equals(user.getId()))
            return Response.dbError("No puede eliminar una sesión que no le pertenece.");

        sessionService.delete(id);
        return Response.ok("La sesión con ID " + id + " fue eliminada correctamente.");
    }

    @PostMapping("/clone")
    public ResponseEntity<Object> clonar(@RequestBody Session original) {
        User user = userService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }

        if (original.getId() == null) {
            return Response.dbError("Debe proporcionar el ID de la sesión a clonar.");
        }

        try {
            Session clonada = sessionService.clone(original, user);
            return Response.ok(clonada);
        } catch (IllegalArgumentException e) {
            return Response.dbError(e.getMessage());
        } catch (Exception e) {
            return Response.dbError("Error al clonar la sesión: " + e.getMessage());
        }
    }

}
