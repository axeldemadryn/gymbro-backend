package com.gym.backend.presenter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gym.backend.Response;
import com.gym.backend.business.services.UserService;
import com.gym.backend.model.User;
import com.gym.backend.security.JwtUtil;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserPresenter {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // 🧩 Registro de usuario
    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody User user) {
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
    public ResponseEntity<Object> login(@RequestBody User user) {
        try {
            Map<String, Object> result = userService.loginConToken(user.getEmail(), user.getPassword());
            return Response.ok(result);
        } catch (IllegalArgumentException e) {
            return Response.dbError(e.getMessage());
        } catch (Exception e) {
            return Response.dbError("Error al iniciar sesión: " + e.getMessage());
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<Object> verificarCuenta(@RequestParam("token") String token) {
        String email;
        try {
            email = jwtUtil.extraerUsername(token);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return Response.error(null, "El token de verificación expiró. Solicita uno nuevo.");
        } catch (io.jsonwebtoken.JwtException e) {
            return Response.error(null, "Token inválido.");
        }

        User user = userService.findByEmail(email);
        if (user == null) {
            return Response.notFound("Token inválido o usuario no encontrado");
        }

        user.setActivo(true);
        userService.save(user);

        return Response.ok("Cuenta verificada con éxito. Ya puedes iniciar sesión");
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<Object> resendVerification(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        if (email == null || email.isEmpty()) {
            return Response.dbError("El correo electrónico es obligatorio.");
        }

        try {
            Map<String, Object> result = userService.reenviarVerificacion(email);
            return Response.ok(result);
        } catch (IllegalStateException e) {
            return Response.dbError(e.getMessage());
        } catch (Exception e) {
            return Response.dbError("Error al reenviar verificación: " + e.getMessage());
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
            return Response.dbError(e.getMessage());
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
        try {
            String email = jwtUtil.extraerUsername(token);
            userService.logout(email);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return Response.error(null, "Token expirado");
        } catch (io.jsonwebtoken.JwtException e) {
            return Response.error(null, "Token inválido");
        } catch (Exception e) {
            return Response.dbError("Error durante logout: " + e.getMessage());
        }

        return Response.ok("Logout exitoso");
    }

}
