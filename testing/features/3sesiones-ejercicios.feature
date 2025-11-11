# language: es

Característica: Staging de sesiones, ejercicios y tabla intermedia entre ambos

    Escenario: Crear sesiones
        Dado que se tiene la sesión con nombre "<nombre>" y descripción "<descripcion>"
        Cuando se carga la sesión
        Entonces se debería obtener el mensaje "OK"

    Ejemplos:
        | nombre                                 | descripcion                             |
        | Sesión Pecho de Enrique para el test   | Rutina de pecho con máquinas básicas    |
        | Sesión Espalda de Enrique para el test | Rutina de espalda y dorsales            |
        | Sesión Piernas de Enrique para el test | Rutina de piernas con enfoque en fuerza |
        | Sesión Brazos de Enrique para el test  | Rutina de biceps y triceps              |
        | Sesión Hombros de Enrique para el test | Rutina de hombros y deltoides           |
        | Sesión Vacía de Enrique para el test   | Rutina vacia                            |
    
    Escenario: Cargar ejercicios
        Dado que se tiene el ejercicio con nombre "<nombre>", tipo "<tipo>", descripción "<descripcion>" y músculo "<musculo>"
        Cuando se carga el ejercicio
        Entonces se debería obtener el mensaje "OK"
    
    Ejemplos:
        | nombre                                                   | tipo   | descripcion                                                  | musculo   |
        | Press de Pecho en Máquina de Enrique para el test        | FUERZA | Ejercicio para pectorales en máquina de press sentado.       | musculo 1 |
        | Aperturas en Máquina de Pecho de Enrique para el test    | FUERZA | Ejercicio para pectorales con máquina de apertura.           | musculo 1 |
        | Jalón al Pecho de Enrique para el test                   | FUERZA | Ejercicio para dorsales en máquina de polea alta.            | musculo 2 |
        | Remo Sentado en Polea Baja de Enrique para el test       | FUERZA | Remo para espalda media en polea baja.                       | musculo 2 |
        | Pull Over en Polea de Enrique para el test               | FUERZA | Ejercicio de dorsales con polea alta.                        | musculo 2 |
        | Curl de Bíceps en Máquina de Enrique para el test        | FUERZA | Ejercicio para bíceps usando máquina de curl.                | musculo 3 |
        | Fondos en Máquina Sentado de Enrique para el test        | FUERZA | Ejercicio para tríceps en máquina de dips sentado.           | musculo 3 |
        | Extensión de Tríceps en Polea de Enrique para el test    | FUERZA | Ejercicio para tríceps en polea alta.                        | musculo 3 |
        | Press de Hombros en Máquina de Enrique para el test      | FUERZA | Ejercicio de empuje vertical para deltoides.                 | musculo 4 |
        | Elevaciones Laterales en Máquina de Enrique para el test | FUERZA | Ejercicio de hombros para deltoides laterales.               | musculo 4 |
        | Extensión de Piernas de Enrique para el test             | FUERZA | Ejercicio para cuadríceps en máquina de extensiones.         | musculo 5 |
        | Curl de Piernas en Máquina de Enrique para el test       | FUERZA | Ejercicio para isquiotibiales en máquina de curl.            | musculo 5 |
        | Prensa de Piernas de Enrique para el test                | FUERZA | Ejercicio compuesto para tren inferior en máquina de prensa. | musculo 5 |
        | Sentadilla Hack de Enrique para el test                  | FUERZA | Ejercicio para cuádriceps y glúteos en máquina hack squat.   | musculo 5 |
    
    Escenario: Asociar sesiones y ejercicios
        Dado que se quiere asociar a la sesión "<nombreSesion>" con el ejercicio "<nombreEjercicio>", con una cantidad de <sets> sets y <reps> reps
        Cuando se asocia a la sesión y el ejercicio
        Entonces se debería obtener el mensaje "OK"
    Ejemplos:
        | nombreSesion                           | nombreEjercicio                                          | sets | reps |
        | Sesión Pecho de Enrique para el test   | Press de Pecho en Máquina de Enrique para el test        | 4    | 12   |
        | Sesión Pecho de Enrique para el test   | Aperturas en Máquina de Pecho de Enrique para el test    | 4    | 12   |
        | Sesión Espalda de Enrique para el test | Jalón al Pecho de Enrique para el test                   | 4    | 12   |
        | Sesión Espalda de Enrique para el test | Remo Sentado en Polea Baja de Enrique para el test       | 4    | 12   |
        | Sesión Espalda de Enrique para el test | Pull Over en Polea de Enrique para el test               | 4    | 12   |
        | Sesión Piernas de Enrique para el test | Extensión de Piernas de Enrique para el test             | 4    | 12   |
        | Sesión Piernas de Enrique para el test | Curl de Piernas en Máquina de Enrique para el test       | 4    | 12   |
        | Sesión Piernas de Enrique para el test | Prensa de Piernas de Enrique para el test                | 4    | 12   |
        | Sesión Piernas de Enrique para el test | Sentadilla Hack de Enrique para el test                  | 4    | 12   |
        | Sesión Brazos de Enrique para el test  | Curl de Bíceps en Máquina de Enrique para el test        | 3    | 10   |
        | Sesión Brazos de Enrique para el test  | Fondos en Máquina Sentado de Enrique para el test        | 3    | 10   |
        | Sesión Brazos de Enrique para el test  | Extensión de Tríceps en Polea de Enrique para el test    | 3    | 10   |
        | Sesión Hombros de Enrique para el test | Press de Hombros en Máquina de Enrique para el test      | 3    | 12   |
        | Sesión Hombros de Enrique para el test | Elevaciones Laterales en Máquina de Enrique para el test | 3    | 12   |