package com.gym.backend.business.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.gym.backend.business.repositories.UserRepository;
import com.gym.backend.model.User;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Encriptador de contraseñas
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User registrarUsuario(User user) {
        // Validar si el email ya existe
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalStateException("El e-mail ya está registrado.");
        }

        // Encriptar la contraseña
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // Fecha de registro
        user.setFechaRegistro(LocalDateTime.now());
        user.setActivo(true);

        return userRepository.save(user);
    }

    public User login(String email, String password) {
        Optional<User> usuarioOpt = userRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado con ese e-mail.");
        }

        User user = usuarioOpt.get();

        // Verificar contraseña
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Contraseña incorrecta.");
        }

        return user;
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

    public User save(User user) {
        return userRepository.save(user);
    }

    public Iterable<User> listarUsuarios() {
        return userRepository.findAll();
    }
}
