package com.gym.backend.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import com.gym.backend.dto.OpenAiRequest;
import com.gym.backend.dto.OpenAiResponse;

import reactor.core.publisher.Mono;

@Service
public class OpenAiService {
    private final WebClient webClient;

    @Value("${openai.base.url}")
    private String baseUrl;

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.model}")
    private String model;

    // Lista interna única: usada solo aquí
    private static final List<String> MAQUINAS_PRUEBA = List.of(
            "press banca", "remo", "sentadilla", "peso muerto",
            "curl biceps", "extensión de piernas", "dominadas",
            "press militar", "fondos", "abdominales", "press inclinado",
            "remo en polea", "aperturas");

    public OpenAiService(WebClient webClient) {
        this.webClient = webClient;
    }

    private Mono<OpenAiResponse> doPost(OpenAiRequest request) {
        // valida apiKey para dar un error claro
        if (apiKey == null || apiKey.isBlank()) {
            return Mono.error(new IllegalStateException("openai.api.key no está configurada"));
        }

        return webClient.post()
                .uri(baseUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                // Captura el body de error si el endpoint responde 4xx/5xx
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> ((HttpStatus) status).isError(), resp -> resp.bodyToMono(String.class)
                        .defaultIfEmpty("<empty body>")
                        .flatMap(body -> Mono.error(new ResponseStatusException(
                                resp.statusCode(),
                                "OpenAI API error: " + body))))
                .bodyToMono(OpenAiResponse.class);
    }

    /**
     * Construye un request JSON para la Responses API de OpenAI:
     * - añade un mensaje con prompt para que responda únicamente con el nombre de
     * la máquina
     */
    public Mono<OpenAiResponse> reconocer(String base64Image) throws IOException {
        // Leemos la lista desde resources/maquinas.txt
        ClassPathResource resource = new ClassPathResource("maquinas.txt");
        List<String> maquinas = new BufferedReader(new InputStreamReader(resource.getInputStream()))
                .lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .toList();

        String maquinasTexto = "Lista de máquinas válidas:\n" + String.join("\n", maquinas);

        // Prompt de reglas
        String systemContent = "Responde solo con un nombre de la lista provista.\n" +
                "Reglas:\n" +
                "- Si la máquina parece una de la lista, elige la más parecida; solo usa ‘Ninguna’ si de verdad no se parece a ninguna.\n"
                +
                "- Si estás seguro, responde solo con el nombre exacto de la máquina de la lista.\n" +
                "- Si NO estás seguro, responde únicamente con: Ninguna.\n" +
                "- No inventes nombres que no estén en la lista.\n" +
                "- Analiza cada máquina de la lista hasta encontrar la más parecida a la de la foto posible, entre todas las máquinas.\n";

  
        OpenAiRequest.Content imageContent = OpenAiRequest.Content.image(base64Image);
        OpenAiRequest.Content textContent = OpenAiRequest.Content.text(maquinasTexto);

        // Armamos los mensajes
        OpenAiRequest.Message systemMessage = new OpenAiRequest.Message("system",
                List.of(OpenAiRequest.Content.text(systemContent)));
        OpenAiRequest.Message userMessage = new OpenAiRequest.Message("user", List.of(textContent, imageContent));

        // Armamos la request final
        OpenAiRequest request = new OpenAiRequest(model, 0, List.of(systemMessage, userMessage));

        return doPost(request);
    }

    /**
     * Construye un request JSON para la Responses API de OpenAI usando una URL de
     * imagen.
     * Útil cuando la imagen ya está disponible públicamente en internet.
     */
    public Mono<OpenAiResponse> reconocerURL(String imageUrl) {
        String prompt = "Esta es una imagen de una máquina de gimnasio. Solo puede ser una de estas: "
                + String.join(", ", MAQUINAS_PRUEBA)
                + ". Devuelve únicamente el nombre correcto.";

        // texto primero, luego imagen
        OpenAiRequest.Content textContent = OpenAiRequest.Content.text(prompt);
        OpenAiRequest.Content imageContent = OpenAiRequest.Content.image(imageUrl);
        OpenAiRequest.Message message = new OpenAiRequest.Message("user", List.of(textContent, imageContent));
        OpenAiRequest request = new OpenAiRequest(model, 0, List.of(message));

        return doPost(request);
    }

    // Métodos de conveniencia que devuelven directamente el nombre resuelto
    public Mono<String> reconocerNombre(String base64Image) throws IOException {
        return reconocer(base64Image)
                .map(OpenAiResponse::getText);
    }

    public Mono<String> reconocerNombreURL(String imageUrl) {
        return reconocerURL(imageUrl)
                .map(OpenAiResponse::getText);
    }
}
