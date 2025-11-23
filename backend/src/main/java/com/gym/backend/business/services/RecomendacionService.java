package com.gym.backend.business.services;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
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
import com.gym.backend.dto.RecomendacionEjercicioDiaDTO;
import com.gym.backend.dto.RecomendacionPorDiaDTO;
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

        public Optional<RecomendacionPorDiaDTO> calcularRecomendacionesParaHoy(
                        Long userId,
                        MaquinaDTO maquinaDTO,
                        String nombreOriginal) {

                LocalDate hoy = LocalDate.now(zoneId);

                // 1️⃣ Obtener RoutineDay del usuario para hoy
                List<RoutineDay> routineDays = routineDayRepository.findRoutineDaysForUserAndDate(userId, hoy);

                if (routineDays == null || routineDays.isEmpty())
                        return Optional.empty();

                RoutineDay routineDayHoy = routineDays.stream()
                                .filter(rd -> {
                                        WeeklyRoutine routine = rd.getRoutine();
                                        LocalDate fechaDelDia = routine.getStartDate()
                                                        .plusDays(rd.getDay().getDia().getValue() - 1);
                                        return fechaDelDia.equals(hoy);
                                })
                                .findFirst()
                                .orElse(null);

                if (routineDayHoy == null || routineDayHoy.getSession() == null)
                        return Optional.empty();

                Session sesion = routineDayHoy.getSession();

                Maquina maquina = maquinaService.findByNombre(nombreOriginal);
                if (maquina == null) {
                        System.err.println("❌ Máquina no encontrada: " + nombreOriginal);
                        return Optional.empty();
                }

                // 2️⃣ Lista de recomendaciones por ejercicio
                List<RecomendacionEjercicioDiaDTO> recomendaciones = new ArrayList<>();

                for (SessionExercise se : sesion.getSessionExercises()) {
                        Ejercicio ejercicio = se.getExercise();
                        Set<Musculo> musculosEjercicio = ejercicio.getMusculos();

                        long matches = sessionExerciseRepository.contarCoincidenciasMusculosPorSessionExercise(
                                        se.getId(), maquina.getId());

                        long totalMusculos = musculosEjercicio.size();
                        double porcentaje = totalMusculos == 0 ? 0 : (matches * 100.0) / totalMusculos;

                        if (porcentaje > 0) { // 🔹 solo agregamos si hay coincidencia
                                RecomendacionEjercicioDiaDTO dto = new RecomendacionEjercicioDiaDTO();
                                dto.setEjercicioId(ejercicio.getId());
                                dto.setEjercicioNombre(ejercicio.getNombre());
                                dto.setSets(se.getSets());
                                dto.setReps(se.getReps());
                                dto.setMusculos(musculosEjercicio.stream()
                                                .map(m -> new MusculoDTO(m.getNombre()))
                                                .toList());
                                dto.setNivelCoincidencia(porcentaje);

                                String mensaje = porcentaje <= 30 ? "No recomendable para este ejercicio de hoy"
                                                : porcentaje <= 70
                                                                ? "Parcialmente recomendable para este ejercicio de hoy"
                                                                : "Altamente recomendable para este ejercicio de hoy";

                                dto.setMensaje(mensaje);
                                recomendaciones.add(dto);
                        }
                }

                recomendaciones.sort(Comparator.comparingDouble(
                                RecomendacionEjercicioDiaDTO::getNivelCoincidencia).reversed());

                // 3️⃣ Si no hay ejercicios con coincidencia, agregamos un mensaje general
                if (recomendaciones.isEmpty()) {
                        recomendaciones.add(crearRecomendacionNoRecomendable());
                }

                RecomendacionPorDiaDTO resp = new RecomendacionPorDiaDTO();
                resp.setMaquina(maquinaDTO);
                resp.setRecomendaciones(recomendaciones);

                return Optional.of(resp);
        }

        // Método auxiliar para recomendación no recomendable
        private RecomendacionEjercicioDiaDTO crearRecomendacionNoRecomendable() {
                RecomendacionEjercicioDiaDTO dto = new RecomendacionEjercicioDiaDTO();
                dto.setEjercicioId(null);
                dto.setEjercicioNombre(null);
                dto.setSets(null);
                dto.setReps(null);
                dto.setMusculos(List.of());
                dto.setNivelCoincidencia(0);
                dto.setMensaje("No recomendable para hoy");
                return dto;
        }

        // NO se usa este método
        public Optional<RecomendacionPorDiaDTO> calcularRecomendacionesParaHoyVersion1(
                        Long userId,
                        MaquinaDTO maquinaDTO,
                        String nombreOriginal) {
                LocalDate hoy = LocalDate.now(zoneId);

                // 1️⃣ Obtener RoutineDay del usuario para hoy
                List<RoutineDay> routineDays = routineDayRepository.findRoutineDaysForUserAndDate(userId, hoy);

                if (routineDays == null || routineDays.isEmpty())
                        return Optional.empty();

                RoutineDay routineDayHoy = routineDays.stream()
                                .filter(rd -> {
                                        WeeklyRoutine routine = rd.getRoutine();
                                        LocalDate fechaDelDia = routine.getStartDate()
                                                        .plusDays(rd.getDay().getDia().getValue() - 1);
                                        return fechaDelDia.equals(hoy);
                                })
                                .findFirst()
                                .orElse(null);

                if (routineDayHoy == null || routineDayHoy.getSession() == null)
                        return Optional.empty();

                Session sesion = routineDayHoy.getSession();

                // 2️⃣ Buscar máquina reconocida
                Maquina maquina = maquinaService.findByNombre(nombreOriginal);

                if (maquina == null) {
                        System.err.println("❌ Máquina no encontrada: " + nombreOriginal);
                        return Optional.empty();
                }

                // 3️⃣ Preparamos lista de recomendaciones por ejercicio
                List<RecomendacionEjercicioDiaDTO> recomendaciones = new ArrayList<>();

                for (SessionExercise se : sesion.getSessionExercises()) {

                        Ejercicio ejercicio = se.getExercise();
                        Set<Musculo> musculosEjercicio = ejercicio.getMusculos();

                        long matches = sessionExerciseRepository.contarCoincidenciasMusculosPorSessionExercise(
                                        se.getId(),
                                        maquina.getId());

                        long totalMusculos = musculosEjercicio.size();
                        double porcentaje = totalMusculos == 0 ? 0 : (matches * 100.0) / totalMusculos;

                        // mensaje
                        String mensaje = porcentaje <= 30 ? "No recomendable para este ejercicio de hoy"
                                        : porcentaje <= 70 ? "Parcialmente recomendable para este ejercicio de hoy"
                                                        : "Altamente recomendable para este ejercicio de hoy";

                        // DTO
                        RecomendacionEjercicioDiaDTO dto = new RecomendacionEjercicioDiaDTO();
                        dto.setEjercicioId(ejercicio.getId());
                        dto.setEjercicioNombre(ejercicio.getNombre());
                        dto.setSets(se.getSets());
                        dto.setReps(se.getReps());
                        dto.setMusculos(
                                        musculosEjercicio.stream()
                                                        .map(m -> new MusculoDTO(m.getNombre()))
                                                        .toList());
                        dto.setNivelCoincidencia(porcentaje);
                        dto.setMensaje(mensaje);

                        recomendaciones.add(dto);
                }

                // 4️⃣ Armamos respuesta final
                RecomendacionPorDiaDTO resp = new RecomendacionPorDiaDTO();
                resp.setMaquina(maquinaDTO);
                resp.setRecomendaciones(recomendaciones);

                return Optional.of(resp);
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
                                                        .flatMap(mu -> maquinaRepository
                                                                        .findMaquinasByMusculoId(mu.getId()).stream())
                                                        .collect(Collectors.toSet());

                                        List<RecomendacionDTO> recomendaciones = maquinas.stream()
                                                        .map(m -> {

                                                                long matches = sessionExerciseRepository
                                                                                .contarCoincidenciasMusculosPorSessionExercise(
                                                                                                se.getId(),
                                                                                                m.getId());

                                                                double porcentaje = musculosEjercicio.isEmpty() ? 0
                                                                                : (matches * 100.0) / musculosEjercicio
                                                                                                .size();

                                                                // DTO de máquina
                                                                MaquinaDTO maquinaDTO = new MaquinaDTO();
                                                                maquinaDTO.setNombre(m.getNombreTraducido());
                                                                maquinaDTO.setTipoEquipo(m.getTipoEquipo() != null
                                                                                ? m.getTipoEquipo().name()
                                                                                : null);
                                                                maquinaDTO.setDescripcion(m.getDescripcion());
                                                                maquinaDTO.setImagen(m.getImagenUrl());

                                                                if (m.getMusculos() != null
                                                                                && !m.getMusculos().isEmpty()) {
                                                                        maquinaDTO.setMusculos(
                                                                                        m.getMusculos().stream()
                                                                                                        .map(mu -> new MusculoDTO(
                                                                                                                        mu.getNombre()))
                                                                                                        .toList());
                                                                }

                                                                if (m.getEjercicios() != null
                                                                                && !m.getEjercicios().isEmpty()) {
                                                                        maquinaDTO.setEjercicios(
                                                                                        m.getEjercicios().stream()
                                                                                                        .map(ej -> {
                                                                                                                EjercicioDTO ejDTO = new EjercicioDTO();
                                                                                                                ejDTO.setNombre(ej
                                                                                                                                .getNombre());
                                                                                                                ejDTO.setTipo(
                                                                                                                                ej.getTipo() != null
                                                                                                                                                ? ej.getTipo().name()
                                                                                                                                                : null);
                                                                                                                ejDTO.setDescripcion(
                                                                                                                                ej.getDescripcion());
                                                                                                                ejDTO.setVideoUrl(
                                                                                                                                ej.getVideoUrl());
                                                                                                                ejDTO.setMusculosPrincipales(
                                                                                                                                ej.getMusculos() == null
                                                                                                                                                ? List.of()
                                                                                                                                                : ej.getMusculos()
                                                                                                                                                                .stream()
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
                                                                                : porcentaje <= 70
                                                                                                ? "Parcialmente recomendable"
                                                                                                : "Altamente recomendable";

                                                                rec.setMensaje(mensaje);

                                                                return rec;
                                                        })
                                                        .sorted((a, b) -> Double.compare(b.getNivelCoincidencia(),
                                                                        a.getNivelCoincidencia()))
                                                        .toList();

                                        dto.setRecommendedMachines(recomendaciones);

                                        return dto;
                                })
                                .toList();
        }

}
