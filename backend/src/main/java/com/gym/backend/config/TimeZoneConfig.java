package com.gym.backend.config;

import java.time.ZoneId;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TimeZoneConfig {

    @Bean
    public ZoneId zoneId() {
        // Acá se setea el uso horario del sistema
        return ZoneId.of("America/Argentina/Buenos_Aires");
    }
}
