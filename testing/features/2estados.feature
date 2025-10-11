# language: es

Característica: estados de sesiones

   Esquema del escenario: Carga inicial de rutinas semanales
      Dado que se intenta crear la rutina semanal "<nombre>" con descripción "<descripcion>" y fechas desde "<fechaInicio>" hasta "<fechaFin>"
      Cuando se crea la rutina semanal con descripción
      Entonces se debería obtener el mensaje "OK"

      Ejemplos:
      | nombre          | descripcion        | fechaInicio | fechaFin   |
      | Semana 7        | Rutina de semana 7 | 2024-07-01  | 2024-07-05 |
      | Semana 8        | Rutina de semana 8 | 2024-07-15  | 2024-07-20 |
      | Semana 9        |                    | 2024-07-22  | 2024-07-28 |
      | Semana 10       |                    | 9999-02-01  | 9999-02-06 |
   
   Esquema del escenario: Carga inicial de rutinas diarias
      Dado que se intenta crear la rutina diaria para "<diaSemana>" con la rutina semanal que dura desde "<inicioRutinaSemanal>" hasta "<finRutinaSemanal>", y con la sesión "<sesion>"
      Cuando se crea la rutina diaria
      Entonces se debería obtener el mensaje "OK"
      Y el estado de la rutina debería ser "<estado>"

      Ejemplos:
      | diaSemana | inicioRutinaSemanal | finRutinaSemanal | sesion         | estado        |
      | LUNES     | 2024-07-01          | 2024-07-05       | Sesion Pecho   | NO_COMPLETADA |
      | MARTES    | 2024-07-15          | 2024-07-20       | Sesion Espalda | NO_COMPLETADA |
      | MIERCOLES | 2024-07-22          | 2024-07-28       | Sesion Piernas | NO_COMPLETADA |
      | JUEVES    | 9999-02-01          | 9999-02-06       | Sesion Brazos  | PENDIENTE     |