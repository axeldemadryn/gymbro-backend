-- Asociaciones ejercicios ↔ máquinas

-- Pecho
INSERT INTO ejercicio_maquinas (ejercicio_id, maquina_id) VALUES
(1, 1), -- Press Pecho → Chest Press Machine
(2, 5), -- Aperturas → Chest Fly Machine

-- Espalda
(3, 2), -- Jalón al Pecho → Lat Pull Down
(4, 3), -- Remo Sentado → Seated Cable Rows
(5, 2), -- Pull Over → Lat Pull Down

-- Brazos
(6, 4), -- Curl Biceps → Arm Curl Machine
(7, 11), -- Fondos Máquina Sentado → Seated Dip Machine
(8, 2), -- Extensión Tríceps → Polea (Lat Pull Down)

-- Hombros
(9, 12), -- Press Hombros → Shoulder Press Machine
(10, 7), -- Elevaciones Laterales → Lateral Raises Machine

-- Piernas
(11, 8), -- Extensión Piernas → Leg Extension
(12, 10), -- Curl Piernas → Leg Curl Machine
(13, 9), -- Prensa Piernas → Leg Press
(14, 14), -- Sentadilla Hack → Hack Squat Machine

-- Multifuncionales
(15, 6), -- Dominadas → Chinning Dipping
(16, 6), -- Fondos → Chinning Dipping
(17, 13), -- Press Pecho Smith → Smith Machine
(18, 13); -- Sentadilla Smith → Smith Machine