-- Crear sesiones
INSERT INTO sessions (name, description, user_id) VALUES
('Sesion Pecho', 'Rutina de pecho con máquinas básicas', NULL),
('Sesion Espalda', 'Rutina de espalda y dorsales', NULL),
('Sesion Piernas', 'Rutina de piernas con enfoque en fuerza', NULL),
('Sesion Brazos', 'Rutina de biceps y triceps', NULL),
('Sesion Hombros', 'Rutina de hombros y deltoides', NULL),
('Sesion Vacía', 'Rutina vacia', NULL);


-- Asignar ejercicios a las sesiones (sin IDs fijos, se hace por nombre de sesión)
-- Supone que ya existen los ejercicios en la base de datos

-- Sesion Pecho
INSERT INTO session_exercises (session_id, exercise_id, sets, reps)
SELECT s.id, e.id, 4, 12
FROM sessions s, ejercicios e
WHERE s.name = 'Sesion Pecho' AND e.id IN (1, 2);

-- Sesion Espalda
INSERT INTO session_exercises (session_id, exercise_id, sets, reps)
SELECT s.id, e.id, 4, 12
FROM sessions s, ejercicios e
WHERE s.name = 'Sesion Espalda' AND e.id IN (3, 4, 5);

-- Sesion Piernas
INSERT INTO session_exercises (session_id, exercise_id, sets, reps)
SELECT s.id, e.id, 4, 12
FROM sessions s, ejercicios e
WHERE s.name = 'Sesion Piernas' AND e.id IN (11, 12, 13, 14);

-- Sesion Brazos
INSERT INTO session_exercises (session_id, exercise_id, sets, reps)
SELECT s.id, e.id, 3, 10
FROM sessions s, ejercicios e
WHERE s.name = 'Sesion Brazos' AND e.id IN (6, 7, 8);

-- Sesion Hombros
INSERT INTO session_exercises (session_id, exercise_id, sets, reps)
SELECT s.id, e.id, 3, 12
FROM sessions s, ejercicios e
WHERE s.name = 'Sesion Hombros' AND e.id IN (9, 10);

