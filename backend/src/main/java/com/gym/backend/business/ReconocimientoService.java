package com.gym.backend.business;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import reactor.core.publisher.Mono;

@Service
public class ReconocimientoService {
    @Autowired
    private RoboflowService roboflowService;

    @Autowired
    private OpenAiService openAiService;

    public Mono<String> reconocer(MultipartFile file) {
        String base64;
        try {
            base64 = "data:" + file.getContentType() + ";base64," + Base64.getEncoder().encodeToString(file.getBytes());
        } catch (IOException e) {
            return Mono.just("no_reconocido");
        }

        return Mono.fromCallable(() -> roboflowService.reconocer(base64)) // captura IOException
                .flatMap(respMono -> respMono)
                .flatMap(resp -> {
                    // Caso 1: exactamente una predicción con confidence >= 0.85
                    if (resp.getPredictions().size() == 1 && resp.getPredictions().get(0).getConfidence() >= 0.85) {
                        return Mono.just(resp.getPredictions().get(0).getClassName());
                    } else {
                        // Caso 2: OpenAI
                        try {
                            return openAiService.reconocerNombre(base64)
                                    .map(nombre -> "Ninguna".equalsIgnoreCase(nombre) ? "no_reconocido" : nombre)
                                    .onErrorResume(e -> Mono.just("no_reconocido"));
                        } catch (IOException e) {
                            return Mono.just("no_reconocido");
                        }
                    }
                })
                .onErrorResume(e -> Mono.just("no_reconocido")); // errores de Roboflow
    }

    /**
     * Version por URL: primero consultamos Roboflow con la URL (GET), si queda
     * ambiguo pedimos a OpenAI (POST)
     * usando la misma URL como image_url.
     */
    public Mono<String> reconocerURL(String urlImagen) {
        return roboflowService.reconocerURL(urlImagen)
                .flatMap(resp -> {
                    if (resp.getPredictions().size() == 1 &&
                            resp.getPredictions().get(0).getConfidence() > 0.8) {
                        return Mono.just(resp.getPredictions().get(0).getClassName());
                    } else {
                        return openAiService.reconocerNombreURL(urlImagen);
                    }
                });
    }
}
