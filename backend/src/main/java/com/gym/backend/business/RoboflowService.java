package com.gym.backend.business;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.backend.dto.RoboflowResponse;

import reactor.core.publisher.Mono;

@Service
public class RoboflowService {

    @Value("${roboflow.api.key}")
    private String apiKey;

    @Value("${roboflow.model}")
    private String model;

    @Value("${roboflow.base.url}")
    private String baseUrl;

    private final WebClient webClient;

    private final ObjectMapper objectMapper;

    public RoboflowService(WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    public Mono<RoboflowResponse> reconocer(String base64Image) throws IOException {
        String url = baseUrl + "/" + model + "?api_key=" + apiKey;

        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .bodyValue(base64Image)
                .retrieve()
                .bodyToMono(String.class)
                .map(responseBody -> {
                    try {
                        // Parseamos JSON a RoboflowResponse
                        return objectMapper.readValue(responseBody, RoboflowResponse.class);
                    } catch (Exception e) {
                        throw new RuntimeException("Error parseando RoboflowResponse", e);
                    }
                });

    }

    public Mono<RoboflowResponse> reconocerURL(String urlImagen){
        String url = baseUrl + "/" + model + "?api_key=" + apiKey + "&image=" + urlImagen;

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .map(responseBody -> {
                    try {
                        // Parseamos JSON a RoboflowResponse
                        return objectMapper.readValue(responseBody, RoboflowResponse.class);
                    } catch (Exception e) {
                        throw new RuntimeException("Error parseando RoboflowResponse", e);
                    }
                });
    }

}
