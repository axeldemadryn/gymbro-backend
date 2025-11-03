package com.gym.backend.business.services;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.backend.business.repositories.MaquinaRepository;
import com.gym.backend.business.repositories.RoutineDayRepository;
import com.gym.backend.business.repositories.SessionExerciseRepository;
import com.gym.backend.dto.EjercicioDTO;
import com.gym.backend.dto.MaquinaDTO;
import com.gym.backend.dto.MusculoDTO;
import com.gym.backend.dto.RecomendacionDTO;
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
    public Optional<RecomendacionDTO> calcularSiCorresponde(Long userId, MaquinaDTO maquinaDTO) {
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

        double mejorPorcentaje = 0.0;

        // 3️⃣ Evaluar cada SessionExercise por separado
        // Comparamos los músculos del ejercicio con los músculos que trabaja la máquina
        for (var se : sesion.getSessionExercises()) {
            // Contar cuántos músculos del ejercicio coinciden con los de la máquina
            long matches = sessionExerciseRepository.contarCoincidenciasMusculosPorSessionExercise(
                    se.getId(),
                    maquinaService.findByNombre(maquinaDTO.getNombre()).getId());

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
        String mensaje = mejorPorcentaje <= 30 ? "No recomendable"
                : mejorPorcentaje <= 70 ? "Parcialmente recomendable para hoy"
                        : "Altamente recomendable para hoy";

        // 5️⃣ Devolver DTO de recomendación con porcentaje y mensaje
        return Optional.of(new RecomendacionDTO(maquinaDTO, mejorPorcentaje, mensaje));
    }

    /**
     * Genera una lista de recomendaciones de máquinas para una sesión de
     * entrenamiento.
     *
     * Este método recorre todos los ejercicios asociados a una sesión
     * (SessionExercise),
     * identifica los músculos que trabaja cada uno y busca todas las máquinas que
     * también trabajan al menos uno de esos músculos. Luego calcula el porcentaje
     * de
     * coincidencia
     * entre los músculos del ejercicio y los de cada máquina.
     *
     * Por cada máquina encontrada, se calcula su nivel de coincidencia con los
     * ejercicios
     * de la sesión y se conserva el valor máximo (en caso de que la máquina
     * coincida con varios ejercicios).
     * De esta manera, el resultado final es una lista de máquinas únicas, ordenadas
     * por su
     * nivel de coincidencia más alto.
     *
     * Por ejemplo, si una sesión tiene 2 ejercicios y una máquina trabaja músculos
     * comunes con ambos, se mostrará una sola vez con el mayor porcentaje de
     * coincidencia.
     *
     * @param sessionId identificador de la sesión de entrenamiento.
     * @return una lista de {@link RecomendacionDTO}, cada uno representando una
     *         máquina
     *         recomendada con su nivel de coincidencia y un mensaje de
     *         recomendación.
     */
    public List<RecomendacionDTO> obtenerRecomendacionesPorSesion(Long sessionId) {
        // 🔹 Obtener todos los ejercicios asociados a la sesión
        List<SessionExercise> sessionExercises = sessionExerciseRepository.findAllBySessionId(sessionId);

        // 🔹 Mapa para almacenar la coincidencia máxima de cada máquina
        Map<Maquina, Double> maquinaCoincidencia = new HashMap<>();

        // 🔹 Recorrer cada ejercicio de la sesión
        for (SessionExercise se : sessionExercises) {
            Set<Musculo> musculosEjercicio = se.getExercise().getMusculos();

            // 🔹 Obtener todas las máquinas que trabajan al menos uno de esos músculos
            Set<Maquina> maquinas = musculosEjercicio.stream()
                    .flatMap(mu -> maquinaRepository.findMaquinasByMusculoId(mu.getId()).stream())
                    .collect(Collectors.toSet());

            // 🔹 Por cada máquina que trabaja al menos 1 ejercicio del SessionExercise, se
            // calcula la coincidencia usando la query
            for (Maquina m : maquinas) {
                long matches = sessionExerciseRepository.contarCoincidenciasMusculosPorSessionExercise(
                        se.getId(),
                        m.getId());

                // 🔹 Guarda por cada máquina el mayor porcentaje de coincidencia encontrado
                // para determinado SessionExercise
                double porcentaje = (matches * 100.0) / musculosEjercicio.size();
                maquinaCoincidencia.merge(m, porcentaje, Double::max);
            }
        }

        // 🔹 Armar DTOs de resultado
        // 🔹 Nota: la cantidad de recomendaciones finales puede ser menor, igual o
        // mayor
        // al número de ejercicios de la sesión, dependiendo de cuántas máquinas
        // distintas
        // trabajen los músculos involucrados.
        return maquinaCoincidencia.entrySet().stream()
                .map(entry -> {
                    Maquina m = entry.getKey();

                    // DTO básico de máquina
                    MaquinaDTO dto = new MaquinaDTO();
                    dto.setNombre(m.getNombre());
                    dto.setTipoEquipo(m.getTipoEquipo() != null ? m.getTipoEquipo().name() : null);
                    dto.setDescripcion(m.getDescripcion());
                    dto.setImagen(m.getImagenUrl());

                    // Músculos
                    if (m.getMusculos() != null) {
                        dto.setMusculos(m.getMusculos().stream()
                                .map(mu -> new MusculoDTO(mu.getNombre()))
                                .toList());
                    }

                    // Ejercicios opcionales
                    if (m.getEjercicios() != null) {
                        dto.setEjercicios(m.getEjercicios().stream().map(e -> {
                            EjercicioDTO ed = new EjercicioDTO();
                            ed.setNombre(e.getNombre());
                            ed.setTipo(e.getTipo() != null ? e.getTipo().name() : null);
                            ed.setDescripcion(e.getDescripcion());
                            ed.setVideoUrl(e.getVideoUrl());

                            if (e.getMusculos() != null) {
                                ed.setMusculosPrincipales(e.getMusculos().stream()
                                        .map(mu -> new MusculoDTO(mu.getNombre()))
                                        .toList());
                            }
                            return ed;
                        }).toList());
                    }

                    // 🔹 Crear recomendación
                    RecomendacionDTO rec = new RecomendacionDTO();
                    rec.setMaquina(dto);
                    rec.setNivelCoincidencia(entry.getValue());

                    double p = entry.getValue();
                    String mensaje = p <= 30 ? "No recomendable"
                            : p <= 70 ? "Parcialmente recomendable"
                                    : "Altamente recomendable";
                    rec.setMensaje(mensaje);

                    return rec;
                })
                // 🔹 Ordenar de mayor a menor coincidencia
                .sorted((a, b) -> Double.compare(b.getNivelCoincidencia(), a.getNivelCoincidencia()))
                .toList();
    }

}
