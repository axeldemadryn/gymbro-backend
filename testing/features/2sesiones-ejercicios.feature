# language: es

Característica: Staging de sesiones, ejercicios y tabla intermedia entre ambos

    Escenario: Crear sesiones
        Dado que se tiene la sesión con nombre "<nombre>" y descripción "<descripcion>"
        Cuando se carga la sesión
        Entonces se debería obtener el mensaje "OK"

    Ejemplos:
        | nombre         | descripcion                             |
        | Sesión Pecho   | Rutina de pecho con máquinas básicas    |
        | Sesión Espalda | Rutina de espalda y dorsales            |
        | Sesión Piernas | Rutina de piernas con enfoque en fuerza |
        | Sesión Brazos  | Rutina de biceps y triceps              |
        | Sesión Hombros | Rutina de hombros y deltoides           |
        | Sesión Vacía   | Rutina vacia                            |
    
    Escenario: Cargar ejercicios
        Dado que se tiene el ejercicio con nombre "<nombre>", tipo "<tipo>" y descripción "<descripcion>"
        Cuando se carga el ejercicio
        Entonces se debería obtener el mensaje "OK"
    
    Ejemplos:
        | nombre                           | tipo   | descripcion                                                  |
        | Press de Pecho en Máquina        | FUERZA | Ejercicio para pectorales en máquina de press sentado.       |
        | Aperturas en Máquina de Pecho    | FUERZA | Ejercicio para pectorales con máquina de apertura.           |
        | Jalón al Pecho                   | FUERZA | Ejercicio para dorsales en máquina de polea alta.            |
        | Remo Sentado en Polea Baja       | FUERZA | Remo para espalda media en polea baja.                       |
        | Pull Over en Polea               | FUERZA | Ejercicio de dorsales con polea alta.                        |
        | Curl de Bíceps en Máquina        | FUERZA | Ejercicio para bíceps usando máquina de curl.                |
        | Fondos en Máquina Sentado        | FUERZA | Ejercicio para tríceps en máquina de dips sentado.           |
        | Extensión de Tríceps en Polea    | FUERZA | Ejercicio para tríceps en polea alta.                        |
        | Press de Hombros en Máquina      | FUERZA | Ejercicio de empuje vertical para deltoides.                 |
        | Elevaciones Laterales en Máquina | FUERZA | Ejercicio de hombros para deltoides laterales.               |
        | Extensión de Piernas             | FUERZA | Ejercicio para cuadríceps en máquina de extensiones.         |
        | Curl de Piernas en Máquina       | FUERZA | Ejercicio para isquiotibiales en máquina de curl.            |
        | Prensa de Piernas                | FUERZA | Ejercicio compuesto para tren inferior en máquina de prensa. |
        | Sentadilla Hack                  | FUERZA | Ejercicio para cuádriceps y glúteos en máquina hack squat.   |
    
    Escenario: Asociar sesiones y ejercicios
        Dado que se quiere asociar a la sesión "<nombreSesion>" con el ejercicio "<nombreEjercicio>", con una cantidad de <sets> sets y <reps> reps
        Cuando se asocia a la sesión y el ejercicio
        Entonces se debería obtener el mensaje "OK"
    Ejemplos:
        | nombreSesion   | nombreEjercicio                  | sets | reps |
        | Sesión Pecho   | Press de Pecho en Máquina        | 4    | 12   |
        | Sesión Pecho   | Aperturas en Máquina de Pecho    | 4    | 12   |
        | Sesión Espalda | Jalón al Pecho                   | 4    | 12   |
        | Sesión Espalda | Remo Sentado en Polea Baja       | 4    | 12   |
        | Sesión Espalda | Pull Over en Polea               | 4    | 12   |
        | Sesión Piernas | Extensión de Piernas             | 4    | 12   |
        | Sesión Piernas | Curl de Piernas en Máquina       | 4    | 12   |
        | Sesión Piernas | Prensa de Piernas                | 4    | 12   |
        | Sesión Piernas | Sentadilla Hack                  | 4    | 12   |
        | Sesión Brazos  | Curl de Bíceps en Máquina        | 3    | 10   |
        | Sesión Brazos  | Fondos en Máquina Sentado        | 3    | 10   |
        | Sesión Brazos  | Extensión de Tríceps en Polea    | 3    | 10   |
        | Sesión Hombros | Press de Hombros en Máquina      | 3    | 12   |
        | Sesión Hombros | Elevaciones Laterales en Máquina | 3    | 12   |