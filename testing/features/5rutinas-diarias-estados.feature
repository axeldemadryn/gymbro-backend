# language: es

Característica: estados de sesiones

   # Escenario 1: Validar requisitos de creación de rutina diaria
  Esquema del escenario: Errores al crear RoutineDays
    Dado que se intenta crear la rutina diaria para el día "<Día>" con la sesión "<Sesión>" para la rutina diaria con fechas desde "<fechaDesde>" hasta "<fechaHasta>"
    Cuando se intenta generar la rutina diaria
    Entonces se debería obtener el mensaje "<Mensaje esperado>"

  Ejemplos:
    | Día       | Sesión                               | fechaDesde | fechaHasta | Mensaje esperado                                                            |
    | DOMINGO   | Sesion Pecho de Enrique para el test | 2023-10-16 | 2023-10-21 | El día seleccionado no cae dentro del rango de fechas de la rutina semanal. |
    | LUNES     | Sesion Vacía de Enrique para el test | 2023-10-23 | 2023-10-29 | La sesión debe tener al menos un ejercicio.                                 |
  
  # Escenario 2: Crear días de rutina para rutinas semanales existentes y validar estados iniciales
  Esquema del escenario: Crear rutinas diarias correctamente y validar estados
    Dado que se intenta crear la rutina diaria para el día "<Día>" con la sesión "<Sesión>" para la rutina diaria con fechas desde "<fechaDesde>" hasta "<fechaHasta>"
    Cuando se guarda la rutina diaria
    Entonces se debería obtener el mensaje "OK"
    Y el estado de la rutina debería ser "<estado>"

  Ejemplos:
    | Día       | Sesión                                 | fechaDesde | fechaHasta | estado        |
    | LUNES     | Sesion Pecho de Enrique para el test   | 2023-10-30 | 2023-11-05 | NO_COMPLETADA |
    | MARTES    | Sesion Espalda de Enrique para el test | 2023-10-30 | 2023-11-05 | NO_COMPLETADA |
    | MIERCOLES | Sesion Piernas de Enrique para el test | 2023-10-30 | 2023-11-05 | NO_COMPLETADA |
    | JUEVES    | Sesion Brazos de Enrique para el test  | 2023-10-30 | 2023-11-05 | NO_COMPLETADA |
    | VIERNES   | Sesion Hombros de Enrique para el test | 2023-10-30 | 2023-11-05 | NO_COMPLETADA |
    | JUEVES    | Sesion Brazos de Enrique para el test  | 9999-02-01 | 9999-02-06 | PENDIENTE     |

  # Escenario 3: Crear rutina diaria para hoy y validar su estado
  Esquema del escenario: Crear y validar estado de rutina para el día de hoy
      Dado que se quiere crear una rutina diaria para el día de hoy asociada a la sesión ya existente "Sesion Espalda de Enrique para el test"
      Cuando se guarda la rutina diaria
      Entonces se debería obtener el mensaje "OK"
      Y el estado de la rutina debería ser "PENDIENTE"

  # Escenario 4: Validar cambios de estado
   Esquema del escenario: Manejar cambios de estados inválidos de rutina diaria a completada
      Dado que se tiene la rutina diaria para "<diaSemana>" con la rutina semanal que dura desde "<inicioRutinaSemanal>" hasta "<finRutinaSemanal>"
      Cuando se cambia el estado de la rutina diaria a COMPLETADA
      Entonces se debería obtener el mensaje "<mensaje>"

      Ejemplos:
      | diaSemana | inicioRutinaSemanal | finRutinaSemanal | mensaje                                                                                   |
      | LUNES     | 2023-10-30          | 2023-11-05       | Error. La rutina ya ha expirado.                                                          |
      | MARTES    | 2023-10-30          | 2023-11-05       | Error. La rutina ya ha expirado.                                                          |
      | MIERCOLES | 2023-10-30          | 2023-11-05       | Error. La rutina ya ha expirado.                                                          |
      | JUEVES    | 9999-02-01          | 9999-02-06       | Error. Sólo se puede marcar como completada si el día de hoy corresponde al de la rutina. |

    # Escenario 5: Marcar rutina de hoy como completada
    Esquema del escenario: Marcar como completada rutina de hoy
      Dado que ya existe una rutina para el día de hoy
      Cuando se cambia el estado de la rutina diaria a COMPLETADA
      Entonces se debería obtener el mensaje "OK"
      Y el estado de la rutina debería ser "COMPLETADA"