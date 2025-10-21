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
import com.gym.backend.business.services.HistorialReconocimientoService;
import com.gym.backend.model.HistorialReconocimiento;

@RestController
@RequestMapping("api/historial-reconocimientos")
public class HistorialReconocimientoPresenter {
    @Autowired
    private HistorialReconocimientoService service;

    @GetMapping
    public ResponseEntity<Object> encontrarTodos(){
        return Response.ok(service.findAll());
    }

    @GetMapping("/por-usuario/{id}")
    public ResponseEntity<Object> encontrarPorIdDeUsuario(@PathVariable("id") long id){
        return Response.ok(service.findAllByUserId(id));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Object> encontrarPorId(@PathVariable("id") long id){
        return Response.ok(service.findById(id));
    }

    @PutMapping
    public ResponseEntity<Object> actualizar(@RequestBody HistorialReconocimiento historial){
        if(historial.getId() <= 0)
            return Response.dbError("El historial tiene ID no positivo: " + historial.getId() + ". No pudo actualizarse.");
        if(historial.getUsuario() == null)
            return Response.dbError("El usuario no puede ser nulo.");
        try {
            HistorialReconocimiento historialActualizado = service.save(historial);
            return historialActualizado != null
                ? Response.ok(historialActualizado)
                : Response.dbError("No se pudo actualizar el historial.");
        } catch (DataIntegrityViolationException e) {
            return Response.error(e, "No se pudo actualizar el historial.");
        }
    }

    @PostMapping
    public ResponseEntity<Object> crear(@RequestBody HistorialReconocimiento historial){
        if(historial.getId() != 0)
            return Response.dbError("El historial tiene ID no nulo: " + historial.getId() + ". Ya existe.");
        if(historial.getUsuario() == null)
            return Response.dbError("El usuario no puede ser nulo.");
        try {
            HistorialReconocimiento nuevoHistorial = service.save(historial);
            return nuevoHistorial != null
                ? Response.ok(nuevoHistorial)
                : Response.dbError("No se pudo crear el historial.");
        } catch (DataIntegrityViolationException e) {
            return Response.error(e, "No se pudo crear el historial.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminarPorId(@PathVariable long id){
        service.deleteById(id);
        return Response.ok("El historial con ID " + id + " fue eliminado");
    }

    @DeleteMapping("/eliminar-por-usuario/{id}")
    public ResponseEntity<Object> eliminarPorIdDeUsuario(@PathVariable long id){
        service.deleteAllByUserId(id);
        return Response.ok("Los historiales asociados al usuario con ID " + id + " fueron eliminados");
    }
}
