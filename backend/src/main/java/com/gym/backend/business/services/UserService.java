package com.gym.backend.business.services;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.gym.backend.business.repositories.UserRepository;
import com.gym.backend.model.User;
import com.gym.backend.security.JwtUtil;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    // Encriptador de contraseñas
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails userDetails) {
            return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        }

        return null;
    }

    @Transactional
    public Map<String, Object> registrarUsuarioConToken(User user) {
        User existente = userRepository.findByEmail(user.getEmail()).orElse(null);

        if (existente != null) {
            if (existente.isActivo()) {
                // Ya verificado → no puede volver a registrarse
                throw new IllegalStateException("El e-mail ya está registrado y activo.");
            } else {
                // Usuario pendiente de verificación
                throw new IllegalStateException(
                        "Este e-mail ya está registrado pero aún no fue verificado. " +
                                "Revisa tu correo o solicita un nuevo enlace de verificación.");
            }
        }

        // Crear nuevo usuario
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setFechaRegistro(LocalDateTime.now());
        user.setActivo(false);

        User created = userRepository.save(user);

        // Generar token JWT para verificación
        String tokenVerificacion = jwtUtil.generarTokenVerificacion(created.getEmail());

        enviarCorreoVerificacion(created.getEmail(), tokenVerificacion);

        return Map.of(
                "usuario", created,
                "token", tokenVerificacion);

    }

    private void enviarCorreoVerificacion(String email, String token) {
        String link = "http://localhost:8080/api/users/verify?token=" + token;

        // Enviar correo con Mailpit
        emailService.enviarCorreo(
                email,
                "Verificación de cuenta",
                "¡Hola!\n\n" +
                        "Por favor, verifica tu cuenta haciendo clic en el siguiente enlace:\n" +
                        link + "\n\nGracias por registrarte");
    }

    public Map<String, Object> loginConToken(String email, String password) {
        Optional<User> usuarioOpt = userRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado con ese e-mail.");
        }

        User user = usuarioOpt.get();

        if (!user.isActivo()) {
            throw new IllegalArgumentException("Tu cuenta aún no fue verificada. Revisa tu correo.");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Contraseña incorrecta.");
        }

        // Generar token de sesión
        String tokenSesion = jwtUtil.generarTokenSesion(user.getEmail());

        return Map.of(
                "usuario", user,
                "token", tokenSesion);
    }

    @Transactional
    public Map<String, Object> reenviarVerificacion(String email) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            throw new IllegalStateException("No existe una cuenta registrada con ese correo.");
        }

        if (user.isActivo()) {
            throw new IllegalStateException("Esta cuenta ya fue verificada.");
        }

        // Verificar si no pasó poco tiempo desde el último registro (evitar spam)
        if (user.getFechaRegistro() != null &&
                user.getFechaRegistro().isAfter(LocalDateTime.now().minusMinutes(10))) {
            throw new IllegalStateException("Ya se envió un correo recientemente. Intenta de nuevo en unos minutos.");
        }

        // Actualizar fecha de registro
        user.setFechaRegistro(LocalDateTime.now());
        userRepository.save(user);

        // Generar nuevo token y enviar correo
        String tokenVerificacion = jwtUtil.generarTokenVerificacion(user.getEmail());
        enviarCorreoVerificacion(user.getEmail(), tokenVerificacion);

        return Map.of(
                "message", "Se reenviaron las instrucciones de verificación.",
                "token", tokenVerificacion);
    }

    @Transactional
    public User actualizarUsuario(Long id, User user) {
        User existente = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró un usuario con ese id."));

        // Validación de email si querés buscar por email
        if (!existente.getEmail().equals(user.getEmail()) &&
                userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email.");
        }

        existente.setNombre(user.getNombre());
        existente.setActivo(user.isActivo());

        return userRepository.save(existente);
    }

    @Transactional
    public void desactivarUsuario(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        user.setActivo(false); // suponiendo que tenés un campo 'activo'
        userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    public Iterable<User> listarUsuarios() {
        return userRepository.findAll();
    }

    @Transactional
    public void logout(String email) {
        User user = findByEmail(email);
        if (user != null) {
            user.setLastLogout(LocalDateTime.now());
            userRepository.save(user);
        }
    }
}
