package com.gym.backend.business.services;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

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
    @Transactional
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
     * Activa o extiende un plan PREMIUM para un usuario.
     *
     * Reglas principales:
     *
     * 1. No permite activar nuevamente el plan GRATUITO.
     *
     * 2. Si el usuario YA TIENE un plan activo:
     * - Si es el MISMO plan (PREMIUM):
     * · Se extiende la fecha de finalización según la situación actual:
     * - Si endDate > hoy → se extiende desde endDate.
     * - Si endDate == hoy → se extiende desde mañana (para no perder el día).
     * - Si endDate < hoy → se extiende desde hoy (plan vencido).
     * · NO se cancela ni se crea un nuevo registro.
     *
     * - Si el plan es diferente:
     * · El plan anterior se cancela (canceled = true, endDate = hoy).
     * · Se fuerza una sincronización para que se aplique el update
     * antes de crear el nuevo plan (evitando violaciones de la constraint
     * UNIQUE(user_id, canceled)).
     * · Se crea un nuevo UserPlan.
     *
     * 3. Si el usuario NO tiene plan activo:
     * · Se crea un nuevo UserPlan desde hoy hasta hoy + 1 mes.
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

        Optional<UserPlan> actualOpt = userPlanRepository.findByUserIdAndCanceledFalse(userId);

        if (actualOpt.isPresent()) {
            UserPlan actual = actualOpt.get();

            // Si es el mismo plan (PREMIUM), extender en vez de cancelar
            if (actual.getPlan().getType() == tipoPlan) {

                LocalDate nuevaFecha;

                if (actual.getEndDate().isAfter(today())) {
                    // endDate > today -> todavía tiene días por delante: extender desde endDate
                    nuevaFecha = actual.getEndDate().plusMonths(1);
                } else if (actual.getEndDate().isEqual(today())) {
                    // endDate == today -> último día: extender desde mañana para no perder ese día
                    nuevaFecha = today().plusDays(1).plusMonths(1);
                } else {
                    // endDate < today -> plan vencido -> extender desde hoy
                    nuevaFecha = today().plusMonths(1);
                }

                actual.setEndDate(nuevaFecha);
                return userPlanRepository.save(actual);
            }

            // Si cambia de plan, entonces sí cancelar
            actual.setCanceled(true);
            actual.setEndDate(today());
            userPlanRepository.save(actual);

            // Forzar sincronización ANTES de crear el nuevo plan
            userPlanRepository.findByUserIdAndCanceledFalse(userId);
        }

        // Crear un nuevo plan
        UserPlan nuevo = new UserPlan();
        nuevo.setUser(user);
        nuevo.setPlan(plan);
        nuevo.setStartDate(today());
        nuevo.setEndDate(today().plusMonths(1));
        nuevo.setCanceled(false);

        return userPlanRepository.save(nuevo);
    }

}
