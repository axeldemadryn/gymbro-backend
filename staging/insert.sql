-- 🟩 MÚSCULOS
INSERT INTO musculos(id, nombre) VALUES

-- pecho
(1, 'Pectoral mayor'),
(2, 'Pectoral menor'),

-- espalda
(3, 'Dorsal ancho'),
(4, 'Romboides'),
(5, 'Trapecio superior'),
(6, 'Trapecio medio'),
(7, 'Trapecio inferior'),
(8, 'Erectores espinales (lumbar)'),
(9, 'Redondo mayor'),
(10, 'Redondo menor'),

-- hombros
(11, 'Deltoides anterior'),
(12, 'Deltoides lateral'),
(13, 'Deltoides posterior'),
(14, 'Serrato anterior'),

-- brazos
(15, 'Bíceps braquial'),
(16, 'Braquial anterior'),
(17, 'Tríceps braquial'),
(18, 'Ancóneo'),
(19, 'Antebrazo (flexores y extensores)'),

-- piernas (tren inferior)
(20, 'Cuádriceps (recto femoral)'),
(21, 'Vasto medial'),
(22, 'Vasto lateral'),
(23, 'Vasto intermedio'),
(24, 'Isquiotibiales (bíceps femoral)'),
(25, 'Semitendinoso'),
(26, 'Semimembranoso'),
(27, 'Glúteo mayor'),
(28, 'Glúteo medio'),
(29, 'Glúteo menor'),
(30, 'Aductores (grupo)'),
(31, 'Abductores (grupo)'),
(32, 'Sartorio'),
(33, 'Tensor de la fascia lata'),
(34, 'Gemelos (gastrocnemios)'),
(35, 'Sóleo'),

-- core/abdomen
(36, 'Recto abdominal'),
(37, 'Oblicuo externo'),
(38, 'Oblicuo interno'),
(39, 'Transverso del abdomen'),

-- multifuncionales/compuestos
(40, 'Músculos de tracción (general)'),
(41, 'Músculos de empuje (general)'),
(42, 'Estabilizadores del core'),
(43, 'Músculos posturales');

-- 🟦 MÁQUINAS con URLs de imágenes asociadas.

INSERT INTO maquinas (id, nombre, nombre_traducido, tipo_equipo, descripcion, imagen_url) VALUES
(1, 'Chest Press Machine', 'Máquina Chest Press', 'MAQUINA_PECHO', 'Máquina para press de pecho sentado', '/imagenes/chest-press-machine.jpg'),
(2, 'Lat Pull Down', 'Máquina de Jalón', 'POLEAS', 'Máquina para jalones dorsales', '/imagenes/lat-pull-down.jpg'),
(3, 'Seated Cable Rows', 'Remo en Polea Sentado', 'POLEAS', 'Remo sentado con polea baja', '/imagenes/seated-cable-row.jpg'),
(4, 'Arm Curl Machine', 'Máquina de Curl de Brazos', 'MAQUINA_HOMBRO', 'Máquina para curl de brazos', '/imagenes/arm-curl.jpeg'),
(5, 'Chest Fly Machine', 'Máquina de Aperturas de Pecho', 'MAQUINA_PECHO', 'Máquina de apertura de pecho', '/imagenes/chest-fly.jpeg'),
(6, 'Chinning Dipping', 'Estación de Dominadas y Fondos', 'MULTIFUNCIONAL', 'Estación para dominadas y fondos', '/imagenes/chinning-dipping.jpeg'),
(7, 'Lateral Raises Machine', 'Máquina de Elevaciones Laterales', 'MAQUINA_HOMBRO', 'Máquina para elevaciones laterales de hombros', '/imagenes/lateral-raises.jpeg'),
(8, 'Leg Extension', 'Máquina de Extensión de Piernas', NULL, 'Máquina de extensión de cuadríceps', '/imagenes/leg-extension.jpeg'),
(9, 'Leg Press', 'Prensa de Piernas', NULL, 'Prensa de piernas', '/imagenes/leg-press.jpeg'),
(10, 'Leg Curl Machine', 'Máquina de Curl de Piernas', NULL, 'Máquina para curl de piernas', '/imagenes/leg-curl.jpeg'),
(11, 'Seated Dip Machine', 'Máquina de Fondos Sentado', NULL, 'Máquina para fondos de tríceps sentado', '/imagenes/seated-dip.jpg'),
(12, 'Shoulder Press Machine', 'Máquina de Press de Hombros', 'MAQUINA_HOMBRO', 'Máquina para press de hombros', '/imagenes/shoulder-press.jpeg'),
(13, 'Smith Machine', 'Máquina Smith', 'MULTIFUNCIONAL', 'Máquina multipower con barra guiada', '/imagenes/smith-machine.jpeg'),
(14, 'Hack Squat Machine', 'Máquina de Sentadilla Hack', NULL, 'Máquina para sentadilla hack', '/imagenes/hack-sqat.jpeg');

-- 🟨 EJERCICIOS, no asociados a ningún video en principio. Se asocian a todos los usuarios existentes en la BD.
INSERT INTO ejercicios (id, nombre, tipo, descripcion, video_url, user_id)
SELECT 
    ejercicio_basico.id,
    ejercicio_basico.nombre,
    ejercicio_basico.tipo,
    ejercicio_basico.descripcion,
    ejercicio_basico.video_url,
    u.id
FROM users u
CROSS JOIN (
    VALUES 
        -- Pecho
        (1, 'Press de Pecho en Máquina', 'FUERZA', 'Ejercicio para pectorales en máquina de press sentado.', NULL),
        (2, 'Aperturas en Máquina de Pecho', 'FUERZA', 'Ejercicio para pectorales con máquina de apertura.', NULL),
        (3, 'Jalón al Pecho', 'FUERZA', 'Ejercicio para dorsales en máquina de polea alta.', NULL),
        (4, 'Remo Sentado en Polea Baja', 'FUERZA', 'Remo para espalda media en polea baja.', NULL),
        (5, 'Pull Over en Polea', 'FUERZA', 'Ejercicio de dorsales con polea alta.', NULL),

        -- Brazos
        (6, 'Curl de Bíceps en Máquina', 'FUERZA', 'Ejercicio para bíceps usando máquina de curl.', NULL),
        (7, 'Fondos en Máquina Sentado', 'FUERZA', 'Ejercicio para tríceps en máquina de dips sentado.', NULL),
        (8, 'Extensión de Tríceps en Polea', 'FUERZA', 'Ejercicio para tríceps en polea alta.', NULL),

        -- Hombros
        (9, 'Press de Hombros en Máquina', 'FUERZA', 'Ejercicio de empuje vertical para deltoides.', NULL),
        (10, 'Elevaciones Laterales en Máquina', 'FUERZA', 'Ejercicio de hombros para deltoides laterales.', NULL),

        -- Piernas
        (11, 'Extensión de Piernas', 'FUERZA', 'Ejercicio para cuadríceps en máquina de extensiones.', NULL),
        (12, 'Curl de Piernas en Máquina', 'FUERZA', 'Ejercicio para isquiotibiales en máquina de curl.', NULL),
        (13, 'Prensa de Piernas', 'FUERZA', 'Ejercicio compuesto para tren inferior en máquina de prensa.', NULL),
        (14, 'Sentadilla Hack', 'FUERZA', 'Ejercicio para cuádriceps y glúteos en máquina hack squat.', NULL),

        -- Multifuncionales / Compuestos
        (15, 'Dominadas en Estación Multifuncional', 'FUERZA', 'Ejercicio de tracción vertical en estación de dominadas.', NULL),
        (16, 'Fondos en Estación Multifuncional', 'FUERZA', 'Ejercicio para tríceps y pectorales en estación multifuncional.', NULL),
        (17, 'Press de Pecho en Smith Machine', 'FUERZA', 'Press de banca guiado en multipower Smith.', NULL),
        (18, 'Sentadilla en Smith Machine', 'FUERZA', 'Sentadilla guiada en multipower Smith.', NULL)
) AS ejercicio_basico(id, nombre, tipo, descripcion, video_url);

-- 🟧 TABLA MÁQUINA-MÚSCULOS

INSERT INTO maquina_musculos (maquina_id, musculo_id) VALUES

-- Pecho (IDs 1 y 5)
(1, 1), -- Chest Press -> Pectoral mayor
(1, 11), -- Chest Press -> Deltoides anterior
(1, 17), -- Chest Press -> Tríceps braquial
(5, 1), -- Chest Fly -> Pectoral mayor
(5, 2), -- Chest Fly -> Pectoral menor
(5, 11), -- Chest Fly -> Deltoides anterior

-- Espalda/poleas (IDs 2 y 3)
(2, 3), -- Lat Pull Down (Polea) -> Dorsal ancho
(2, 15), -- Lat Pull Down (Polea) -> Bíceps braquial
(2, 17), -- Lat Pull Down (Polea) -> Tríceps braquial (para extensiones)
(3, 4), -- Seated Cable Rows -> Romboides
(3, 6), -- Seated Cable Rows -> Trapecio medio
(3, 3), -- Seated Cable Rows -> Dorsal ancho
(3, 15), -- Seated Cable Rows -> Bíceps braquial

-- Brazos (IDs 4 y 11)
(4, 15), -- Arm Curl -> Bíceps braquial
(4, 16), -- Arm Curl -> Braquial anterior
(11, 17), -- Seated Dip Machine -> Tríceps braquial
(11, 1), -- Seated Dip Machine -> Pectoral mayor
(11, 11), -- Seated Dip Machine -> Deltoides anterior

-- Hombros (IDs 7 y 12)
(7, 12), -- Lateral Raises Machine -> Deltoides lateral
(7, 11), -- Lateral Raises Machine -> Deltoides anterior
(12, 11), -- Shoulder Press Machine -> Deltoides anterior
(12, 12), -- Shoulder Press Machine -> Deltoides lateral
(12, 17), -- Shoulder Press Machine -> Tríceps braquial

-- Piernas (IDs 8, 9, 10 y 14)
(8, 20), -- Leg Extension -> Cuádriceps
(9, 20), -- Leg Press -> Cuádriceps
(9, 27), -- Leg Press -> Glúteo mayor
(9, 24), -- Leg Press -> Isquiotibiales
(10, 24), -- Leg Curl Machine -> Isquiotibiales
(14, 20), -- Hack Squat Machine -> Cuádriceps
(14, 27), -- Hack Squat Machine -> Glúteo mayor
(14, 24), -- Hack Squat Machine -> Isquiotibiales

-- Multifuncionales (IDs 6 y 13)
(6, 3), -- Chinning Dipping -> Dorsal ancho (Dominadas)
(6, 17), -- Chinning Dipping -> Tríceps braquial (Fondos)
(6, 1), -- Chinning Dipping -> Pectoral mayor (Fondos)
(6, 15), -- Chinning Dipping -> Bíceps braquial (Dominadas)
(13, 1), -- Smith Machine -> Pectoral mayor (Press)
(13, 20), -- Smith Machine -> Cuádriceps (Sentadilla)
(13, 27), -- Smith Machine -> Glúteo mayor (Sentadilla)
(13, 11), -- Smith Machine -> Deltoides anterior (Press)
(13, 17); -- Smith Machine -> Tríceps braquial (Press)

-- 🟥 TABLA EJERCICIO-MÁQUINAS

INSERT INTO ejercicio_maquinas (ejercicio_id, maquina_id) VALUES

-- Pecho
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

-- 🟪 TABLA EJERCICIO-MÚSCULOS

INSERT INTO ejercicio_musculos (ejercicio_id, musculo_id) VALUES

-- Pecho (IDs 1 y 2)
(1, 1), -- Press Pecho Máquina -> Pectoral mayor
(1, 11), -- Press Pecho Máquina -> Deltoides anterior
(1, 17), -- Press Pecho Máquina -> Tríceps braquial (secundario)
(2, 1), -- Aperturas Pecho Máquina -> Pectoral mayor
(2, 2), -- Aperturas Pecho Máquina -> Pectoral menor
(2, 11), -- Aperturas Pecho Máquina -> Deltoides anterior (secundario)

-- Espalda (IDs 3, 4 y 5)
(3, 3), -- Jalón al Pecho -> Dorsal ancho
(3, 4), -- Jalón al Pecho -> Romboides
(3, 15), -- Jalón al Pecho -> Bíceps braquial (secundario)
(4, 4), -- Remo Sentado -> Romboides
(4, 6), -- Remo Sentado -> Trapecio medio
(4, 3), -- Remo Sentado -> Dorsal ancho (secundario)
(4, 15), -- Remo Sentado -> Bíceps braquial (secundario)
(5, 3), -- Pull Over Polea -> Dorsal ancho
(5, 1), -- Pull Over Polea -> Pectoral mayor (secundario)

-- Brazos (IDs 6, 7 y 8)
(6, 15), -- Curl Bíceps Máquina -> Bíceps braquial
(6, 16), -- Curl Bíceps Máquina -> Braquial anterior
(7, 17), -- Fondos Máquina Sentado -> Tríceps braquial
(7, 1), -- Fondos Máquina Sentado -> Pectoral mayor (secundario)
(8, 17), -- Extensión Tríceps Polea -> Tríceps braquial

-- Hombros (IDs 9 y 10)
(9, 11), -- Press Hombros Máquina -> Deltoides anterior
(9, 12), -- Press Hombros Máquina -> Deltoides lateral
(9, 17), -- Press Hombros Máquina -> Tríceps braquial (secundario)
(10, 12), -- Elevaciones Laterales Máquina -> Deltoides lateral
(10, 11), -- Elevaciones Laterales Máquina -> Deltoides anterior (secundario)

-- Piernas (IDs 11, 12, 13 y 14)
(11, 20), -- Extensión Piernas -> Cuádriceps
(12, 24), -- Curl Piernas Máquina -> Isquiotibiales
(13, 20), -- Prensa de Piernas -> Cuádriceps
(13, 27), -- Prensa de Piernas -> Glúteo mayor
(13, 24), -- Prensa de Piernas -> Isquiotibiales (secundario)
(14, 20), -- Sentadilla Hack -> Cuádriceps
(14, 27), -- Sentadilla Hack -> Glúteo mayor
(14, 24), -- Sentadilla Hack -> Isquiotibiales (secundario)

-- Multifincionales/compuestos (IDs 15, 16, 17 y 18)
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

-- ⚫ SESIONES
INSERT INTO sessions (id, name, description, user_id)
SELECT
    sesion_basico.id,
    sesion_basico.nombre,
    sesion_basico.descripcion
    users.id
FROM users
CROSS JOIN
    VALUES(
        (1, 'Sesion Pecho', 'Rutina de pecho con máquinas básicas'),
        (2, 'Sesion Espalda', 'Rutina de espalda y dorsales'),
        (3, 'Sesion Piernas', 'Rutina de piernas con enfoque en fuerza'),
        (4, 'Sesion Brazos', 'Rutina de biceps y triceps'),
        (5, 'Sesion Hombros', 'Rutina de hombros y deltoides'),
        (6, 'Sesion Vacía', 'Rutina vacia')
) AS sesion_basico(id, nombre, descripcion);

-- 🟩 TABLA SESSION-EXERCISE

INSERT INTO session_exercises(id, session_id, exercise_id, sets, reps) VALUES
-- Sesion Pecho
(1, 1, 1, 4, 12), -- Press de Pecho en Máquina
(2, 1, 2, 4, 12), -- Aperturas en Máquina de Pecho

-- Sesion Espalda
(3, 2, 3, 4, 12), -- Jalón al Pecho
(4, 2, 4, 4, 12), -- Remo Sentado en Polea Baja
(5, 2, 5, 4, 12), -- Pull Over en Polea

-- Sesion Piernas
(6, 3, 11, 4, 12), -- Extensión de Piernas
(7, 3, 12, 4, 12), -- Curl de Piernas en Máquina
(8, 3, 13, 4, 12), -- Prensa de Piernas
(9, 3, 14, 4, 12), -- Sentadilla Hack

-- Sesion Brazos
(10, 4, 6, 3, 10), -- Curl de Bíceps en Máquina
(11, 4, 7, 3, 10), -- Fondos en Máquina Sentado
(12, 4, 8, 3, 10), -- Extensión de Tríceps en Polea

-- Sesion Hombros
(13, 5, 9, 3, 12),  -- Press de Hombros en Máquina
(14, 5, 10, 3, 12); -- Elevaciones Laterales en Máquina

-- 🟦 RUTINAS SEMANALES (sin rutinas diarias)

INSERT INTO weekly_routines (name, start_date, end_date, user_id)
SELECT
    rutinas_semanales_basico.nombre,
    rutinas_semanales_basico.inicio,
    rutinas_semanales_basico.fin,
    users.id
FROM users
CROSS JOIN
    VALUES(
        ('Semana X', '2023-10-09', '2023-10-15', 1),
        ('Semana Y', '2023-10-16', '2023-10-21', 1),
        ('Semana Z', '2023-10-23', '2023-10-29', 1)
) AS rutinas_semanales_basico(nombre, inicio, fin);