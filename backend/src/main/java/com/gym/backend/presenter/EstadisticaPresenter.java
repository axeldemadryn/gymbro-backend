package com.gym.backend.presenter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gym.backend.dto.EstadisticaSemanalDTO;
import com.gym.backend.model.User;
import com.gym.backend.model.WeeklyRoutine;
import com.gym.backend.response.Response;
import com.gym.backend.business.services.EstadisticaService;
import com.gym.backend.business.services.UserService;
import com.gym.backend.business.services.WeeklyRoutineService;

@RestController
@RequestMapping("/api/stats")
public class EstadisticaPresenter {

    @Autowired
    private EstadisticaService estadisticaService;
    @Autowired
    private WeeklyRoutineService weeklyRoutineService;
    @Autowired
    private UserService userService;

    @GetMapping("/rutinas/{id}")
    public ResponseEntity<Object> obtenerEstadisticas(@PathVariable Long id) {

        User user = userService.getAuthenticatedUser();
        if (user == null) {
            return ResponseEntity.status(401).body("Usuario no autenticado.");
        }

        WeeklyRoutine routine = weeklyRoutineService.findById(id);
        if (routine == null) {
            return Response.notFound("No se encontró la rutina semanal.");
        }

        if (!routine.getUser().getId().equals(user.getId())) {
            return Response.dbError("No puede ver estadísticas de una rutina que no le pertenece.");
        }

        EstadisticaSemanalDTO dto = estadisticaService.calcular(routine);
        return Response.ok(dto);
    }
}
