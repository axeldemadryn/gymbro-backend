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
    public ResponseEntity<String> verificarCuenta(@RequestParam("token") String token) {
        String email = jwtUtil.extraerUsername(token);

        User user = userService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body("Token inválido o usuario no encontrado");
        }

        user.setActivo(true);
        userService.save(user);

        return ResponseEntity.ok("Cuenta verificada con éxito. Ya puedes iniciar sesión");
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
}
