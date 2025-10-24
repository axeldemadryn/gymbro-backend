package com.gym.backend.presenter;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gym.backend.Response;
import com.gym.backend.business.services.EjercicioService;
import com.gym.backend.business.services.UserService;
import com.gym.backend.model.Ejercicio;
import com.gym.backend.model.TipoEjercicio;
import com.gym.backend.model.User;

@RestController
@RequestMapping("/api/ejercicios")
public class EjercicioPresenter {

    @Autowired
    private EjercicioService ejercicioService;

    @Autowired
    private UserService userService;

    // 🔹 GET: Ejercicios del usuario autenticado
    @GetMapping("/mis-ejercicios")
    public ResponseEntity<Object> obtenerMisEjercicios() {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");

        List<Ejercicio> ejercicios = ejercicioService.obtenerPorUserId(user.getId());
        if (ejercicios.isEmpty())
            return Response.notFound("No hay ejercicios personalizados para este usuario.");

        return Response.ok(ejercicios);
    }

    // 🔹 POST: crear ejercicio personalizado
    @PostMapping("/crear-usuario")
    public ResponseEntity<Object> crearParaUsuario(@RequestBody Ejercicio ejercicio) {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");

        if (ejercicio.getNombre() == null || ejercicio.getNombre().isEmpty())
            return Response.dbError("El nombre del ejercicio no puede estar vacío.");

        ejercicio.setUser(user);

        try {
            Ejercicio guardado = ejercicioService.guardar(ejercicio);
            return Response.ok(guardado);
        } catch (DataIntegrityViolationException e) {
            return Response.dbError("Ya existe un ejercicio con ese nombre para este usuario.");
        }
    }

    // 🔹 PUT: editar ejercicio personalizado
    @PutMapping("/{id}")
    public ResponseEntity<Object> editarEjercicio(@RequestBody Ejercicio ejercicioActualizado) {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");

        Ejercicio existente = ejercicioService.obtenerPorId(ejercicioActualizado.getId()).orElse(null);
        if (existente == null)
            return Response.notFound("No se encontró el ejercicio con ID " + ejercicioActualizado.getId());

        // No permitir editar ejercicios globales o de otro usuario
        if (existente.getUser() == null || !existente.getUser().getId().equals(user.getId()))
            return Response.dbError("No puede editar un ejercicio que no le pertenece.");

        existente.setNombre(ejercicioActualizado.getNombre());
        existente.setDescripcion(ejercicioActualizado.getDescripcion());
        existente.setTipo(ejercicioActualizado.getTipo());
        existente.setVideoUrl(ejercicioActualizado.getVideoUrl());

        try {
            Ejercicio actualizado = ejercicioService.guardar(existente);
            return Response.ok(actualizado);
        } catch (DataIntegrityViolationException e) {
            return Response.dbError("Ya existe otro ejercicio con ese nombre para este usuario.");
        }
    }

    // 🔹 DELETE: eliminar ejercicio personalizado
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminar(@PathVariable Long id) {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");

        Ejercicio ejercicio = ejercicioService.obtenerPorId(id).orElse(null);
        if (ejercicio == null)
            return Response.notFound("No se encontró el ejercicio con ID " + id);

        // Solo eliminar si pertenece al usuario y es personalizado
        if (ejercicio.getUser() == null || !ejercicio.getUser().getId().equals(user.getId()))
            return Response.dbError("No puede eliminar un ejercicio global o que no le pertenece.");

        ejercicioService.eliminar(id);
        return Response.ok("Ejercicio personalizado eliminado correctamente.");
    }

    // 🔹 GET: todos los ejercicios (globales + personalizados)
    @GetMapping
    public ResponseEntity<Object> listarEjercicios() {
        List<Ejercicio> ejercicios = ejercicioService.obtenerTodos();
        if (ejercicios.isEmpty())
            return Response.notFound("No hay ejercicios registrados");
        return Response.ok(ejercicios);
    }

    // 🔹 GET: ejercicio por ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> obtenerPorId(@PathVariable Long id) {
        Optional<Ejercicio> ejercicio = ejercicioService.obtenerPorId(id);
        return ejercicio.map(Response::ok)
                .orElseGet(() -> Response.notFound("Ejercicio no encontrado"));
    }

    // 🔹 GET: buscar ejercicios por nombre
    @GetMapping("/buscar")
    public ResponseEntity<Object> buscarPorNombre(@RequestParam String nombre) {
        List<Ejercicio> resultados = ejercicioService.buscarPorNombre(nombre);
        if (resultados.isEmpty())
            return Response.notFound("No se encontraron ejercicios con ese nombre");
        return Response.ok(resultados);
    }

    // 🔹 GET: filtrar por tipo
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<Object> buscarPorTipo(@PathVariable TipoEjercicio tipo) {
        List<Ejercicio> resultados = ejercicioService.buscarPorTipo(tipo);
        if (resultados.isEmpty())
            return Response.notFound("No se encontraron ejercicios de tipo " + tipo);
        return Response.ok(resultados);
    }
}
