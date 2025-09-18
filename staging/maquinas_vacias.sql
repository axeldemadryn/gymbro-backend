-- Se llama 'máquinas vacías' porque en principio no tienen ejercicios almacenados
INSERT INTO maquinas (nombre, tipo_equipo, descripcion, imagen_url)
VALUES 
('Chest Press Machine', 'MAQUINA_PECHO', 'Máquina para press de pecho sentado', '/imagenes/chest-press-machine.jpg'),
('Lat Pull Down', 'POLEAS', 'Máquina para jalones dorsales', '/imagenes/lat-pull-down.jpg'),
('Seated Cable Rows', 'POLEAS', 'Remo sentado con polea baja', '/imagenes/seated-cable-row.jpg'),
('Arm Curl Machine', 'MAQUINA_HOMBRO', 'Máquina para curl de brazos', '/imagenes/arm-curl.jpeg'),
('Chest Fly Machine', 'MAQUINA_PECHO', 'Máquina de apertura de pecho', '/imagenes/chest-fly.jpeg'),
('Chinning Dipping', 'MULTIFUNCIONAL', 'Estación para dominadas y fondos', '/imagenes/chinning-dipping.jpeg'),
('Lateral Raises Machine', 'MAQUINA_HOMBRO', 'Máquina para elevaciones laterales de hombros', '/imagenes/lateral-raises.jpeg'),
('Leg Extension', NULL, 'Máquina de extensión de cuadríceps', '/imagenes/leg-extension.jpeg'),
('Leg Press', NULL, 'Prensa de piernas', '/imagenes/leg-press.jpeg'),
('Leg Curl Machine', NULL, 'Máquina para curl de piernas', '/imagenes/leg-curl.jpeg'),
('Seated Dip Machine', NULL, 'Máquina para fondos de tríceps sentado', '/imagenes/seated-dip.jpg'),
('Shoulder Press Machine', 'MAQUINA_HOMBRO', 'Máquina para press de hombros', '/imagenes/shoulder-press.jpeg'),
('Smith Machine', 'MULTIFUNCIONAL', 'Máquina multipower con barra guiada', '/imagenes/smith-machine.jpeg'),
('Hack Squat Machine', NULL, 'Máquina para sentadilla hack', '/imagenes/hack-sqat.jpeg');
