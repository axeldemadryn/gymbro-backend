TRUNCATE TABLE
    session_exercises,      -- Tabla intermedia entre sesiones y ejercicios
    routine_days,           -- Tabla de días de rutina
    weekly_routines,        -- Tabla de rutinas semanales
    sessions,               -- Tabla de sesiones, ya que routine_days depende de ésta
    ejercicio_maquinas,     -- Tabla intermedia entre maquinas y ejercicios
    ejercicio_musculos,     -- Tabla intermedia entre ejercicios y musculos
    maquina_musculos,       -- Tabla intermedia entre maquinas y musculos
    maquinas,               -- Tabla de maquinas
    ejercicios,             -- Tabla de ejercicios
    musculos,               -- Tabla de musculos, ya que no depende de otras
    users                   -- Tabla de usuarios, ya que tampoco depende de otras
RESTART IDENTITY CASCADE;