#language: es

Característica: Carga básica de músculos

    # Los nombres no son totalmente realistas, y no se supone que lo sean en un TEST
    Esquema del escenario: Carga de músculos
        Dado que se tiene el músculo con nombre "<nombre>"
        Cuando se carga dicho músculo en la BD
        Entonces se debería obtener el mensaje "OK"
    
    Ejemplos:
    | nombre    |
    | musculo 1 |
    | musculo 2 |
    | musculo 3 |
    | musculo 4 |
    | musculo 5 |