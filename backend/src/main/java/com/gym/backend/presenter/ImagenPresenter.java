package com.gym.backend.presenter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gym.backend.business.services.HistorialReconocimientoService;
import com.gym.backend.business.services.UserService;
import com.gym.backend.model.User;

@RestController
@RequestMapping("/api/imagenes")
public class ImagenPresenter {

    @Autowired
    private UserService userService;

    @Autowired
    private HistorialReconocimientoService historialService;

    private final String uploadDir = "imagenes_maquinas_reconocidas/";

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getImagen(@PathVariable String filename) {
        try {
            // 1. Obtener usuario autenticado
            User user = userService.getAuthenticatedUser();
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // 2. Verificar que la imagen pertenezca a un historial del usuario
            boolean hasAccess = historialService.findAllByUserId(user.getId()).stream()
                    .anyMatch(hist -> {
                        String detalleJson = hist.getDetalleReconocimiento();
                        return detalleJson != null && detalleJson.contains(filename);
                    });

            if (!hasAccess) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // 3. Cargar el archivo
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            // 4. Determinar tipo de contenido
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            // 5. Devolver la imagen
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
