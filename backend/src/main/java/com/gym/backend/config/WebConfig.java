package com.gym.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // ✅ Permitir servir imágenes desde la carpeta "imagenes_maquinas_reconocidas"
        registry.addResourceHandler("/imagenes_maquinas_reconocidas/**")
                .addResourceLocations("file:imagenes_maquinas_reconocidas/");
    }
}
