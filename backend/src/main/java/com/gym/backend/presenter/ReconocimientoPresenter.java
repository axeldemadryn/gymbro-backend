package com.gym.backend.presenter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.gym.backend.business.services.HistorialReconocimientoService;
import com.gym.backend.business.services.MaquinaService;
import com.gym.backend.business.services.OpenAiService;
import com.gym.backend.business.services.RecomendacionService;
import com.gym.backend.business.services.ReconocimientoService;
import com.gym.backend.business.services.ReconocimientoUsoService;
import com.gym.backend.business.services.RoboflowService;
import com.gym.backend.business.services.UserService;
import com.gym.backend.dto.MaquinaDTO;
import com.gym.backend.dto.OpenAiResponse;
import com.gym.backend.dto.RecomendacionDTO;
import com.gym.backend.dto.ReconocimientoViewModel;
import com.gym.backend.dto.RoboflowResponse;
import com.gym.backend.model.HistorialReconocimiento;
import com.gym.backend.model.User;
import com.gym.backend.response.Response;

import jakarta.annotation.PostConstruct;
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
    @Autowired
    private HistorialReconocimientoService historialService;
    @Autowired
    private UserService userService;
    @Autowired
    private RecomendacionService recomendacionService;
    @Autowired
    private ReconocimientoUsoService reconocimientoUsoService;

    private final ZoneId zoneId;

    // ✅ Inyectar ruta desde properties
    @Value("${app.imagenes.usuarios.path}")
    private String imagenesUsuariosPath;

    public ReconocimientoPresenter(ZoneId zoneId) {
        this.zoneId = zoneId; // Spring inyecta el bean
    }

    // ✅ Log en el inicio para verificar la variable
    @PostConstruct
    public void init() {
        System.out.println("════════════════════════════════════════");
        System.out.println("🔧 INICIALIZACIÓN DE ReconocimientoPresenter");
        System.out.println("📂 imagenesUsuariosPath = " + imagenesUsuariosPath);
        System.out.println("📂 ¿Es null? " + (imagenesUsuariosPath == null));
        if (imagenesUsuariosPath != null) {
            File dir = new File(imagenesUsuariosPath);
            System.out.println("📂 Ruta absoluta: " + dir.getAbsolutePath());
            System.out.println("📂 ¿Existe? " + dir.exists());
            System.out.println("📂 ¿Se puede escribir? " + dir.canWrite());
        }
        System.out.println("════════════════════════════════════════");
    }

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
    // ------------------ Endpoint seguro (requiere usuario autenticado)
    @PostMapping(value = "/api/reconocimiento", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> reconocer(@RequestParam MultipartFile file) {
        System.out.println("\n════════════════════════════════════════");
        System.out.println("🚀 INICIO DE RECONOCIMIENTO");
        System.out.println("════════════════════════════════════════");

        // 1. Obtener usuario autenticado
        System.out.println("👤 Obteniendo usuario autenticado...");
        User user = userService.getAuthenticatedUser();
        if (user == null) {
            System.err.println("❌ Usuario no autenticado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuario no autenticado");
        }
        System.out.println("✅ Usuario autenticado: ID=" + user.getId() + ", Email=" + user.getEmail());

        // 2. Validar y registrar el uso ANTES del reconocimiento
        try {
            reconocimientoUsoService.registrarUso(user.getId());
            System.out.println("📌 Uso registrado antes del reconocimiento");
        } catch (RuntimeException e) {
            System.err.println("❌ No se pudo registrar el uso: " + e.getMessage());
            return Response.error(null, e.getMessage());
        }

        // 3. Información del archivo recibido
        System.out.println("\n📸 INFORMACIÓN DEL ARCHIVO:");
        System.out.println("   Nombre: " + file.getOriginalFilename());
        System.out.println("   Tamaño: " + file.getSize() + " bytes");
        System.out.println("   Tipo: " + file.getContentType());
        System.out.println("   ¿Está vacío? " + file.isEmpty());

        // 4. Guardar la imagen del usuario
        System.out.println("\n💾 GUARDANDO IMAGEN...");
        System.out.println("   Ruta configurada: " + imagenesUsuariosPath);

        String publicUrl;
        try {
            publicUrl = guardarImagenUsuario(file, user.getId());
            System.out.println("✅ Imagen guardada exitosamente");
            System.out.println("   URL pública: " + publicUrl);
        } catch (IOException e) {
            System.err.println("❌ ERROR AL GUARDAR IMAGEN:");
            System.err.println("   Mensaje: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar la imagen: " + e.getMessage());
        }

        // 5. Reconocer la máquina a partir de la foto
        System.out.println("\n🤖 RECONOCIENDO MÁQUINA...");
        String nombre = reconocimientoService.reconocer(file).block();
        System.out.println("   Resultado: " + nombre);

        if ("no_reconocido".equalsIgnoreCase(nombre)) {
            System.out.println("⚠️ Máquina no reconocida");
            System.out.println("════════════════════════════════════════\n");
            return Response.ok("no_reconocido");
        }
        System.out.println("✅ Máquina reconocida: " + nombre);

        // 6. Obtener la info completa de la máquina reconocida
        System.out.println("\n📋 OBTENIENDO INFORMACIÓN DE LA MÁQUINA...");
        MaquinaDTO maquinaDTO = maquinaService.obtenerMaquinaConInfo(nombre).block();
        if (maquinaDTO == null) {
            System.err.println("❌ No se encontró información de la máquina: " + nombre);
            System.out.println("════════════════════════════════════════\n");
            return Response.notFound("No se encontró la máquina.");
        }
        System.out.println("✅ Información obtenida: " + maquinaDTO.getNombre());

        // 7. Reemplazar con la foto del usuario
        System.out.println("\n🔄 REEMPLAZANDO IMAGEN...");
        System.out.println("   Imagen original: " + maquinaDTO.getImagen());
        maquinaDTO.setImagen(publicUrl);
        System.out.println("   Nueva imagen: " + maquinaDTO.getImagen());

        // 8. Guardar el reconocimiento en historial
        System.out.println("\n💿 GUARDANDO EN HISTORIAL...");
        HistorialReconocimiento historial = new HistorialReconocimiento();
        historial.setUser(user);
        historial.setMaquina(maquinaService.findByNombre(nombre));
        historial.setFechaReconocimiento(LocalDate.now(zoneId));

        try {
            String jsonDetalle = new ObjectMapper().writeValueAsString(maquinaDTO);
            historial.setDetalleReconocimiento(jsonDetalle);
            System.out.println("   JSON guardado (primeros 100 chars): "
                    + jsonDetalle.substring(0, Math.min(100, jsonDetalle.length())) + "...");
        } catch (JsonProcessingException e) {
            System.err.println("⚠️ Error al serializar JSON: " + e.getMessage());
            historial.setDetalleReconocimiento(null);
        }

        historialService.save(historial);
        System.out.println("✅ Historial guardado con ID: " + historial.getId());

        // 9. Generar recomendación (solo si tiene rutina de hoy)
        System.out.println("\n🎯 GENERANDO RECOMENDACIÓN...");
        Optional<RecomendacionDTO> recomendacionOpt = recomendacionService.calcularSiCorresponde(user.getId(),
                maquinaDTO, nombre);

        // 10. Devolver respuesta final
        if (recomendacionOpt.isPresent()) {
            System.out.println("✅ Recomendación generada");
            System.out.println("════════════════════════════════════════\n");
            return Response.ok(recomendacionOpt.get());
        } else {
            System.out.println("ℹ️ Sin recomendación (usuario sin rutina de hoy)");
            System.out.println("════════════════════════════════════════\n");
            return Response.ok(maquinaDTO);
        }
    }

    // ✅ Método auxiliar con logs detallados
    private String guardarImagenUsuario(MultipartFile file, Long userId) throws IOException {
        System.out.println("   → Iniciando guardarImagenUsuario()");
        System.out.println("   → Ruta base: " + imagenesUsuariosPath);

        // Crear directorio si no existe
        File directory = new File(imagenesUsuariosPath);
        System.out.println("   → Verificando directorio...");
        System.out.println("      Existe: " + directory.exists());
        System.out.println("      Puede escribir: " + directory.canWrite());
        System.out.println("      Puede leer: " + directory.canRead());

        if (!directory.exists()) {
            System.out.println("   → Directorio no existe, creando...");
            boolean created = directory.mkdirs();
            System.out.println("      ¿Creado? " + created);
            if (!created) {
                throw new IOException("No se pudo crear el directorio: " + directory.getAbsolutePath());
            }
        }

        // Generar nombre único
        String timestamp = String.valueOf(System.currentTimeMillis());
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : ".jpg";
        String filename = timestamp + "_user_" + userId + extension;

        System.out.println("   → Nombre del archivo: " + filename);

        // Guardar archivo
        Path filePath = Paths.get(imagenesUsuariosPath, filename);
        System.out.println("   → Ruta completa: " + filePath.toAbsolutePath());
        System.out.println("   → Copiando archivo...");

        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("   → ✅ Archivo copiado exitosamente");
        } catch (Exception e) {
            System.err.println("   → ❌ Error al copiar archivo: " + e.getMessage());
            throw e;
        }

        // Verificar que se guardó
        File savedFile = filePath.toFile();
        System.out.println("   → Verificando archivo guardado:");
        System.out.println("      ¿Existe? " + savedFile.exists());
        System.out.println("      Tamaño: " + savedFile.length() + " bytes");

        String publicUrl = "/imagenes_usuarios/" + filename;
        System.out.println("   → URL pública generada: " + publicUrl);

        return publicUrl;
    }

    @GetMapping(value = "/api/reconocimiento-url")
    public Mono<String> reconocerURL(@RequestParam String urlImagen) {
        return reconocimientoService.reconocerURL(urlImagen);
    }

    // ----------------------------------------------------------------------------------------

    // Endpoints para testear directamente OpenAI
    @PostMapping(value = "/api/openai", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String openAiReconocer(@RequestParam MultipartFile file)
            throws IOException {
        String base64 = "data:" + file.getContentType() + ";base64,"
                + Base64.getEncoder().encodeToString(file.getBytes());
        return openAiService.reconocerNombre(base64).block();
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
