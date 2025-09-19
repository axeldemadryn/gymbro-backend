package com.gym.backend.presenter;

import com.gym.backend.Response;
import com.gym.backend.business.EjercicioService;
import com.gym.backend.model.Ejercicio;
import com.gym.backend.model.TipoEjercicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ejercicios")
public class EjercicioPresenter {

    @Autowired
    private EjercicioService ejercicioService;

    // Crear un nuevo ejercicio
    @PostMapping
    public ResponseEntity<Object> crearEjercicio(@RequestBody Ejercicio ejercicio) {
        if (ejercicio.getNombre() == null || ejercicio.getNombre().isEmpty()) {
            return Response.dbError("El nombre del ejercicio no puede estar vacío.");
        }
        try {
            Ejercicio guardado = ejercicioService.guardar(ejercicio);
            return (guardado != null)
                    ? Response.ok(guardado)
                    : Response.dbError("No se pudo crear el ejercicio.");
        } catch (DataIntegrityViolationException d) { // Nombre duplicado
            return Response.dbError("Error. Ya existe un ejercicio con nombre " + ejercicio.getNombre() + ".");
        }
    }

    // Obtener todos los ejercicios
    @GetMapping
    public ResponseEntity<Object> listarEjercicios() {
        List<Ejercicio> ejercicios = ejercicioService.obtenerTodos();
        if (ejercicios.isEmpty()) {
            return Response.notFound("No hay ejercicios registrados");
        }
        return Response.ok(ejercicios);
    }

    // Obtener ejercicio por ID
    @GetMapping("/{id}")
    public ResponseEntity<Object> obtenerPorId(@PathVariable Long id) {
        Optional<Ejercicio> ejercicio = ejercicioService.obtenerPorId(id);
        return ejercicio.map(Response::ok)
                .orElseGet(() -> Response.notFound("Ejercicio no encontrado"));
    }

    // Buscar ejercicios por nombre
    @GetMapping("/buscar")
    public ResponseEntity<Object> buscarPorNombre(@RequestParam String nombre) {
        List<Ejercicio> resultados = ejercicioService.buscarPorNombre(nombre);
        if (resultados.isEmpty()) {
            return Response.notFound("No se encontraron ejercicios con ese nombre");
        }
        return Response.ok(resultados);
    }

    // Filtrar por tipo
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<Object> buscarPorTipo(@PathVariable TipoEjercicio tipo) {
        List<Ejercicio> resultados = ejercicioService.buscarPorTipo(tipo);
        if (resultados.isEmpty()) {
            return Response.notFound("No se encontraron ejercicios de tipo " + tipo);
        }
        return Response.ok(resultados);
    }

    // Filtrar por personalizados
    @GetMapping("/personalizado/{esPersonalizado}")
    public ResponseEntity<Object> buscarPorPersonalizado(@PathVariable boolean esPersonalizado) {
        List<Ejercicio> resultados = ejercicioService.buscarPorPersonalizado(esPersonalizado);
        if (resultados.isEmpty()) {
            return Response.notFound("No se encontraron ejercicios con ese filtro");
        }
        return Response.ok(resultados);
    }

    // Eliminar ejercicio por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminar(@PathVariable Long id) {
        try {
            ejercicioService.eliminar(id);
            return Response.ok(null, "Ejercicio eliminado correctamente");
        } catch (Exception e) {
            return Response.dbError("Error al eliminar el ejercicio");
        }
    }
}
