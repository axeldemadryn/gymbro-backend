package com.gym.backend.config;

import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import com.gym.backend.business.repositories.MusculoRepository;
import com.gym.backend.business.repositories.PlanRepository;
import com.gym.backend.model.Musculo;
import com.gym.backend.model.Plan;
import com.gym.backend.model.TipoPlan;

import jakarta.transaction.Transactional;

@Configuration
public class CargaInicialConfig {
    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private MusculoRepository musculoRepository;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void verificarEstadosIniciales() {
        if (planRepository.count() == 0) { // Si no hay planes cargados, los carga
            planRepository.save(new Plan(TipoPlan.GRATUITO));
            planRepository.save(new Plan(TipoPlan.PREMIUM));
        }

        if(musculoRepository.count() == 0){ // Si no hay músculos cargados, los carga
            musculoRepository.saveAll(Stream.of(
                // Pecho
                "Pectoral mayor", "Pectoral menor",

                // Espalda
                "Dorsal ancho", "Romboides", "Trapecio superior", "Trapecio medio", "Trapecio inferior",
                "Erectores espinales (torácico)", "Erectores espinales (lumbar)", "Redondo mayor", "Redondo menor", "Infraespinoso",
                "Supraespinoso",

                // Hombros
                "Deltoides anterior", "Deltoides lateral", "Deltoides posterior", "Serrato anterior",

                // Brazos
                "Bíceps braquial", "Braquial anterior", "Tríceps braquial", "Ancóneo", "Antebrazo (flexores y extensores)",

                // Piernas (tren inferior)
                "Cuádriceps (recto femoral)", "Cuádriceps (vasto medial)", "Cuádriceps (vasto lateral)", "Cuádriceps (vasto intermedio)",
                "Isquiotibiales (bíceps femoral)", "Isquiotibiales (semitendinoso)", "Isquiotibiales (semimembranoso)", "Glúteo mayor",
                "Glúteo medio", "Glúteo menor", "Aductores (grupo)", "Abductores (grupo)", "Sartorio", "Tensor de la fascia lata",
                "Gemelos (gastrocnemios)", "Sóleo",

                // Core/abdomen
                "Recto abdominal", "Oblicuo externo", "Oblicuo interno", "Transverso del abdomen",

                // Multifuncionales/compuestos
                "Músculos de tracción (general)", "Músculos de empuje (general)", "Estabilizadores del core", "Músculos posturales"
            ).map(Musculo::new).toList());
        }
    }
}
