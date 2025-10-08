-- Escenario 3: Rutina "Semana C" para probar solapamiento
INSERT INTO weekly_routines (name, start_date, end_date)
VALUES ('Semana C', '2023-10-09', '2023-10-15');

-- Escenario 4: Rutina "Semana E" para probar días fuera del rango
INSERT INTO weekly_routines (name, start_date, end_date)
VALUES ('Semana E', '2023-10-16', '2023-10-21');

-- Escenario 5: Rutina "Semana F" para probar sesiones sin ejercicios
INSERT INTO weekly_routines (name, start_date, end_date)
VALUES ('Semana F', '2023-10-23', '2023-10-29');
