package com.gym.backend.presenter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.backend.Response;
import com.gym.backend.business.services.HistorialReconocimientoService;
import com.gym.backend.business.services.UserService;
import com.gym.backend.dto.HistorialReconocimientoDTO;
import com.gym.backend.dto.MaquinaDTO;
import com.gym.backend.model.HistorialReconocimiento;
import com.gym.backend.model.User;

@RestController
@RequestMapping("api/historial-reconocimientos")
public class HistorialReconocimientoPresenter {
    @Autowired
    private HistorialReconocimientoService service;
    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Object> encontrarTodos() {
        return Response.ok(service.findAll());
    }

    private HistorialReconocimientoDTO toDTO(HistorialReconocimiento hist) {
        ObjectMapper mapper = new ObjectMapper();
        HistorialReconocimientoDTO dto = new HistorialReconocimientoDTO();
        dto.setFechaReconocimiento(hist.getFechaReconocimiento());

        try {
            MaquinaDTO detalle = hist.getDetalleReconocimiento() != null
                    ? mapper.readValue(hist.getDetalleReconocimiento(), MaquinaDTO.class)
                    : null;
            dto.setDetalleReconocimiento(detalle);
        } catch (JsonProcessingException e) {
            dto.setDetalleReconocimiento(null);
        }

        return dto;
    }

    @GetMapping("/por-usuario")
    public ResponseEntity<Object> encontrarPorUsuarioAutenticado() {
        // Obtener usuario autenticado desde el servicio
        User user = userService.getAuthenticatedUser();

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuario no autenticado");
        }

        // Buscar historiales del usuario autenticado
        List<HistorialReconocimiento> historiales = service.findAllByUserId(user.getId());
        if (historiales.isEmpty()) {
            return Response.notFound("No hay historiales de reconocimiento para este usuario.");
        }

        // Convertir a DTOs
        List<HistorialReconocimientoDTO> response = historiales.stream()
                .map(this::toDTO)
                .toList();

        return Response.ok(response);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Object> encontrarPorId(@PathVariable long id) {
        HistorialReconocimiento hist = service.findById(id);
        return Response.ok(hist != null ? toDTO(hist) : null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminarPorId(@PathVariable long id) {
        service.deleteById(id);
        return Response.ok("El historial con ID " + id + " fue eliminado");
    }

    @DeleteMapping("/eliminar-por-usuario/{id}")
    public ResponseEntity<Object> eliminarPorIdDeUsuario(@PathVariable long id) {
        service.deleteAllByUserId(id);
        return Response.ok("Los historiales asociados al usuario con ID " + id + " fueron eliminados");
    }
}
