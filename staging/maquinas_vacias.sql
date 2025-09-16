-- Se llama 'máquinas vacías' porque en principio no tienen ejercicios almacenados
INSERT INTO maquinas (nombre, tipo_equipo, descripcion, video_url, es_personalizado)
VALUES 
('Chest Press Machine', 'MAQUINA_PECHO', 'Máquina para press de pecho sentado', NULL, FALSE),
('Lat Pull Down', 'POLEAS', 'Máquina para jalones dorsales', NULL, FALSE),
('Seated Cable Rows', 'POLEAS', 'Remo sentado con polea baja', NULL, FALSE),
('Arm Curl Machine', 'MAQUINA_HOMBRO', 'Máquina para curl de brazos', NULL, FALSE),
('Chest Fly Machine', 'MAQUINA_PECHO', 'Máquina de apertura de pecho', NULL, FALSE),
('Chinning Dipping', 'MULTIFUNCIONAL', 'Estación para dominadas y fondos', NULL, FALSE),
('Lateral Raises Machine', 'MAQUINA_HOMBRO', 'Máquina para elevaciones laterales de hombros', NULL, FALSE),
('Leg Extension', NULL, 'Máquina de extensión de cuadríceps', NULL, FALSE),
('Leg Press', NULL, 'Prensa de piernas', NULL, FALSE),
('Leg Curl Machine', NULL, 'Máquina para curl de piernas', NULL, FALSE),
('Seated Dip Machine', NULL, 'Máquina para fondos de tríceps sentado', NULL, FALSE),
('Shoulder Press Machine', 'MAQUINA_HOMBRO', 'Máquina para press de hombros', NULL, FALSE),
('Smith Machine', 'MULTIFUNCIONAL', 'Máquina multipower con barra guiada', NULL, FALSE),
('Hack Squat Machine', NULL, 'Máquina para sentadilla hack', NULL, FALSE);
