package com.gym.backend.presenter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/api/historial-reconocimientos")
public class HistorialReconocimientoPresenter {

    @Autowired
    private HistorialReconocimientoService service;

    @Autowired
    private UserService userService;

    // ------------------ Helpers ------------------
    private HistorialReconocimientoDTO toDTO(HistorialReconocimiento hist) {
        ObjectMapper mapper = new ObjectMapper();
        HistorialReconocimientoDTO dto = new HistorialReconocimientoDTO();
        dto.setId(hist.getId());
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

    private ResponseEntity<Object> validarUsuarioAutenticado() {
        User user = userService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");
        }
        return null; // usuario válido
    }

    private boolean verificarPropiedad(HistorialReconocimiento hist, User user) {
        return hist.getUser().getId().equals(user.getId());
    }

    // ------------------ Endpoints ------------------

    // Todos los historiales
    @GetMapping
    public ResponseEntity<Object> encontrarTodos() {
        return Response.ok(service.findAll());
    }

    // Historiales del usuario autenticado
    @GetMapping("/por-usuario")
    public ResponseEntity<Object> encontrarPorUsuarioAutenticado() {
        ResponseEntity<Object> authCheck = validarUsuarioAutenticado();
        if (authCheck != null)
            return authCheck;

        User user = userService.getAuthenticatedUser();
        List<HistorialReconocimiento> historiales = service.findAllByUserId(user.getId());
        if (historiales.isEmpty()) {
            return Response.notFound("No hay historiales de reconocimiento para este usuario.");
        }

        List<HistorialReconocimientoDTO> response = historiales.stream()
                .map(this::toDTO)
                .toList();

        return Response.ok(response);
    }

    // Buscar historial por ID (solo si pertenece al usuario autenticado)
    @GetMapping("/id/{id}")
    public ResponseEntity<Object> encontrarPorId(@PathVariable long id) {
        ResponseEntity<Object> authCheck = validarUsuarioAutenticado();
        if (authCheck != null)
            return authCheck;

        User user = userService.getAuthenticatedUser();
        HistorialReconocimiento hist = service.findById(id);
        if (hist == null)
            return Response.notFound("Historial no encontrado");
        if (!verificarPropiedad(hist, user))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes acceso a este historial");

        return Response.ok(toDTO(hist));
    }

    // Eliminar historial por ID (solo propietario)
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> eliminarPorId(@PathVariable long id) {
        ResponseEntity<Object> authCheck = validarUsuarioAutenticado();
        if (authCheck != null)
            return authCheck;

        User user = userService.getAuthenticatedUser();
        HistorialReconocimiento hist = service.findById(id);
        if (hist == null)
            return Response.notFound("Historial no encontrado");
        if (!verificarPropiedad(hist, user))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para eliminar este historial");

        service.deleteById(id);
        return Response.ok("El historial con ID " + id + " fue eliminado");
    }

    // Eliminar todos los historiales del usuario autenticado
    @DeleteMapping("/por-usuario")
    public ResponseEntity<Object> eliminarHistoralesDelUsuario() {
        ResponseEntity<Object> authCheck = validarUsuarioAutenticado();
        if (authCheck != null)
            return authCheck;

        User user = userService.getAuthenticatedUser();
        service.deleteAllByUserId(user.getId());
        return Response.ok("Se eliminaron todos los historiales del usuario autenticado");
    }
}
