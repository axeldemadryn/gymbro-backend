package com.gym.backend.presenter;

import java.util.List;
import java.util.Optional;

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

import com.gym.backend.business.services.EjercicioService;
import com.gym.backend.business.services.UserService;
import com.gym.backend.model.Ejercicio;
import com.gym.backend.model.TipoEjercicio;
import com.gym.backend.model.User;
import com.gym.backend.response.Response;

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

        // Asignamos usuario
        ejercicio.setUser(user);

        // 🔹 Verificar que no exista otro ejercicio global con ese nombre
        boolean existeGlobal = ejercicioService.existeEjercicioGlobalPorNombre(ejercicio.getNombre());
        if (existeGlobal) {
            return Response.dbError("Ya existe un ejercicio global con ese nombre.");
        }

        try {
            Ejercicio guardado = ejercicioService.guardar(ejercicio);
            return Response.ok(guardado);
        } catch (DataIntegrityViolationException e) {
            return Response.dbError("Ya existe un ejercicio con ese nombre para este usuario.");
        } catch (IllegalArgumentException e) {
            return Response.dbError(e.getMessage());
        }
    }

    // ✅ OPCIÓN 1: ID en el BODY (igual que Session)
    @PutMapping // ← SIN /{id}
    public ResponseEntity<Object> editarEjercicio(@RequestBody Ejercicio ejercicioActualizado) {
        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");

        // ✅ Validar que el ID venga en el body
        if (ejercicioActualizado.getId() == null || ejercicioActualizado.getId() <= 0)
            return Response.dbError("El ejercicio tiene un ID no válido.");

        Ejercicio existente = ejercicioService.obtenerPorId(ejercicioActualizado.getId()).orElse(null);
        if (existente == null)
            return Response.notFound("No se encontró el ejercicio con ID " + ejercicioActualizado.getId());

        // No permitir editar ejercicios globales o de otro usuario
        if (existente.getUser() == null || !existente.getUser().getId().equals(user.getId()))
            return Response.dbError("No puede editar un ejercicio que no le pertenece.");

        // Verificar que no intente renombrar con un nombre de un ejercicio global
        boolean existeGlobal = ejercicioService.existeEjercicioGlobalPorNombre(ejercicioActualizado.getNombre());
        if (existeGlobal && !existente.getNombre().equals(ejercicioActualizado.getNombre()))
            return Response.dbError("Ya existe un ejercicio global con ese nombre.");

        // ✅ Actualizar campos
        existente.setNombre(ejercicioActualizado.getNombre());
        existente.setDescripcion(ejercicioActualizado.getDescripcion());
        existente.setTipo(ejercicioActualizado.getTipo());
        existente.setVideoUrl(ejercicioActualizado.getVideoUrl());

        // Actualizar músculos si vienen
        if (ejercicioActualizado.getMusculos() != null && !ejercicioActualizado.getMusculos().isEmpty()) {
            existente.setMusculos(ejercicioActualizado.getMusculos());
        }

        try {
            Ejercicio actualizado = ejercicioService.guardar(ejercicioActualizado);
            return Response.ok(actualizado);
        } catch (DataIntegrityViolationException e) {
            return Response.dbError("Ya existe otro ejercicio con ese nombre para este usuario.");
        } catch (IllegalArgumentException e) {
            return Response.dbError(e.getMessage());
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

    // 🔹 GET: todos los ejercicios (globales + personalizados, solo del usuario)
    @GetMapping
    public ResponseEntity<Object> listarEjercicios() {
        User user = userService.getAuthenticatedUser();
        Long userId = (user != null) ? user.getId() : null;

        List<Ejercicio> ejercicios = ejercicioService.obtenerGlobalesYDelUsuario(userId);
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

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Object> obtenerPorNombre(@PathVariable String nombre) {
        try {
            User user = userService.getAuthenticatedUser();
            Long userId = (user != null) ? user.getId() : null;

            Ejercicio ejercicio = ejercicioService.buscarPorNombreYUserOGlobal(nombre, userId);

            if (ejercicio == null)
                return Response.notFound("No se encontraron ejercicios con el nombre: " + nombre);

            return Response.ok(ejercicio);
        } catch (Exception e) {
            return Response.error(e, "Algo impidió el retorno de los ejercicios. Detalle: " + e.getMessage());
        }
    }

    // 🔹 GET: buscar o filtar (contiene o no la palabra) ejercicios por nombre
    // (solo globales + del usuario
    // autenticado)
    @GetMapping("/buscar")
    public ResponseEntity<Object> buscarPorNombre(@RequestParam String nombre) {
        User user = userService.getAuthenticatedUser();
        Long userId = (user != null) ? user.getId() : null;

        List<Ejercicio> resultados = ejercicioService.buscarPorNombre(nombre, userId);
        if (resultados.isEmpty())
            return Response.notFound("No se encontraron ejercicios con ese nombre.");

        return Response.ok(resultados);
    }

    // 🔹 GET: filtrar por tipo
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<Object> buscarPorTipo(@PathVariable TipoEjercicio tipo) {
        User user = userService.getAuthenticatedUser();
        Long userId = (user != null) ? user.getId() : null;

        // 🔹 Buscar solo ejercicios globales + del usuario autenticado
        List<Ejercicio> resultados = ejercicioService.buscarPorTipoYUserOGlobal(tipo, userId);

        if (resultados.isEmpty())
            return Response.notFound("No se encontraron ejercicios de tipo " + tipo);

        return Response.ok(resultados);
    }

}
