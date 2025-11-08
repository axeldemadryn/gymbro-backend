-- 🟩 MÚSCULOS
INSERT INTO musculos(nombre) VALUES
-- pecho
('Pectoral mayor'), ('Pectoral menor'),

-- espalda
('Dorsal ancho'), ('Romboides'), ('Trapecio superior'), ('Trapecio medio'), ('Trapecio inferior'),
('Erectores espinales (torácico)'), ('Erectores espinales (lumbar)'), ('Redondo mayor'), ('Redondo menor'),
('Infraespinoso'), ('Supraespinoso'),

-- hombros
('Deltoides anterior'), ('Deltoides lateral'), ('Deltoides posterior'), ('Serrato anterior'),

-- brazos
('Bíceps braquial'), ('Braquial anterior'), ('Tríceps braquial'), ('Ancóneo'), ('Antebrazo (flexores y extensores)'),

-- piernas (tren inferior)
('Cuádriceps (recto femoral)'), ('Cuádriceps (vasto medial)'), ('Cuádriceps (vasto lateral)'), ('Cuádriceps (vasto intermedio)'),
('Isquiotibiales (bíceps femoral)'), ('Isquiotibiales (semitendinoso)'), ('Isquiotibiales (semimembranoso)'), ('Glúteo mayor'),
('Glúteo medio'), ('Glúteo menor'), ('Aductores (grupo)'), ('Abductores (grupo)'), ('Sartorio'), ('Tensor de la fascia lata'),
('Gemelos (gastrocnemios)'), ('Sóleo'),

-- core/abdomen
('Recto abdominal'), ('Oblicuo externo'), ('Oblicuo interno'), ('Transverso del abdomen'),

-- multifuncionales/compuestos
('Músculos de tracción (general)'), ('Músculos de empuje (general)'), ('Estabilizadores del core'), ('Músculos posturales');

-- 🟦 MÁQUINAS con URLs de imágenes asociadas.
INSERT INTO maquinas (nombre, nombre_traducido, tipo_equipo, descripcion, imagen_url) VALUES
('Chest Press Machine', 'Máquina Chest Press', 'MAQUINA_PECHO', 'Máquina para press de pecho sentado', '/imagenes/chest-press-machine.jpg'),
('Lat Pull Down', 'Máquina de Jalón', 'POLEAS', 'Máquina para jalones dorsales', '/imagenes/lat-pull-down.jpg'),
('Seated Cable Rows', 'Remo en Polea Sentado', 'POLEAS', 'Remo sentado con polea baja', '/imagenes/seated-cable-row.jpg'),
('Arm Curl Machine', 'Máquina de Curl de Brazos', 'MAQUINA_HOMBRO', 'Máquina para curl de brazos', '/imagenes/arm-curl.jpeg'),
('Chest Fly Machine', 'Máquina de Aperturas de Pecho', 'MAQUINA_PECHO', 'Máquina de apertura de pecho', '/imagenes/chest-fly.jpeg'),
('Chinning Dipping', 'Estación de Dominadas y Fondos', 'MULTIFUNCIONAL', 'Estación para dominadas y fondos', '/imagenes/chinning-dipping.jpeg'),
('Lateral Raises Machine', 'Máquina de Elevaciones Laterales', 'MAQUINA_HOMBRO', 'Máquina para elevaciones laterales de hombros', '/imagenes/lateral-raises.jpeg'),
('Leg Extension', 'Máquina de Extensión de Piernas', NULL, 'Máquina de extensión de cuadríceps', '/imagenes/leg-extension.jpeg'),
('Leg Press', 'Prensa de Piernas', NULL, 'Prensa de piernas', '/imagenes/leg-press.jpeg'),
('Leg Curl Machine', 'Máquina de Curl de Piernas', NULL, 'Máquina para curl de piernas', '/imagenes/leg-curl.jpeg'),
('Seated Dip Machine', 'Máquina de Fondos Sentado', NULL, 'Máquina para fondos de tríceps sentado', '/imagenes/seated-dip.jpg'),
('Shoulder Press Machine', 'Máquina de Press de Hombros', 'MAQUINA_HOMBRO', 'Máquina para press de hombros', '/imagenes/shoulder-press.jpeg'),
('Smith Machine', 'Máquina Smith', 'MULTIFUNCIONAL', 'Máquina multipower con barra guiada', '/imagenes/smith-machine.jpeg'),
('Hack Squat Machine', 'Máquina de Sentadilla Hack', NULL, 'Máquina para sentadilla hack', '/imagenes/hack-sqat.jpeg');

-- 🟨 EJERCICIOS GLOBALES (no están asociados a ningún video en principio).
INSERT INTO ejercicios (nombre, tipo, descripcion, video_url, user_id) VALUES

-- Pecho
('Press de Pecho en Máquina', 'FUERZA', 'Ejercicio para pectorales en máquina de press sentado.', NULL, NULL),
('Aperturas en Máquina de Pecho', 'FUERZA', 'Ejercicio para pectorales con máquina de apertura.', NULL, NULL),

-- Espalda
('Jalón al Pecho', 'FUERZA', 'Ejercicio para dorsales en máquina de polea alta.', NULL, NULL),
('Remo Sentado en Polea Baja', 'FUERZA', 'Remo para espalda media en polea baja.', NULL, NULL),
('Pull Over en Polea', 'FUERZA', 'Ejercicio de dorsales con polea alta.', NULL, NULL),

-- Brazos
('Curl de Bíceps en Máquina', 'FUERZA', 'Ejercicio para bíceps usando máquina de curl.', NULL, NULL),
('Fondos en Máquina Sentado', 'FUERZA', 'Ejercicio para tríceps en máquina de dips sentado.', NULL, NULL),
('Extensión de Tríceps en Polea', 'FUERZA', 'Ejercicio para tríceps en polea alta.', NULL, NULL),

-- Hombros
('Press de Hombros en Máquina', 'FUERZA', 'Ejercicio de empuje vertical para deltoides.', NULL, NULL),
('Elevaciones Laterales en Máquina', 'FUERZA', 'Ejercicio de hombros para deltoides laterales.', NULL, NULL),

-- Piernas
('Extensión de Piernas', 'FUERZA', 'Ejercicio para cuadríceps en máquina de extensiones.', NULL, NULL),
('Curl de Piernas en Máquina', 'FUERZA', 'Ejercicio para isquiotibiales en máquina de curl.', NULL, NULL),
('Prensa de Piernas', 'FUERZA', 'Ejercicio compuesto para tren inferior en máquina de prensa.', NULL, NULL),
('Sentadilla Hack', 'FUERZA', 'Ejercicio para cuádriceps y glúteos en máquina hack squat.', NULL, NULL),

-- Multifuncionales / Compuestos
('Dominadas en Estación Multifuncional', 'FUERZA', 'Ejercicio de tracción vertical en estación de dominadas.', NULL, NULL),
('Fondos en Estación Multifuncional', 'FUERZA', 'Ejercicio para tríceps y pectorales en estación multifuncional.', NULL, NULL),
('Press de Pecho en Smith Machine', 'FUERZA', 'Press de banca guiado en multipower Smith.', NULL, NULL),
('Sentadilla en Smith Machine', 'FUERZA', 'Sentadilla guiada en multipower Smith.', NULL, NULL);

-- 🟧 TABLA MÁQUINA-MÚSCULOS
INSERT INTO maquina_musculos (maquina_id, musculo_id)
SELECT
    maquinas.id,
    musculos.id
FROM maquinas
CROSS JOIN musculos
WHERE (maquinas.nombre, musculos.nombre) IN (
    -- Pecho
    ('Chest Press Machine', 'Pectoral mayor'),
    ('Chest Press Machine', 'Deltoides anterior'),
    ('Chest Press Machine', 'Tríceps braquial'),
    ('Chest Fly Machine', 'Pectoral mayor'),
    ('Chest Fly Machine', 'Pectoral menor'),
    ('Chest Fly Machine', 'Deltoides anterior'),

    -- Espalda/poleas
    ('Lat Pull Down', 'Dorsal ancho'),
    ('Lat Pull Down', 'Bíceps braquial'),
    ('Lat Pull Down', 'Trapecio medio'),
    ('Seated Cable Rows', 'Romboides'),
    ('Seated Cable Rows', 'Trapecio medio'),
    ('Seated Cable Rows', 'Dorsal ancho'),
    ('Seated Cable Rows', 'Bíceps braquial'),

    -- Brazos
    ('Arm Curl Machine', 'Bíceps braquial'),
    ('Arm Curl Machine', 'Braquial anterior'),
    ('Seated Dip Machine', 'Tríceps braquial'),
    ('Seated Dip Machine', 'Pectoral mayor'),
    ('Seated Dip Machine', 'Deltoides anterior'),

    -- Hombros
    ('Lateral Raises Machine', 'Deltoides lateral'),
    ('Lateral Raises Machine', 'Deltoides anterior'),
    ('Shoulder Press Machine', 'Deltoides anterior'),
    ('Shoulder Press Machine', 'Deltoides lateral'),
    ('Shoulder Press Machine', 'Tríceps braquial'),

    -- Piernas - Leg Extension (todos los cuádriceps)
    ('Leg Extension', 'Cuádriceps (recto femoral)'),
    ('Leg Extension', 'Cuádriceps (vasto medial)'),
    ('Leg Extension', 'Cuádriceps (vasto lateral)'),
    ('Leg Extension', 'Cuádriceps (vasto intermedio)'),
    
    -- Piernas - Leg Press (cuádriceps, glúteos e isquiotibiales)
    ('Leg Press', 'Cuádriceps (recto femoral)'),
    ('Leg Press', 'Cuádriceps (vasto medial)'),
    ('Leg Press', 'Cuádriceps (vasto lateral)'),
    ('Leg Press', 'Cuádriceps (vasto intermedio)'),
    ('Leg Press', 'Glúteo mayor'),
    ('Leg Press', 'Isquiotibiales (bíceps femoral)'),
    ('Leg Press', 'Isquiotibiales (semitendinoso)'),
    ('Leg Press', 'Isquiotibiales (semimembranoso)'),
    
    -- Piernas - Leg Curl (isquiotibiales)
    ('Leg Curl Machine', 'Isquiotibiales (bíceps femoral)'),
    ('Leg Curl Machine', 'Isquiotibiales (semitendinoso)'),
    ('Leg Curl Machine', 'Isquiotibiales (semimembranoso)'),
    
    -- Piernas - Hack Squat (cuádriceps, glúteos e isquiotibiales)
    ('Hack Squat Machine', 'Cuádriceps (recto femoral)'),
    ('Hack Squat Machine', 'Cuádriceps (vasto medial)'),
    ('Hack Squat Machine', 'Cuádriceps (vasto lateral)'),
    ('Hack Squat Machine', 'Cuádriceps (vasto intermedio)'),
    ('Hack Squat Machine', 'Glúteo mayor'),
    ('Hack Squat Machine', 'Isquiotibiales (bíceps femoral)'),

    -- Multifuncionales - Chinning Dipping (dominadas y fondos)
    ('Chinning Dipping', 'Dorsal ancho'),
    ('Chinning Dipping', 'Tríceps braquial'),
    ('Chinning Dipping', 'Pectoral mayor'),
    ('Chinning Dipping', 'Bíceps braquial'),
    ('Chinning Dipping', 'Deltoides anterior'),
    
    -- Multifuncionales - Smith Machine (versatilidad para pecho y piernas)
    ('Smith Machine', 'Pectoral mayor'),
    ('Smith Machine', 'Cuádriceps (recto femoral)'),
    ('Smith Machine', 'Cuádriceps (vasto medial)'),
    ('Smith Machine', 'Cuádriceps (vasto lateral)'),
    ('Smith Machine', 'Cuádriceps (vasto intermedio)'),
    ('Smith Machine', 'Glúteo mayor'),
    ('Smith Machine', 'Deltoides anterior'),
    ('Smith Machine', 'Tríceps braquial')
);

-- 🟥 TABLA EJERCICIO-MÁQUINAS
INSERT INTO ejercicio_maquinas (ejercicio_id, maquina_id)
SELECT
    ejercicios.id,
    maquinas.id
FROM ejercicios
CROSS JOIN maquinas
WHERE (ejercicios.nombre, maquinas.nombre) IN (
    -- Pecho
    ('Press de Pecho en Máquina', 'Chest Press Machine'),
    ('Aperturas en Máquina de Pecho', 'Chest Fly Machine'),
    -- Espalda
    ('Jalón al Pecho', 'Lat Pull Down'),
    ('Remo Sentado en Polea Baja', 'Seated Cable Rows'),
    ('Pull Over en Polea', 'Lat Pull Down'),
    -- Brazos
    ('Curl de Bíceps en Máquina', 'Arm Curl Machine'),
    ('Fondos en Máquina Sentado', 'Seated Dip Machine'),
    ('Extensión de Tríceps en Polea', 'Lat Pull Down'),
    -- Hombros
    ('Press de Hombros en Máquina', 'Shoulder Press Machine'),
    ('Elevaciones Laterales en Máquina', 'Lateral Raises Machine'),
    -- Piernas
    ('Extensión de Piernas', 'Leg Extension'),
    ('Curl de Piernas en Máquina', 'Leg Curl Machine'),
    ('Prensa de Piernas', 'Leg Press'),
    ('Sentadilla Hack', 'Hack Squat Machine'),
    -- Multifuncionales / Compuestos
    ('Dominadas en Estación Multifuncional', 'Chinning Dipping'),
    ('Fondos en Estación Multifuncional', 'Chinning Dipping'),
    ('Press de Pecho en Smith Machine', 'Smith Machine'),
    ('Sentadilla en Smith Machine', 'Smith Machine')
);

-- 🟪 TABLA EJERCICIO-MÚSCULOS
INSERT INTO ejercicio_musculos (ejercicio_id, musculo_id)
SELECT
    ejercicios.id,
    musculos.id
FROM ejercicios
CROSS JOIN musculos
WHERE (ejercicios.nombre, musculos.nombre) IN (
    -- Pecho
    ('Press de Pecho en Máquina', 'Pectoral mayor'),
    ('Press de Pecho en Máquina', 'Deltoides anterior'),
    ('Press de Pecho en Máquina', 'Tríceps braquial'),
    ('Aperturas en Máquina de Pecho', 'Pectoral mayor'),
    ('Aperturas en Máquina de Pecho', 'Pectoral menor'),
    ('Aperturas en Máquina de Pecho', 'Deltoides anterior'),

    -- Espalda
    ('Jalón al Pecho', 'Dorsal ancho'),
    ('Jalón al Pecho', 'Romboides'),
    ('Jalón al Pecho', 'Bíceps braquial'),
    ('Remo Sentado en Polea Baja', 'Romboides'),
    ('Remo Sentado en Polea Baja', 'Trapecio medio'),
    ('Remo Sentado en Polea Baja', 'Dorsal ancho'),
    ('Remo Sentado en Polea Baja', 'Bíceps braquial'),
    ('Pull Over en Polea', 'Dorsal ancho'),
    ('Pull Over en Polea', 'Pectoral mayor'),

    -- Brazos
    ('Curl de Bíceps en Máquina', 'Bíceps braquial'),
    ('Curl de Bíceps en Máquina', 'Braquial anterior'),
    ('Fondos en Máquina Sentado', 'Tríceps braquial'),
    ('Fondos en Máquina Sentado', 'Pectoral mayor'),
    ('Extensión de Tríceps en Polea', 'Tríceps braquial'),

    -- Hombros
    ('Press de Hombros en Máquina', 'Deltoides anterior'),
    ('Press de Hombros en Máquina', 'Deltoides lateral'),
    ('Press de Hombros en Máquina', 'Tríceps braquial'),
    ('Elevaciones Laterales en Máquina', 'Deltoides lateral'),
    ('Elevaciones Laterales en Máquina', 'Deltoides anterior'),

    -- Piernas
    ('Extensión de Piernas', 'Cuádriceps (recto femoral)'),
    ('Extensión de Piernas', 'Cuádriceps (vasto medial)'),
    ('Extensión de Piernas', 'Cuádriceps (vasto lateral)'),
    ('Extensión de Piernas', 'Cuádriceps (vasto intermedio)'),
    ('Curl de Piernas en Máquina', 'Isquiotibiales (bíceps femoral)'),
    ('Curl de Piernas en Máquina', 'Isquiotibiales (semitendinoso)'),
    ('Curl de Piernas en Máquina', 'Isquiotibiales (semimembranoso)'),
    ('Prensa de Piernas', 'Cuádriceps (recto femoral)'),
    ('Prensa de Piernas', 'Cuádriceps (vasto medial)'),
    ('Prensa de Piernas', 'Cuádriceps (vasto lateral)'),
    ('Prensa de Piernas', 'Cuádriceps (vasto intermedio)'),
    ('Prensa de Piernas', 'Glúteo mayor'),
    ('Prensa de Piernas', 'Isquiotibiales (bíceps femoral)'),
    ('Sentadilla Hack', 'Cuádriceps (recto femoral)'),
    ('Sentadilla Hack', 'Cuádriceps (vasto medial)'),
    ('Sentadilla Hack', 'Cuádriceps (vasto lateral)'),
    ('Sentadilla Hack', 'Cuádriceps (vasto intermedio)'),
    ('Sentadilla Hack', 'Glúteo mayor'),
    ('Sentadilla Hack', 'Isquiotibiales (bíceps femoral)'),

    -- Multifuncionales / Compuestos
    ('Dominadas en Estación Multifuncional', 'Dorsal ancho'),
    ('Dominadas en Estación Multifuncional', 'Músculos de tracción (general)'),
    ('Dominadas en Estación Multifuncional', 'Bíceps braquial'),
    ('Fondos en Estación Multifuncional', 'Tríceps braquial'),
    ('Fondos en Estación Multifuncional', 'Músculos de empuje (general)'),
    ('Fondos en Estación Multifuncional', 'Pectoral mayor'),
    ('Press de Pecho en Smith Machine', 'Pectoral mayor'),
    ('Press de Pecho en Smith Machine', 'Deltoides anterior'),
    ('Press de Pecho en Smith Machine', 'Tríceps braquial'),
    ('Sentadilla en Smith Machine', 'Cuádriceps (recto femoral)'),
    ('Sentadilla en Smith Machine', 'Cuádriceps (vasto medial)'),
    ('Sentadilla en Smith Machine', 'Cuádriceps (vasto lateral)'),
    ('Sentadilla en Smith Machine', 'Cuádriceps (vasto intermedio)'),
    ('Sentadilla en Smith Machine', 'Glúteo mayor'),
    ('Sentadilla en Smith Machine', 'Estabilizadores del core')
);

-- ⚫ SESIONES (para todos los usuarios)
INSERT INTO sessions (name, description, user_id)
SELECT
    sesion_basico.nombre,
    sesion_basico.descripcion,
    users.id
FROM users
CROSS JOIN (
    VALUES
        ('Sesion Pecho', 'Rutina de pecho con máquinas básicas'),
        ('Sesion Espalda', 'Rutina de espalda y dorsales'),
        ('Sesion Piernas', 'Rutina de piernas con enfoque en fuerza'),
        ('Sesion Brazos', 'Rutina de biceps y triceps'),
        ('Sesion Hombros', 'Rutina de hombros y deltoides'),
        ('Sesion Full Body', 'Rutina de cuerpo completo'),
        ('Sesion Vacía', 'Rutina vacia')
) AS sesion_basico(nombre, descripcion);

-- 🟩 TABLA SESSION-EXERCISE
INSERT INTO session_exercises(session_id, exercise_id, sets, reps)
SELECT
    sessions.id,
    ejercicios.id,
    sesion_ejercicio_basico.series,
    sesion_ejercicio_basico.repeticiones
FROM (
    VALUES
        ('Sesion Pecho', 'Press de Pecho en Máquina', 4, 12),
        ('Sesion Pecho', 'Aperturas en Máquina de Pecho', 4, 12),
        ('Sesion Pecho', 'Press de Pecho en Smith Machine', 3, 10),
        ('Sesion Pecho', 'Fondos en Máquina de Pecho', 3, 12),
        ('Sesion Espalda', 'Jalón al Pecho', 4, 12),
        ('Sesion Espalda', 'Remo Sentado en Polea Baja', 4, 12),
        ('Sesion Espalda', 'Pull Over en Polea', 3, 15),
        ('Sesion Espalda', 'Dominadas en Estación Multifuncional', 3, 8),
        ('Sesion Piernas', 'Extensión de Piernas', 4, 15),
        ('Sesion Piernas', 'Curl de Piernas en Máquina', 4, 15),
        ('Sesion Piernas', 'Prensa de Piernas', 4, 12),
        ('Sesion Piernas', 'Sentadilla Hack', 4, 10),
        ('Sesion Piernas', 'Sentadilla en Smith Machine', 3, 12),
        ('Sesion Brazos', 'Curl de Bíceps en Máquina', 3, 12),
        ('Sesion Brazos', 'Fondos en Máquina Sentado', 3, 12),
        ('Sesion Brazos', 'Extensión de Tríceps en Polea', 3, 15),
        ('Sesion Hombros', 'Press de Hombros en Máquina', 4, 12),
        ('Sesion Hombros', 'Elevaciones Laterales en Máquina', 3, 15),
        ('Sesion Full Body', 'Press de Pecho en Máquina', 3, 12),
        ('Sesion Full Body', 'Jalón al Pecho', 3, 12),
        ('Sesion Full Body', 'Press de Hombros en Máquina', 3, 12),
        ('Sesion Full Body', 'Prensa de Piernas', 3, 12),
        ('Sesion Full Body', 'Curl de Bíceps en Máquina', 2, 12),
        ('Sesion Full Body', 'Extensión de Tríceps en Polea', 2, 12)
) AS sesion_ejercicio_basico(nombre_sesion, nombre_ejercicio, series, repeticiones)
INNER JOIN sessions ON sessions.name = sesion_ejercicio_basico.nombre_sesion
INNER JOIN ejercicios ON ejercicios.nombre = sesion_ejercicio_basico.nombre_ejercicio;

-- 🟦 RUTINAS SEMANALES (para todos los usuarios)
INSERT INTO weekly_routines (name, start_date, end_date, user_id)
SELECT
    rutinas_semanales_basico.nombre,
    rutinas_semanales_basico.inicio::date,
    rutinas_semanales_basico.fin::date,
    users.id
FROM users
CROSS JOIN (
    VALUES
        ('Semana Iniciación', '2024-01-01', '2024-01-07'),
        ('Semana Fuerza', '2024-01-08', '2024-01-14'),
        ('Semana Hipertrofia', '2024-01-15', '2024-01-21'),
        ('Semana Descarga', '2024-01-22', '2024-01-28')
) AS rutinas_semanales_basico(nombre, inicio, fin);