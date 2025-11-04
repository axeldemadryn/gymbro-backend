package com.gym.backend.presenter;

import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gym.backend.business.services.UserService;
import com.gym.backend.dto.EmailRequestDTO;
import com.gym.backend.dto.LoginRequestDTO;
import com.gym.backend.model.User;
import com.gym.backend.response.Response;
import com.gym.backend.security.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserPresenter {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // 🧩 Registro de usuario
    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody User user) {
        try {
            Map<String, Object> result = userService.registrarUsuarioConToken(user);
            return Response.ok(result);
        } catch (IllegalStateException e) {
            return Response.dbError(e.getMessage());
        } catch (DataIntegrityViolationException e) {
            return Response.dbError("El correo ya está registrado.");
        } catch (Exception e) {
            return Response.dbError("Error al registrar usuario: " + e.getMessage());
        }
    }

    // 🔐 Login
    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequestDTO dto) {
        try {
            Map<String, Object> result = userService.loginConToken(dto.getEmail(), dto.getPassword());
            return Response.ok(result);
        } catch (IllegalArgumentException e) {
            return Response.dbError(e.getMessage());
        } catch (Exception e) {
            return Response.dbError("Error al iniciar sesión: " + e.getMessage());
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Object> resendVerification(@Valid @RequestBody EmailRequestDTO request) {
        String email = request.getEmail();

        try {
            userService.reenviarVerificacion(email);
            return Response.ok("Correo de verificación enviado con éxito.");
        } catch (IllegalStateException e) {
            return Response.dbError(e.getMessage());
        } catch (Exception e) {
            return Response.dbError("Error al reenviar verificación: " + e.getMessage());
        }
    }

    // Recuperación de contraseña
    // 📧 Enviar link de recuperación
    @PostMapping("/recover-password")
    public ResponseEntity<Object> recoverPassword(@Valid @RequestBody EmailRequestDTO request) {
        String email = request.getEmail();

        try {
            userService.enviarRecuperacion(email);
            return Response.ok("Correo de recuperación enviado con éxito.");
        } catch (IllegalArgumentException e) {
            return Response.dbError(e.getMessage());
        } catch (Exception e) {
            return Response.dbError("Error al enviar correo de recuperación: " + e.getMessage());
        }
    }

    // 📋 Listar todos los usuarios (solo temporal para pruebas)
    @GetMapping
    public ResponseEntity<Object> listarUsuarios() {
        return Response.ok(userService.listarUsuarios());
    }

    // ✏️ Actualizar datos del usuario
    @PutMapping("/{id}")
    public ResponseEntity<Object> actualizar(@PathVariable Long id, @RequestBody User user) {
        try {
            User actualizado = userService.actualizarUsuario(id, user);
            return Response.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return Response.notFound(e.getMessage());
        } catch (Exception e) {
            return Response.dbError("Error al actualizar el usuario: " + e.getMessage());
        }
    }

    // 🚫 Desactivar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> desactivar(@PathVariable Long id) {
        try {
            userService.desactivarUsuario(id);
            return Response.ok("Usuario desactivado correctamente.");
        } catch (IllegalArgumentException e) {
            return Response.error(e, e.getMessage());
        } catch (Exception e) {
            return Response.dbError("Error al desactivar el usuario: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Object> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Response.error(null, "Token no proporcionado");
        }

        String token = authHeader.substring(7);

        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");

        try {
            String email = jwtUtil.extraerUsername(token);
            userService.logout(email);
        } catch (ExpiredJwtException e) {
            return Response.error(e, "Token expirado");
        } catch (JwtException e) {
            return Response.error(e, "Token inválido");
        } catch (Exception e) {
            return Response.dbError("Error durante logout: " + e.getMessage());
        }

        return Response.ok("Logout exitoso");
    }

}
