package com.example.puntofacil.demo.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Resuelve la ruta absoluta de la carpeta de uploads
        Path dir = Paths.get(uploadPath).toAbsolutePath().normalize();

        // Todo lo que venga a /uploads/** se sirve desde esa carpeta
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + dir.toString() + "/");
    }
}
