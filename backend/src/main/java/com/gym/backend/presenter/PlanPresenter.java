package com.gym.backend.presenter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gym.backend.business.services.UserPlanService;
import com.gym.backend.business.services.UserService;
import com.gym.backend.model.TipoPlan;
import com.gym.backend.model.User;
import com.gym.backend.model.UserPlan;

@RestController
@RequestMapping("/api/planes")
public class PlanPresenter {

    @Autowired
    private UserService userService;
    @Autowired
    private UserPlanService userPlanService;

    @PostMapping("/cambiar/{tipoPlan}")
    public ResponseEntity<Object> cambiarPlan(@PathVariable TipoPlan tipoPlan) {

        User user = userService.getAuthenticatedUser();
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no autenticado");

        try {
            UserPlan nuevo = userPlanService.activatePlan(user.getId(), tipoPlan);
            return ResponseEntity.ok(nuevo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
