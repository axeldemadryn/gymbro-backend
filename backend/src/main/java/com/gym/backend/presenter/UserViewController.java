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

    // 🔹 Verificación de cuenta (enlace del mail)
    @GetMapping("/verify")
    public String verificarCuenta(@RequestParam("token") String token, Model model) {
        try {
            String email = jwtUtil.extraerUsername(token);
            User user = userService.findByEmail(email);

            if (user == null) {
                model.addAttribute("tipoToken", "verificación");
                return "token-invalido";
            }

            if (user.isActivo()) {
                model.addAttribute("mensaje", "Tu cuenta ya fue verificada anteriormente. Ya podés iniciar sesión.");
                return "verify-success";
            }

            user.setActivo(true);
            userService.save(user);

            return "verify-success";

        } catch (ExpiredJwtException e) {
            model.addAttribute("tipoToken", "verificación");
            return "token-expirado";

        } catch (JwtException e) {
            model.addAttribute("tipoToken", "verificación");
            return "token-invalido";

        } catch (Exception e) {
            model.addAttribute("mensaje", "Ocurrió un error al verificar la cuenta.");
            return "verify-error";
        }
    }

    // 🔹 Mostrar formulario de restablecimiento
    @GetMapping("/reset-password")
    public String mostrarFormularioResetPassword(@RequestParam("token") String token, Model model) {
        try {
            jwtUtil.extraerUsername(token); // valida token
            model.addAttribute("token", token);
            return "reset-password-form";
        } catch (ExpiredJwtException e) {
            model.addAttribute("tipoToken", "recuperación");
            return "token-expirado";
        } catch (JwtException e) {
            model.addAttribute("tipoToken", "recuperación");
            return "token-invalido";
        }
    }

    // 🔹 Procesar el envío del formulario
    @PostMapping("/reset-password")
    public String procesarResetPassword(
            @RequestParam("token") String token,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {

        if (!password.equals(confirmPassword)) {
            model.addAttribute("mensaje", "Las contraseñas no coinciden.");
            model.addAttribute("token", token);
            return "reset-password-form";
        }

        /*
         * Validación opcional de complejidad:
         *
         * if (!password.matches(
         * "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%^&+=!]).{8,}$")) {
         * model.addAttribute("mensaje",
         * "La contraseña debe tener al menos 8 caracteres, incluir mayúscula, minúscula, número y carácter especial."
         * );
         * model.addAttribute("token", token);
         * return "reset-password-form";
         * }
         */

        try {
            String email = jwtUtil.extraerUsername(token);
            User user = userService.findByEmail(email);

            if (user == null) {
                model.addAttribute("tipoToken", "recuperación");
                return "token-invalido";
            }

            userService.resetearPassword(user, password);
            return "reset-password-success";

        } catch (ExpiredJwtException e) {
            model.addAttribute("tipoToken", "recuperación");
            return "token-expirado";

        } catch (JwtException e) {
            model.addAttribute("tipoToken", "recuperación");
            return "token-invalido";

        } catch (Exception e) {
            model.addAttribute("mensaje", "Ocurrió un error al cambiar la contraseña.");
            return "reset-password-error";
        }
    }
}
