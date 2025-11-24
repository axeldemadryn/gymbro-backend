-- Los planes y músculos ya no se eliminan de la BD, sino que quedan persistidos
-- El orden de las tablas es tal que cada tabla no es referenciada por ninguna tabla arriba, aunque sí por alguna de las tablas de abajo
TRUNCATE TABLE
    historial_reconocimientos,
    session_exercises,
    routine_days,
    weekly_routines,
    sessions,
    ejercicio_maquinas,
    ejercicio_musculos,
    maquina_musculos,
    maquinas,
    ejercicios,
    users
RESTART IDENTITY CASCADE;