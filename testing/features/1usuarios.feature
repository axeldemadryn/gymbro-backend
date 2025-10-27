# language: es

Característica: Testing de usuarios

    Esquema del escenario: Carga inicial y validaciones de usuarios
        Dado que se tiene el usuario con nombre "<nombre>", e-mail "<email>", contraseña "<contrasena>" y registrado en la fecha "<fechaYHoraRegistro>"
        Cuando se guarda al usuario
        Entonces se debería obtener el mensaje "<mensaje>"

        Ejemplos:
        | nombre          | email                     | contrasena   | fechaYHoraRegistro     | mensaje                             |
        | Enrique López   | enriquelopez@ejemplo.com  | lopezenrique | 2023-08-10T00:00:00    | OK                                  |
        | Camila Andretti | camilandretti@ejemplo.com | camiandreti  | 2023-08-15T01:00:00    | OK                                  |
        | Julio Ibáñez    | julioibanez@ejemplo.com   | ibanezjulio  | 2023-08-20T12:34:56    | OK                                  |
        | Mateo Bloque    | mateobloque@ejemplo.com   | mateobloqueo |                        | OK                                  |
        |                 | nombrevacio@ejemplo.com   | xd           | 2022-01-01T00:00:00    | El nombre no puede estar vacío      |
        | Nadie           |                           | xd           | 2022-01-02T00:00:00    | El e-mail no puede estar vacío      |
        | Nadie           | emailInvalido             | xd           | 2022-01-03T00:00:00    | Por favor, escriba un e-mail válido |
        | Hola            | hola@email.com            |              | 2022-01-04T00:00:00    | La contraseña no puede estar vacía. |
        | Repetido        | julioibanez@ejemplo.com   | xd           | 2022-01-05T00:00:00    | El e-mail ya está registrado.       |
