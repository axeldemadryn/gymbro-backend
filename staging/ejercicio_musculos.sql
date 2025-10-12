INSERT INTO ejercicio_musculos (ejercicio_id, musculo_id) VALUES

-- EJERCICIOS DE PECHO (IDs 1 y 2)
(1, 1), -- Press Pecho Máquina -> Pectoral mayor
(1, 11), -- Press Pecho Máquina -> Deltoides anterior
(1, 17), -- Press Pecho Máquina -> Tríceps braquial (secundario)
(2, 1), -- Aperturas Pecho Máquina -> Pectoral mayor
(2, 2), -- Aperturas Pecho Máquina -> Pectoral menor
(2, 11), -- Aperturas Pecho Máquina -> Deltoides anterior (secundario)

-- EJERCICIOS DE ESPALDA (IDs 3, 4 y 5)
(3, 3), -- Jalón al Pecho -> Dorsal ancho
(3, 4), -- Jalón al Pecho -> Romboides
(3, 15), -- Jalón al Pecho -> Bíceps braquial (secundario)
(4, 4), -- Remo Sentado -> Romboides
(4, 6), -- Remo Sentado -> Trapecio medio
(4, 3), -- Remo Sentado -> Dorsal ancho (secundario)
(4, 15), -- Remo Sentado -> Bíceps braquial (secundario)
(5, 3), -- Pull Over Polea -> Dorsal ancho
(5, 1), -- Pull Over Polea -> Pectoral mayor (secundario)

-- EJERCICIOS DE BRAZOS (IDs 6, 7 y 8)
(6, 15), -- Curl Bíceps Máquina -> Bíceps braquial
(6, 16), -- Curl Bíceps Máquina -> Braquial anterior
(7, 17), -- Fondos Máquina Sentado -> Tríceps braquial
(7, 1), -- Fondos Máquina Sentado -> Pectoral mayor (secundario)
(8, 17), -- Extensión Tríceps Polea -> Tríceps braquial

-- EJERCICIOS DE HOMBROS (IDs 9 y 10)
(9, 11), -- Press Hombros Máquina -> Deltoides anterior
(9, 12), -- Press Hombros Máquina -> Deltoides lateral
(9, 17), -- Press Hombros Máquina -> Tríceps braquial (secundario)
(10, 12), -- Elevaciones Laterales Máquina -> Deltoides lateral
(10, 11), -- Elevaciones Laterales Máquina -> Deltoides anterior (secundario)

-- EJERCICIOS DE PIERNAS (IDs 11, 12, 13 y 14)
(11, 20), -- Extensión Piernas -> Cuádriceps
(12, 24), -- Curl Piernas Máquina -> Isquiotibiales
(13, 20), -- Prensa de Piernas -> Cuádriceps
(13, 27), -- Prensa de Piernas -> Glúteo mayor
(13, 24), -- Prensa de Piernas -> Isquiotibiales (secundario)
(14, 20), -- Sentadilla Hack -> Cuádriceps
(14, 27), -- Sentadilla Hack -> Glúteo mayor
(14, 24), -- Sentadilla Hack -> Isquiotibiales (secundario)

-- EJERCICIOS MULTIFUNCIONALES / COMPUESTOS (IDs 15, 16, 17 y 18)
(15, 3), -- Dominadas -> Dorsal ancho
(15, 40), -- Dominadas -> Músculos de tracción (general)
(15, 15), -- Dominadas -> Bíceps braquial (secundario)
(16, 17), -- Fondos Estación -> Tríceps braquial
(16, 41), -- Fondos Estación -> Músculos de empuje (general)
(16, 1), -- Fondos Estación -> Pectoral mayor (secundario)
(17, 1), -- Press Pecho Smith -> Pectoral mayor
(17, 11), -- Press Pecho Smith -> Deltoides anterior
(17, 17), -- Press Pecho Smith -> Tríceps braquial (secundario)
(18, 20), -- Sentadilla Smith -> Cuádriceps
(18, 27), -- Sentadilla Smith -> Glúteo mayor
(18, 42); -- Sentadilla Smith -> Estabilizadores del core (secundario)