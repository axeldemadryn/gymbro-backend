package com.gym.backend.business.services;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.backend.business.repositories.RoutineDayRepository;
import com.gym.backend.business.repositories.SessionExerciseRepository;
import com.gym.backend.dto.MaquinaDTO;
import com.gym.backend.dto.RecomendacionDTO;
import com.gym.backend.model.RoutineDay;
import com.gym.backend.model.Session;
import com.gym.backend.model.WeeklyRoutine;

@Service
public class RecomendacionService {

    @Autowired
    private RoutineDayRepository routineDayRepository;

    @Autowired
    private SessionExerciseRepository sessionExerciseRepository;

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

}
