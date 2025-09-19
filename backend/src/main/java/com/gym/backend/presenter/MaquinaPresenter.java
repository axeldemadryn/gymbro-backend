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
import org.springframework.web.bind.annotation.RestController;

import com.gym.backend.Response;
import com.gym.backend.business.services.MaquinaService;
import com.gym.backend.model.Maquina;

@RestController
@RequestMapping("maquinas")
public class MaquinaPresenter {
    @Autowired
    private MaquinaService service;

    @GetMapping
    public ResponseEntity<Object> encontrarTodas() {
        return Response.ok(service.findAll());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Object> encontrarById(@PathVariable("id") int id) {
        Maquina unaMaquina = service.findById(id);
        return (unaMaquina != null)
                ? Response.ok(unaMaquina)
                : Response.notFound("No se encontró a la máquina con ID " + id + ".");
    }

    @GetMapping("/nombre/{nombre}")
    public ResponseEntity<Object> encontrarPorNombre(@PathVariable("nombre") String nombre) {
        Maquina unaMaquina = service.findByNombre(nombre);
        return (unaMaquina != null)
                ? Response.ok(unaMaquina)
                : Response.notFound("No se encontró a la máquina con nombre " + nombre + ".");
    }

    @PutMapping
    public ResponseEntity<Object> actualizar(@RequestBody Maquina unaMaquina) {
        if (unaMaquina.getId() <= 0) {
            return Response.dbError("El ID de la máquina es inválido.");
        }
        if (unaMaquina.getNombre() == null || unaMaquina.getNombre().isEmpty()) {
            return Response.dbError("El nombre de la máquina no puede estar vacío.");
        }
        try {
            Maquina maquinaActualizada = service.save(unaMaquina);
            return (maquinaActualizada != null)
                    ? Response.ok(maquinaActualizada)
                    : Response.dbError("No se pudo actualizar a la máquina con ID " + unaMaquina.getId() + ".");
        } catch (DataIntegrityViolationException d) { // Nombre duplicado
            return Response.dbError("Error. Ya existe una máquina con nombre " + unaMaquina.getNombre() + ".");
        }
    }

    @PostMapping
    public ResponseEntity<Object> crear(@RequestBody Maquina unaMaquina) {
        if (unaMaquina.getId() != 0) {
            return Response
                    .dbError("Error. Al parecer, se introdujo una máquina con ID no nulo (que es autogenerado).");
        }
        if (unaMaquina.getNombre() == null || unaMaquina.getNombre().isEmpty()) {
            return Response.dbError("El nombre de la máquina no puede estar vacío.");
        }
        try {
            Maquina maquinaCreada = service.save(unaMaquina);
            return (maquinaCreada != null)
                    ? Response.ok(maquinaCreada)
                    : Response.dbError("No se pudo crear la máquina.");
        } catch (DataIntegrityViolationException d) { // Nombre duplicado
            return Response.dbError("Error. Ya existe una máquina con nombre " + unaMaquina.getNombre() + ".");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminar(@PathVariable("id") int id) {
        service.delete(id);
        return Response.ok("La máquina con ID " + id + " fue eliminada.");
    }

}