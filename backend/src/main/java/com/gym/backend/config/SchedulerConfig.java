package com.gym.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.gym.backend.business.services.RoutineDayService;
import com.gym.backend.business.services.UserPlanService;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Autowired
    private RoutineDayService service;
    @Autowired
    private UserPlanService userPlanService;

    // Ejecuta todos los días a la medianoche
    // Rutinas
    @Scheduled(cron = "0 0 0 * * *", zone = "America/Argentina/Buenos_Aires")
    public void actualizarEstadosRutinasDiarias() {
        service.actualizarEstadosSegunHoy();
    }

    // Planes
    @Scheduled(cron = "0 5 0 * * *", zone = "America/Argentina/Buenos_Aires")
    public void cancelarPlanesVencidos() {
        userPlanService.cancelarPlanesVencidos();
    }
}
