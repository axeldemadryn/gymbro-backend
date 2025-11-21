package com.gym.backend.business.services;

import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gym.backend.business.repositories.PlanRepository;
import com.gym.backend.business.repositories.UserPlanRepository;
import com.gym.backend.business.repositories.UserRepository;
import com.gym.backend.model.Plan;
import com.gym.backend.model.TipoPlan;
import com.gym.backend.model.User;
import com.gym.backend.model.UserPlan;

import jakarta.transaction.Transactional;

@Service
public class UserPlanService {

    private final ZoneId zoneId;

    @Autowired
    private UserPlanRepository userPlanRepository;
    @Autowired
    private PlanRepository planRepository;
    @Autowired
    private UserRepository userRepository;

    public UserPlanService(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    private LocalDate today() {
        return LocalDate.now(zoneId);
    }

    @Transactional
    public void cancelarPlanesVencidos() {

        LocalDate hoy = today();

        // Buscar planes con fecha vencida y cancelarlos
        userPlanRepository.findAllByCanceledFalse().forEach(plan -> {
            if (plan.getEndDate() != null && plan.getEndDate().isBefore(hoy)) {
                plan.setCanceled(true);
                userPlanRepository.save(plan);
            }
        });
    }

    /**
     * Obtiene el plan activo del usuario, teniendo en cuenta vencimiento.
     */
    public UserPlan getActivePlan(Long userId) {

        UserPlan plan = userPlanRepository.findByUserIdAndCanceledFalse(userId)
                .orElseThrow(() -> new RuntimeException("El usuario no tiene un plan activo."));

        // Si está vencido, lo cancelamos y no lo devolvemos como activo
        if (plan.getEndDate() != null && plan.getEndDate().isBefore(today())) {
            plan.setCanceled(true);
            userPlanRepository.save(plan);
            throw new RuntimeException("El plan del usuario está vencido.");
        }

        return plan;
    }

    /**
     * Asigna el plan FREE inicial solo si el usuario no tenía ningún plan.
     */
    @Transactional
    public UserPlan asignarPlanFree(User user) {

        boolean tuvoAlguno = userPlanRepository.existsByUserId(user.getId());
        if (tuvoAlguno)
            return null;

        Plan planFree = planRepository.findByType(TipoPlan.GRATUITO)
                .orElseThrow(() -> new IllegalStateException("Plan FREE no está cargado"));

        UserPlan up = new UserPlan();
        up.setUser(user);
        up.setPlan(planFree);
        up.setStartDate(today());
        up.setEndDate(today().plusMonths(1));
        up.setCanceled(false);

        return userPlanRepository.save(up);
    }

    /**
     * Activa un nuevo plan para el usuario.
     *
     * - Cancela automáticamente el plan activo anterior (si lo hubiera).
     * - No permite regresar al plan GRATUITO.
     * - Si es PREMIUM, crea un período de 1 mes desde la fecha actual.
     * - Registra un nuevo UserPlan sin modificar los históricos anteriores.
     */
    @Transactional
    public UserPlan activatePlan(Long userId, TipoPlan tipoPlan) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (tipoPlan == TipoPlan.GRATUITO) {
            throw new RuntimeException("No podés volver al plan FREE.");
        }

        Plan plan = planRepository.findByType(tipoPlan)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado: " + tipoPlan));

        userPlanRepository.findByUserIdAndCanceledFalse(userId).ifPresent(actual -> {
            actual.setCanceled(true);
            actual.setEndDate(today());
            userPlanRepository.save(actual);
        });

        // Forzar sincronización con un SELECT
        userPlanRepository.findByUserIdAndCanceledFalse(userId);

        UserPlan nuevo = new UserPlan();
        nuevo.setUser(user);
        nuevo.setPlan(plan);
        nuevo.setStartDate(today());
        nuevo.setEndDate(today().plusMonths(1));
        nuevo.setCanceled(false);

        return userPlanRepository.save(nuevo);
    }

    /**
     * Extiende un plan PREMIUM activo agregando un mes adicional.
     * No crea un nuevo registro, solo actualiza la fecha de finalización del plan
     * actual.
     */
    @Transactional
    public UserPlan renovarPremium(Long userId) {

        UserPlan activo = getActivePlan(userId);

        if (activo.getPlan().getType() != TipoPlan.PREMIUM) {
            throw new RuntimeException("El usuario no tiene un plan PREMIUM activo.");
        }

        activo.setEndDate(activo.getEndDate().plusMonths(1));
        return userPlanRepository.save(activo);
    }

}
