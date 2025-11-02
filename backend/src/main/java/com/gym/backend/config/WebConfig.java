package com.gym.backend.config;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Value("${app.imagenes.usuarios.path}")
    private String imagenesUsuariosPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absolutePath = new File(imagenesUsuariosPath).getAbsolutePath();
        
        System.out.println("📂 Sirviendo imágenes de usuarios desde: " + absolutePath);
        
        registry.addResourceHandler("/imagenes_usuarios/**")
                .addResourceLocations("file:" + absolutePath + "/");
    }
}
