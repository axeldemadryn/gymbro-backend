package com.gym.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.gym.backend.business.services.RoutineDayService;

@Configuration
@EnableScheduling
public class SchedulerConfig {
    @Autowired
    private RoutineDayService service;

    // Ejecuta todos los días a la medianoche
    @Scheduled(cron = "0 0 0 * * *")
    public void actualizarEstadosRutinasDiarias() {
        service.actualizarEstadosSegunHoy();
    }
}
