package com.gym.backend.presenter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gym.backend.Response;
import com.gym.backend.business.services.MusculoService;
import com.gym.backend.model.Musculo;

import java.util.List;

@RestController
@RequestMapping("api/musculos")
public class MusculoPresenter {

    @Autowired
    private MusculoService service;

    @GetMapping
    public ResponseEntity<Object> encontrarTodos() {
        List<Musculo> musculos = service.findAll();
        return Response.ok(musculos);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Object> encontrarById(@PathVariable("id") Long id) {
        Musculo musculo = service.findById(id);
        return (musculo != null)
                ? Response.ok(musculo)
                : Response.notFound("No se encontró el músculo con ID " + id + ".");
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Object> encontrarPorNombre(@PathVariable("nombre") String nombre) {
        Musculo musculo = service.findByNombre(nombre);
        return (musculo != null)
                ? Response.ok(musculo)
                : Response.notFound("No se encontró el músculo con nombre " + nombre + ".");
    }

    @PostMapping
    public ResponseEntity<Object> crear(@RequestBody Musculo musculo) {
        if (musculo.getNombre() == null || musculo.getNombre().isEmpty()) {
            return Response.dbError("El nombre del músculo no puede estar vacío.");
        }
        try {
            Musculo creado = service.save(musculo);
            return Response.ok(creado);
        } catch (DataIntegrityViolationException e) { // nombre duplicado
            return Response.dbError("Error. Ya existe un músculo con nombre " + musculo.getNombre() + ".");
        }
    }

    @PutMapping
    public ResponseEntity<Object> actualizar(@RequestBody Musculo musculo) {
        if (musculo.getId() == null || musculo.getId() <= 0) {
            return Response.dbError("El ID del músculo es inválido.");
        }
        if (musculo.getNombre() == null || musculo.getNombre().isEmpty()) {
            return Response.dbError("El nombre del músculo no puede estar vacío.");
        }
        try {
            Musculo actualizado = service.save(musculo);
            return Response.ok(actualizado);
        } catch (DataIntegrityViolationException e) {
            return Response.dbError("Error. Ya existe un músculo con nombre " + musculo.getNombre() + ".");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminar(@PathVariable("id") Long id) {
        service.delete(id);
        return Response.ok("El músculo con ID " + id + " fue eliminado.");
    }
}
