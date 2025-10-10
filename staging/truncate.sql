TRUNCATE TABLE
    session_exercises,      -- Tabla intermedia entre sesiones y ejercicios
    sessions,               -- Tabla de sesiones
    routine_days,           -- Tabla de días de rutina
    weekly_routines,        -- Tabla de rutinas semanales
    ejercicio_maquinas,     -- Tabla intermedia entre maquinas y ejercicios
    ejercicio_musculos,     -- Tabla intermedia entre ejercicios y musculos
    maquina_musculos,       -- Tabla intermedia entre maquinas y musculos
    maquinas,               -- Tabla de maquinas
    ejercicios,             -- Tabla de ejercicios
    musculos                -- Tabla de musculos, ya que no depende de otras
RESTART IDENTITY CASCADE;