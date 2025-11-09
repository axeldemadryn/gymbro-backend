# language: es

Característica: Gestionar rutinas semanales

  # Escenario 1: Crear rutinas semanales (correctamente y con errores) en general, pero NO para la semana actual (otro test con
  # otra lógica).
  # NOTA: Si en el futuro se llega al año 9999, o se extiende el rango horario soportado por Cucumber, convendrá cambiar las fechas
  # de la semana E.
  Escenario: Crear WeeklyRoutines en general
    Dado que se intenta crear la rutina semanal "<semana>" con descripción "<descripcion>" y fechas desde "<fechaInicio>" hasta "<fechaFin>"
    Cuando se guarda la rutina semanal
    Entonces se debería obtener el mensaje "<mensaje>"

  Ejemplos:
    | semana   | descripcion                         | fechaInicio | fechaFin   | mensaje                                         |
    | Semana A | Rutina de 2023                      | 2023-10-09  | 2023-10-15 | OK                                              |
    | Semana B | Una rutina                          | 2023-10-16  | 2023-10-21 | OK                                              |
    | Semana C |                                     | 2023-10-23  | 2023-10-29 | OK                                              |
    | Semana D |                                     | 2023-10-30  | 2023-11-05 | OK                                              |
    | Semana E |                                     | 9999-02-01  | 9999-02-06 | OK                                              |
    | Semana F | La rutina empieza un martes         | 2023-10-03  | 2023-10-08 | La rutina semanal debe comenzar un lunes.       |
    | Semana G | La rutina dura 3 días               | 2023-10-02  | 2023-10-05 | La rutina semanal debe tener entre 5 y 7 días.  |
    | Semana H | Solapamiento con rutina de Semana A | 2023-10-09  | 2023-10-13 | La rutina semanal se solapa con otra existente. |

  # Escenario 2: Crear rutina semanal para la semana actual
  Escenario: Crear WeeklyRoutine para esta semana
    Dado que se intenta crear una rutina semanal para esta semana
    Cuando se guarda la rutina semanal
    Entonces se debería obtener el mensaje "OK"