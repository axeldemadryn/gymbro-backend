# language: es

Característica: Gestionar rutinas semanales y diarias

  # Escenario 1: Validar que la rutina semanal empiece un lunes
  Escenario: Crear una WeeklyRoutine que no empiece un lunes
    Dado que se intenta crear la rutina semanal "Semana A" con fechas desde "2023-10-03" hasta "2023-10-08"
    Cuando se guarda la rutina semanal
    Entonces se obtiene un error con mensaje "La rutina semanal debe comenzar un lunes"

  # Escenario 2: Validar duración de la rutina semanal (5-7 días)
  Escenario: Crear una WeeklyRoutine con duración inválida
    Dado que se intenta crear la rutina semanal "Semana B" con fechas desde "2023-10-02" hasta "2023-10-05"
    Cuando se guarda la rutina semanal
    Entonces se obtiene un error con mensaje "La rutina semanal debe tener entre 5 y 7 días."

  # Escenario 3: Validar que no se solapen fechas de rutinas semanales
  Escenario: Crear una WeeklyRoutine que se solapa con otra existente
    Dado que existe la rutina semanal "Semana C" con fechas desde "2023-10-09" hasta "2023-10-15"
    Y se intenta crear la rutina semanal "Semana D" con fechas desde "2023-10-09" hasta "2023-10-13"
    Cuando se guarda la rutina semanal
    Entonces se obtiene un error con mensaje "La rutina semanal se solapa con otra existente"

  # Escenario 4: Validar que los días de rutina estén dentro del rango de la WeeklyRoutine
  Escenario: Crear RoutineDays fuera del rango de la rutina
    Dado que existe la rutina semanal "Semana E" con fechas desde "2023-10-16" hasta "2023-10-21"
    Y se intenta crear la rutina diaria para "DOMINGO" con la sesión "Sesion Pecho"
    Cuando se guarda la rutina diaria
    Entonces se obtiene un error con mensaje "El día seleccionado no cae dentro del rango de fechas de la rutina semanal."

  # Escenario 5: Validar que los días tengan al menos una sesión con ejercicios
  Escenario: Crear RoutineDay sin ejercicios en la sesión
    Dado que existe la rutina semanal "Semana F" con fechas desde "2023-10-23" hasta "2023-10-29"
    Y se intenta crear la rutina diaria para "LUNES" con la sesión "Sesion Vacía"
    Cuando se guarda la rutina diaria
    Entonces se obtiene un error con mensaje "La sesión debe tener al menos un ejercicio"

  # Escenario 6: Crear una rutina semanal y sus días correctamente
  Escenario: Crear WeeklyRoutine con días válidos y sesiones existentes
    Dado que se intenta crear la rutina semanal "Semana G" con fechas desde "2023-10-30" hasta "2023-11-05"
    Y se crean las siguientes rutinas diarias:

      | Día       | Sesión          |
      | LUNES     | Sesion Pecho    |
      | MARTES    | Sesion Espalda  |
      | MIERCOLES | Sesion Piernas  |
      | JUEVES    | Sesion Brazos   |
      | VIERNES   | Sesion Hombros  |

    Cuando se guarda la rutina semanal y los días
    Entonces la rutina se crea correctamente y los días quedan asignados con sus sesiones

