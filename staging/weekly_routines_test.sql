-- ERROR: user_id NO debería ser nulo. Corregir eso
INSERT INTO weekly_routines (name, start_date, end_date, user_id) VALUES
('Semana C', '2023-10-09', '2023-10-15', NULL), -- Escenario 3: Rutina "Semana C" para probar solapamiento
('Semana E', '2023-10-16', '2023-10-21', NULL), -- Escenario 4: Rutina "Semana E" para probar días fuera del rango
('Semana F', '2023-10-23', '2023-10-29', NULL); -- Escenario 5: Rutina "Semana F" para probar sesiones sin ejercicios
