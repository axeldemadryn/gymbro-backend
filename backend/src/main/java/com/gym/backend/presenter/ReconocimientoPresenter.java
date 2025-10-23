package com.gym.backend.presenter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.stream.Collectors;

import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.backend.Response;
import com.gym.backend.business.services.HistorialReconocimientoService;
import com.gym.backend.business.services.MaquinaService;
import com.gym.backend.business.services.OpenAiService;
import com.gym.backend.business.services.ReconocimientoService;
import com.gym.backend.business.services.RoboflowService;
import com.gym.backend.business.services.UserService;
import com.gym.backend.dto.MaquinaDTO;
import com.gym.backend.dto.OpenAiResponse;
import com.gym.backend.dto.ReconocimientoViewModel;
import com.gym.backend.dto.RoboflowResponse;
import com.gym.backend.model.HistorialReconocimiento;
import com.gym.backend.model.User;

@RestController
public class ReconocimientoPresenter {

    @Autowired
    private RoboflowService roboflowService;
    @Autowired
    private ReconocimientoService reconocimientoService;
    @Autowired
    private OpenAiService openAiService;
    @Autowired
    private MaquinaService maquinaService;
    @Autowired
    private HistorialReconocimientoService historialService;
    @Autowired
    private UserService userService;

    // ------------------Endpoints para Roboflow
    // -----------------------------------------

    @PostMapping(value = "/api/roboflow", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ReconocimientoViewModel> reconocerRoboflow(@RequestParam MultipartFile file) throws IOException {
        String base64 = "data:" + file.getContentType() + ";base64,"
                + Base64.getEncoder().encodeToString(file.getBytes());
        return roboflowService.reconocer(base64)
                .map(ReconocimientoPresenter::present); // Transformar a ViewModel cuando llegue la respuesta

    }

    @GetMapping(value = "/api/roboflow-url")
    public Mono<ReconocimientoViewModel> reconocerRoboflowURL(@RequestParam String urlImagen) {
        return roboflowService.reconocerURL(urlImagen)
                .map(ReconocimientoPresenter::present);
    }

    // ------------------ Endpoints para el flujo completo (Roboflow + OpenAI)
    // ------------------
    @PostMapping(value = "/api/reconocimiento", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> reconocer(@RequestParam MultipartFile file) {

        // Obtener usuario autenticado
        User user = userService.getAuthenticatedUser();

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuario no autenticado");
        }

        // Bloquear el Mono para obtener el nombre de la máquina
        String nombre = reconocimientoService.reconocer(file).block();
        if ("no_reconocido".equalsIgnoreCase(nombre)) {
            return Response.ok("no_reconocido");
        }

        // Bloquear el Mono para obtener la info completa de la máquina
        MaquinaDTO maquinaDTO = maquinaService.obtenerMaquinaConInfo(nombre).block();
        if (maquinaDTO == null) {
            return Response.notFound("No se encontró la máquina.");
        }

        // Guardar en historial
        HistorialReconocimiento historial = new HistorialReconocimiento();
        historial.setUser(user);
        historial.setMaquina(maquinaService.findByNombre(nombre));
        historial.setFechaReconocimiento(LocalDate.now());
        try {
            historial.setDetalleReconocimiento(
                    new ObjectMapper().writeValueAsString(maquinaDTO));
        } catch (JsonProcessingException e) {
            historial.setDetalleReconocimiento(null);
        }
        historialService.save(historial);

        // Devolver la máquina al frontend
        return Response.ok(maquinaDTO);
    }

    @GetMapping(value = "/api/reconocimiento-url")
    public Mono<String> reconocerURL(@RequestParam String urlImagen) {
        return reconocimientoService.reconocerURL(urlImagen);
    }

    // ----------------------------------------------------------------------------------------

    // Endpoints para testear directamente OpenAI
    @PostMapping(value = "/api/openai", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> openAiReconocer(@RequestParam MultipartFile file)
            throws IOException {
        String base64 = "data:" + file.getContentType() + ";base64,"
                + Base64.getEncoder().encodeToString(file.getBytes());
        return openAiService.reconocerNombre(base64);
    }

    @GetMapping(value = "/api/openai-url")
    public Mono<OpenAiResponse> openAiReconocerURL(@RequestParam String urlImagen) {
        return openAiService.reconocerURL(urlImagen);
    }
    // fin endpoints OpenAI

    public static ReconocimientoViewModel present(RoboflowResponse response) {
        ReconocimientoViewModel vm = new ReconocimientoViewModel();

        vm.setCantidadObjetos(response.getPredictions().size());
        vm.setObjetos(
                response.getPredictions().stream()
                        .map(pred -> new ReconocimientoViewModel.ObjetoReconocido(
                                pred.getClassName(),
                                pred.getConfidence(),
                                pred.getX(),
                                pred.getY()))
                        .collect(Collectors.toList()));

        return vm;
    }

}
