package com.gym.backend.presenter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gym.backend.business.services.UserService;
import com.gym.backend.model.User;
import com.gym.backend.security.JwtUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

@Controller
@RequestMapping("/api/users")
public class UserViewController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @GetMapping("/reset-password")
    public String mostrarFormularioResetPassword(@RequestParam("token") String token, Model model) {
        try {
            jwtUtil.extraerUsername(token); // solo valida el token
            model.addAttribute("token", token);
            return "reset-password-form"; // template Thymeleaf
        } catch (ExpiredJwtException e) {
            return "token-expirado"; // otro template
        } catch (JwtException e) {
            return "token-invalido"; // otro template
        }
    }

    // 🔹 Procesar envío del formulario de reset
    @PostMapping("/reset-password")
    public String procesarResetPassword(
            @RequestParam("token") String token,
            @RequestParam("password") String password,
            Model model) {

        try {
            String email = jwtUtil.extraerUsername(token);
            User user = userService.findByEmail(email);

            if (user == null) {
                model.addAttribute("mensaje", "Token inválido o usuario no encontrado");
                return "reset-password-error";
            }

            userService.resetearPassword(user, password);
            return "reset-password-success"; // template de éxito

        } catch (ExpiredJwtException e) {
            model.addAttribute("mensaje", "El enlace de recuperación expiró. Solicita uno nuevo.");
            return "reset-password-error";
        } catch (JwtException e) {
            model.addAttribute("mensaje", "Token inválido.");
            return "reset-password-error";
        } catch (Exception e) {
            model.addAttribute("mensaje", "Ocurrió un error al cambiar la contraseña.");
            return "reset-password-error";
        }
    }

}
