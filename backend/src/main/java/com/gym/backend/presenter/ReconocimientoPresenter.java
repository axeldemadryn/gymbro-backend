package com.gym.backend.presenter;

import java.io.IOException;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.gym.backend.Response;
import com.gym.backend.business.OpenAiService;
import com.gym.backend.business.ReconocimientoService;
import com.gym.backend.business.RoboflowService;
import com.gym.backend.business.services.MaquinaService;
import com.gym.backend.dto.OpenAiResponse;
import com.gym.backend.dto.ReconocimientoViewModel;
import com.gym.backend.dto.RoboflowResponse;

import reactor.core.publisher.Mono;

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

    // ------------------Endpoints para Roboflow
    // -----------------------------------------
/* 
    @PostMapping(value = "/api/roboflow", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ReconocimientoViewModel> reconocerRoboflow(@RequestParam MultipartFile file) throws IOException {
        return roboflowService.reconocer(file)
                .map(ReconocimientoPresenter::present); // Transformar a ViewModel cuando llegue la respuesta

    }*/
    @GetMapping(value = "/api/roboflow-url")
    public Mono<ReconocimientoViewModel> reconocerRoboflowURL(@RequestParam String urlImagen) {
        return roboflowService.reconocerURL(urlImagen)
                .map(ReconocimientoPresenter::present);
    }

    // ------------------ Endpoints para el flujo completo (Roboflow + OpenAI)
    // ------------------
    @PostMapping(value = "/api/reconocimiento", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<Object>> reconocer(@RequestParam MultipartFile file) throws IOException {
        return reconocimientoService.reconocer(file) // devuelve el nombre de la máquina
                .flatMap(nombre -> {
                    if ("no_reconocido".equalsIgnoreCase(nombre)) {
                        return Mono.just(Response.ok("no_reconocido"));
                    }
                    // Consultar el servicio de máquinas para traer MaquinaDTO con ejercicios
                    return maquinaService.obtenerMaquinaConEjercicios(nombre)
                            .map(maquinaDTO -> Response.ok(maquinaDTO))
                            .defaultIfEmpty(Response.notFound("No se encontró la máquina en la base de datos."));
                })
                .onErrorResume(e -> Mono.just(Response.ok("no_reconocidoja"))); // errores
    }

    @GetMapping(value = "/api/reconocimiento-url")
    public Mono<String> reconocerURL(@RequestParam String urlImagen) {
        return reconocimientoService.reconocerURL(urlImagen);
    }
    // ----------------------------------------------------------------------------------------

    // Endpoints para testear directamente OpenAI
/* 
    @PostMapping(value = "/api/openai", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> openAiReconocer(@RequestParam MultipartFile file)
            throws IOException {
        return openAiService.reconocerNombre(file);
    }*/

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
