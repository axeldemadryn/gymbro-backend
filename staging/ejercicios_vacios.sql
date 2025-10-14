-- Vacíos porque no están asociados a ningún músculo, ni máquina (en principio)
INSERT INTO ejercicios (id, nombre, tipo, descripcion, video_url, es_personalizado)
VALUES
-- Pecho
(1, 'Press de Pecho en Máquina', 'FUERZA', 'Ejercicio para pectorales en máquina de press sentado.', null, false),
(2, 'Aperturas en Máquina de Pecho', 'FUERZA', 'Ejercicio para pectorales con máquina de apertura.', null, false),

-- Espalda
(3, 'Jalón al Pecho', 'FUERZA', 'Ejercicio para dorsales en máquina de polea alta.', null, false),
(4, 'Remo Sentado en Polea Baja', 'FUERZA', 'Remo para espalda media en polea baja.', null, false),
(5, 'Pull Over en Polea', 'FUERZA', 'Ejercicio de dorsales con polea alta.', null, false),

-- Brazos
(6, 'Curl de Bíceps en Máquina', 'FUERZA', 'Ejercicio para bíceps usando máquina de curl.', null, false),
(7, 'Fondos en Máquina Sentado', 'FUERZA', 'Ejercicio para tríceps en máquina de dips sentado.', null, false),
(8, 'Extensión de Tríceps en Polea', 'FUERZA', 'Ejercicio para tríceps en polea alta.', null, false),

-- Hombros
(9, 'Press de Hombros en Máquina', 'FUERZA', 'Ejercicio de empuje vertical para deltoides.', null, false),
(10, 'Elevaciones Laterales en Máquina', 'FUERZA', 'Ejercicio de hombros para deltoides laterales.', null, false),

-- Piernas
(11, 'Extensión de Piernas', 'FUERZA', 'Ejercicio para cuadríceps en máquina de extensiones.', null, false),
(12, 'Curl de Piernas en Máquina', 'FUERZA', 'Ejercicio para isquiotibiales en máquina de curl.', null, false),
(13, 'Prensa de Piernas', 'FUERZA', 'Ejercicio compuesto para tren inferior en máquina de prensa.', null, false),
(14, 'Sentadilla Hack', 'FUERZA', 'Ejercicio para cuádriceps y glúteos en máquina hack squat.', null, false),

-- Multifuncionales / Compuestos
(15, 'Dominadas en Estación Multifuncional', 'FUERZA', 'Ejercicio de tracción vertical en estación de dominadas.', null, false),
(16, 'Fondos en Estación Multifuncional', 'FUERZA', 'Ejercicio para tríceps y pectorales en estación multifuncional.', null, false),
(17, 'Press de Pecho en Smith Machine', 'FUERZA', 'Press de banca guiado en multipower Smith.', null, false),
(18, 'Sentadilla en Smith Machine', 'FUERZA', 'Sentadilla guiada en multipower Smith.', null, false);