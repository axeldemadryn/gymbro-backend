package com.gym.backend.business.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.backend.business.repositories.RoutineDayRepository;
import com.gym.backend.model.RoutineDay;
import com.gym.backend.model.SessionStatus;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class RoutineDayService {
    @Autowired
    private RoutineDayRepository repository;

    private void evaluarRutina(RoutineDay rd){
        if (rd.getStatus() == SessionStatus.PENDIENTE // Si la rutina estaba pendiente (sin completar todavía)...
            && LocalDate.now().isAfter(rd.getRoutine().getStartDate()) // ...y el día de hoy es posterior al día de inicio de la rutina semanal...
            && LocalDate.now().getDayOfWeek().getValue() > rd.getDay().getDia().getValue()) { // ...y además (el día de hoy) es posterior al de la rutina diaria misma...
                        rd.setStatus(SessionStatus.NO_COMPLETADA); // ...entonces no está completa.
        }
    }

    @Transactional
    public void actualizarEstadosSegunHoy() {
        List<RoutineDay> routineDays = logicaObtencionDeTodas();

        for (RoutineDay rd : routineDays) {
            evaluarRutina(rd);
        }
        repository.saveAll(routineDays);
    }

    public RoutineDay findById(long id) {
        actualizarEstadosSegunHoy();
        return repository.findById(id).orElse(null);
    }

    private List<RoutineDay> logicaObtencionDeTodas(){
        List<RoutineDay> result = new ArrayList<>();
        repository.findAll().forEach(result::add);
        return result;
    }

    public List<RoutineDay> findAll() {
        actualizarEstadosSegunHoy();
        return logicaObtencionDeTodas();
    }

    @Transactional
    public RoutineDay save(RoutineDay routineDay) {
        routineDay.setStatus(SessionStatus.PENDIENTE); // Pasa el estado a pendiente
        evaluarRutina(routineDay); // Setea el estado a no completada, si corresponde
        return repository.save(routineDay);
    }

    @Transactional
    public RoutineDay marcarCompletada(RoutineDay routine){
        if(routine.getStatus().equals(SessionStatus.PENDIENTE)){
            routine.setStatus(SessionStatus.COMPLETADA);
        } else {
            throw new RuntimeException(routine.getStatus().equals(SessionStatus.COMPLETADA)
                ? "La rutina ya está completada." // no se puede marcar como completada una rutina ya completada y marcada
                : "Error. La rutina ya ha expirado."); // rutina incompleta
        }
        return repository.save(routine);
    }

    @Transactional
    public void delete(long routineDayId) {
        repository.deleteById(routineDayId);
    }
}
