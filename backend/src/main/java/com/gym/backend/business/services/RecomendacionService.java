package com.gym.backend.business.services;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.backend.business.repositories.MaquinaRepository;
import com.gym.backend.business.repositories.RoutineDayRepository;
import com.gym.backend.business.repositories.SessionExerciseRepository;
import com.gym.backend.dto.EjercicioDTO;
import com.gym.backend.dto.ExerciseRecommendationsDTO;
import com.gym.backend.dto.MaquinaDTO;
import com.gym.backend.dto.MusculoDTO;
import com.gym.backend.dto.RecomendacionDTO;
import com.gym.backend.model.Ejercicio;
import com.gym.backend.model.Maquina;
import com.gym.backend.model.Musculo;
import com.gym.backend.model.RoutineDay;
import com.gym.backend.model.Session;
import com.gym.backend.model.SessionExercise;
import com.gym.backend.model.WeeklyRoutine;

@Service
public class RecomendacionService {

    @Autowired
    private RoutineDayRepository routineDayRepository;

    @Autowired
    private SessionExerciseRepository sessionExerciseRepository;

    @Autowired
    private MaquinaRepository maquinaRepository;

    @Autowired
    private MaquinaService maquinaService;

    private final ZoneId zoneId;

    public RecomendacionService(ZoneId zoneId) {
        this.zoneId = zoneId; // Spring inyecta el bean
    }

    /**
     * Calcula la recomendación de uso de una máquina para el día actual
     * de la rutina del usuario.
     * 
     * Historia de usuario:
     * Como usuario, quiero que al reconocer una máquina se me indique
     * si trabaja los músculos del día actual de mi rutina, para saber si debo
     * usarla o no.
     */
    public Optional<RecomendacionDTO> calcularSiCorresponde(Long userId, MaquinaDTO maquinaDTO, String nombreOriginal) {
        LocalDate hoy = LocalDate.now(zoneId);

        // 1️⃣ Obtener todos los RoutineDay del usuario
        List<RoutineDay> routineDays = routineDayRepository.findRoutineDaysForUserAndDate(userId, hoy);

        if (routineDays == null || routineDays.isEmpty())
            return Optional.empty(); // No hay rutinas, no hay recomendación

        // 2️⃣ Buscar el RoutineDay correspondiente a hoy
        RoutineDay routineDayHoy = routineDays.stream()
                .filter(rd -> {
                    WeeklyRoutine routine = rd.getRoutine();
                    // Calcular la fecha real del día dentro de la semana de la rutina
                    LocalDate fechaDelDia = routine.getStartDate().plusDays(rd.getDay().getDia().getValue() - 1);
                    return fechaDelDia.equals(hoy);
                })
                .findFirst()
                .orElse(null);

        if (routineDayHoy == null || routineDayHoy.getSession() == null)
            return Optional.empty(); // No hay sesión hoy, no se puede recomendar

        Session sesion = routineDayHoy.getSession();

        // ✅ CAMBIO: Buscar la máquina ANTES del loop usando el nombre original
        Maquina maquina = maquinaService.findByNombre(nombreOriginal);

        if (maquina == null) {
            System.err.println("❌ Máquina no encontrada: " + nombreOriginal);
            return Optional.empty();
        }

        double mejorPorcentaje = 0.0;

        // 3️⃣ Evaluar cada SessionExercise por separado
        // Comparamos los músculos del ejercicio con los músculos que trabaja la máquina
        for (var se : sesion.getSessionExercises()) {
            // ✅ CAMBIO: Usar maquina.getId() en lugar de buscar de nuevo
            long matches = sessionExerciseRepository.contarCoincidenciasMusculosPorSessionExercise(
                    se.getId(),
                    maquina.getId()); // ✅ Usar la máquina encontrada arriba

            long totalMusculosEjercicio = se.getExercise().getMusculos().size();

            // Calcular porcentaje de coincidencia para este ejercicio
            double porcentaje = (matches * 100.0) / totalMusculosEjercicio;

            // Guardar el mejor porcentaje entre todos los ejercicios de la sesión del día
            if (porcentaje > mejorPorcentaje) {
                mejorPorcentaje = porcentaje;
            }
        }

        if (mejorPorcentaje == 0)
            return Optional.empty(); // Ningún ejercicio coincide, no recomendable

        // 4️⃣ Asignar mensaje según porcentaje
        String mensaje = mejorPorcentaje <= 30 ? "No recomendable para hoy"
                : mejorPorcentaje <= 70 ? "Parcialmente recomendable para hoy"
                        : "Altamente recomendable para hoy";

        // 5️⃣ Crear y devolver DTO de recomendación
        RecomendacionDTO dto = new RecomendacionDTO();
        dto.setMaquina(maquinaDTO);
        dto.setNivelCoincidencia(mejorPorcentaje);
        dto.setMensaje(mensaje);

        return Optional.of(dto);
    }

    /**
     * Genera recomendaciones de máquinas agrupadas por ejercicio dentro de una
     * sesión.
     * 
     * Para cada SessionExercise:
     * - Obtiene sus músculos
     * - Busca todas las máquinas que trabajan esos músculos
     * - Calcula el nivel de coincidencia de cada máquina según los músculos en
     * común
     * - Arma un DTO por ejercicio con las máquinas recomendadas (ordenadas por
     * coincidencia)
     * 
     * Si la sesión no tiene ejercicios → devuelve lista vacía.
     */
    public List<ExerciseRecommendationsDTO> obtenerRecomendacionesAgrupadasPorSesion(Long sessionId) {

        List<SessionExercise> sessionExercises = sessionExerciseRepository.findAllBySessionId(sessionId);

        return sessionExercises.stream()
                .map(se -> {

                    Ejercicio ejercicio = se.getExercise();
                    Set<Musculo> musculosEjercicio = ejercicio.getMusculos();

                    // 🔹 DTO por ejercicio
                    ExerciseRecommendationsDTO dto = new ExerciseRecommendationsDTO();
                    dto.setExerciseId(ejercicio.getId());
                    dto.setExerciseName(ejercicio.getNombre());
                    dto.setSets(se.getSets());
                    dto.setReps(se.getReps());

                    // Músculos del ejercicio
                    dto.setMusculos(
                            musculosEjercicio.stream()
                                    .map(m -> new MusculoDTO(m.getNombre()))
                                    .toList());

                    // 🔹 Obtener máquinas relacionadas según los músculos del ejercicio
                    Set<Maquina> maquinas = musculosEjercicio.stream()
                            .flatMap(mu -> maquinaRepository.findMaquinasByMusculoId(mu.getId()).stream())
                            .collect(Collectors.toSet());

                    List<RecomendacionDTO> recomendaciones = maquinas.stream()
                            .map(m -> {

                                long matches = sessionExerciseRepository.contarCoincidenciasMusculosPorSessionExercise(
                                        se.getId(),
                                        m.getId());

                                double porcentaje = musculosEjercicio.isEmpty() ? 0
                                        : (matches * 100.0) / musculosEjercicio.size();

                                // DTO de máquina
                                MaquinaDTO maquinaDTO = new MaquinaDTO();
                                maquinaDTO.setNombre(m.getNombreTraducido());
                                maquinaDTO.setTipoEquipo(m.getTipoEquipo() != null ? m.getTipoEquipo().name() : null);
                                maquinaDTO.setDescripcion(m.getDescripcion());
                                maquinaDTO.setImagen(m.getImagenUrl());

                                if (m.getMusculos() != null && !m.getMusculos().isEmpty()) {
                                    maquinaDTO.setMusculos(
                                            m.getMusculos().stream()
                                                    .map(mu -> new MusculoDTO(mu.getNombre()))
                                                    .toList());
                                }

                                if (m.getEjercicios() != null && !m.getEjercicios().isEmpty()) {
                                    maquinaDTO.setEjercicios(
                                            m.getEjercicios().stream()
                                                    .map(ej -> {
                                                        EjercicioDTO ejDTO = new EjercicioDTO();
                                                        ejDTO.setNombre(ej.getNombre());
                                                        ejDTO.setTipo(
                                                                ej.getTipo() != null ? ej.getTipo().name() : null);
                                                        ejDTO.setDescripcion(ej.getDescripcion());
                                                        ejDTO.setVideoUrl(ej.getVideoUrl());
                                                        ejDTO.setMusculosPrincipales(
                                                                ej.getMusculos() == null ? List.of()
                                                                        : ej.getMusculos().stream()
                                                                                .map(mu -> new MusculoDTO(
                                                                                        mu.getNombre()))
                                                                                .toList());
                                                        return ejDTO;
                                                    })
                                                    .toList());
                                }

                                // Crear recomendación
                                RecomendacionDTO rec = new RecomendacionDTO();
                                rec.setMaquina(maquinaDTO);
                                rec.setNivelCoincidencia(porcentaje);

                                String mensaje = porcentaje <= 30 ? "No recomendable"
                                        : porcentaje <= 70 ? "Parcialmente recomendable"
                                                : "Altamente recomendable";

                                rec.setMensaje(mensaje);

                                return rec;
                            })
                            .sorted((a, b) -> Double.compare(b.getNivelCoincidencia(), a.getNivelCoincidencia()))
                            .toList();

                    dto.setRecommendedMachines(recomendaciones);

                    return dto;
                })
                .toList();
    }

}
