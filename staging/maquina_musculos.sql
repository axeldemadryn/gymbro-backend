INSERT INTO maquina_musculos (maquina_id, musculo_id) VALUES

-- MÁQUINAS DE PECHO (IDs 1 y 5)
(1, 1), -- Chest Press -> Pectoral mayor
(1, 11), -- Chest Press -> Deltoides anterior
(1, 17), -- Chest Press -> Tríceps braquial
(5, 1), -- Chest Fly -> Pectoral mayor
(5, 2), -- Chest Fly -> Pectoral menor
(5, 11), -- Chest Fly -> Deltoides anterior

-- MÁQUINAS DE ESPALDA/POLEAS (IDs 2 y 3)
(2, 3), -- Lat Pull Down (Polea) -> Dorsal ancho
(2, 15), -- Lat Pull Down (Polea) -> Bíceps braquial
(2, 17), -- Lat Pull Down (Polea) -> Tríceps braquial (para extensiones)
(3, 4), -- Seated Cable Rows -> Romboides
(3, 6), -- Seated Cable Rows -> Trapecio medio
(3, 3), -- Seated Cable Rows -> Dorsal ancho
(3, 15), -- Seated Cable Rows -> Bíceps braquial

-- MÁQUINAS DE BRAZOS (IDs 4 y 11)
(4, 15), -- Arm Curl -> Bíceps braquial
(4, 16), -- Arm Curl -> Braquial anterior
(11, 17), -- Seated Dip Machine -> Tríceps braquial
(11, 1), -- Seated Dip Machine -> Pectoral mayor
(11, 11), -- Seated Dip Machine -> Deltoides anterior

-- MÁQUINAS DE HOMBROS (IDs 7 y 12)
(7, 12), -- Lateral Raises Machine -> Deltoides lateral
(7, 11), -- Lateral Raises Machine -> Deltoides anterior
(12, 11), -- Shoulder Press Machine -> Deltoides anterior
(12, 12), -- Shoulder Press Machine -> Deltoides lateral
(12, 17), -- Shoulder Press Machine -> Tríceps braquial

-- MÁQUINAS DE PIERNAS (IDs 8, 9, 10 y 14)
(8, 20), -- Leg Extension -> Cuádriceps
(9, 20), -- Leg Press -> Cuádriceps
(9, 27), -- Leg Press -> Glúteo mayor
(9, 24), -- Leg Press -> Isquiotibiales
(10, 24), -- Leg Curl Machine -> Isquiotibiales
(14, 20), -- Hack Squat Machine -> Cuádriceps
(14, 27), -- Hack Squat Machine -> Glúteo mayor
(14, 24), -- Hack Squat Machine -> Isquiotibiales

-- MÁQUINAS MULTIFUNCIONALES (IDs 6 y 13)
(6, 3), -- Chinning Dipping -> Dorsal ancho (Dominadas)
(6, 17), -- Chinning Dipping -> Tríceps braquial (Fondos)
(6, 1), -- Chinning Dipping -> Pectoral mayor (Fondos)
(6, 15), -- Chinning Dipping -> Bíceps braquial (Dominadas)
(13, 1), -- Smith Machine -> Pectoral mayor (Press)
(13, 20), -- Smith Machine -> Cuádriceps (Sentadilla)
(13, 27), -- Smith Machine -> Glúteo mayor (Sentadilla)
(13, 11), -- Smith Machine -> Deltoides anterior (Press)
(13, 17); -- Smith Machine -> Tríceps braquial (Press)