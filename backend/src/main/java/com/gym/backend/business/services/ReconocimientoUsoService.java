package com.gym.backend.business.services;

import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.backend.business.repositories.ReconocimientoUsoRepository;
import com.gym.backend.model.ReconocimientoUso;
import com.gym.backend.model.TipoPlan;
import com.gym.backend.model.User;
import com.gym.backend.model.UserPlan;

@Service
public class ReconocimientoUsoService {

    private final ZoneId zoneId;

    @Autowired
    private ReconocimientoUsoRepository repo;

    @Autowired
    private UserPlanService userPlanService;

    @Autowired
    private UserService userService;

    public ReconocimientoUsoService(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    private LocalDate hoy() {
        return LocalDate.now(zoneId);
    }

    public void registrarUso(Long userId) {

        UserPlan plan = userPlanService.getActivePlan(userId);
        TipoPlan tipo = plan.getPlan().getType();

        // --- VALIDAR LIMITE DIARIO ---
        ReconocimientoUso usoHoy = repo.findByUserIdAndFecha(userId, hoy()).orElse(null);

        int usadosHoy = usoHoy != null ? usoHoy.getCantidadUsos() : 0;

        if (usadosHoy >= tipo.getMaxReconocimientosDiarios()) {
            throw new RuntimeException("Limite diario alcanzado para tu plan.");
        }

        // --- VALIDAR LIMITE TOTAL ---
        int usadosTotales = repo.sumarUsosTotales(userId);
        if (usadosTotales >= tipo.getMaxReconocimientos()) {
            throw new RuntimeException("Limite total de reconocimientos alcanzado para tu plan.");
        }

        // --- REGISTRAR EL USO ---
        User user = userService.findById(userId); // usuario completo

        if (usoHoy == null) {
            usoHoy = new ReconocimientoUso();
            usoHoy.setUser(user); // usuario real, no solo un id
            usoHoy.setFecha(hoy());
            usoHoy.setCantidadUsos(1);
        } else {
            usoHoy.setCantidadUsos(usoHoy.getCantidadUsos() + 1);
        }

        repo.save(usoHoy);
    }
}
