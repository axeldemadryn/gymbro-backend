-- Vacíos porque no están asociados a ningún músculo, ni máquina (en principio)
INSERT INTO ejercicios (nombre, tipo, descripcion, video_url, es_personalizado)
VALUES
-- Pecho
('Press de Pecho en Máquina', 'FUERZA', 'Ejercicio para pectorales en máquina de press sentado.', null, false),
('Aperturas en Máquina de Pecho', 'FUERZA', 'Ejercicio para pectorales con máquina de apertura.', null, false),

-- Espalda
('Jalón al Pecho', 'FUERZA', 'Ejercicio para dorsales en máquina de polea alta.', null, false),
('Remo Sentado en Polea Baja', 'FUERZA', 'Remo para espalda media en polea baja.', null, false),
('Pull Over en Polea', 'FUERZA', 'Ejercicio de dorsales con polea alta.', null, false),

-- Brazos
('Curl de Bíceps en Máquina', 'FUERZA', 'Ejercicio para bíceps usando máquina de curl.', null, false),
('Fondos en Máquina Sentado', 'FUERZA', 'Ejercicio para tríceps en máquina de dips sentado.', null, false),
('Extensión de Tríceps en Polea', 'FUERZA', 'Ejercicio para tríceps en polea alta.', null, false),

-- Hombros
('Press de Hombros en Máquina', 'FUERZA', 'Ejercicio de empuje vertical para deltoides.', null, false),
('Elevaciones Laterales en Máquina', 'FUERZA', 'Ejercicio de hombros para deltoides laterales.', null, false),

-- Piernas
('Extensión de Piernas', 'FUERZA', 'Ejercicio para cuadríceps en máquina de extensiones.', null, false),
('Curl de Piernas en Máquina', 'FUERZA', 'Ejercicio para isquiotibiales en máquina de curl.', null, false),
('Prensa de Piernas', 'FUERZA', 'Ejercicio compuesto para tren inferior en máquina de prensa.', null, false),
('Sentadilla Hack', 'FUERZA', 'Ejercicio para cuádriceps y glúteos en máquina hack squat.', null, false),

-- Multifuncionales / Compuestos
('Dominadas en Estación Multifuncional', 'FUERZA', 'Ejercicio de tracción vertical en estación de dominadas.', null, false),
('Fondos en Estación Multifuncional', 'FUERZA', 'Ejercicio para tríceps y pectorales en estación multifuncional.', null, false),
('Press de Pecho en Smith Machine', 'FUERZA', 'Press de banca guiado en multipower Smith.', null, false),
('Sentadilla en Smith Machine', 'FUERZA', 'Sentadilla guiada en multipower Smith.', null, false);