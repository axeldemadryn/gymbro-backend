# language: es

Característica: Testing de usuarios

    Esquema del escenario: Carga inicial y validaciones de usuarios
        Dado que se tiene el usuario con nombre "<nombre>", e-mail "<email>" y contraseña "<contrasena>"
        Cuando se guarda al usuario
        Entonces se debería obtener el mensaje "<mensaje>"

        Ejemplos:
        | nombre          | email                     | contrasena   | mensaje                                                                                                                 |
        | Camila Andretti | camilandretti@ejemplo.com | camiandreti  | OK                                                                                                                      |
        | Julio Ibáñez    | julioibanez@ejemplo.com   | ibanezjulio  | OK                                                                                                                      |
        | Mateo Bloque    | mateobloque@ejemplo.com   | mateobloqueo | OK                                                                                                                      |
        |                 | nombrevacio@ejemplo.com   | xd           | El nombre no puede estar vacío                                                                                          |
        | Nadie           |                           | xd           | El e-mail no puede estar vacío                                                                                          |
        | Nadie           | emailInvalido             | xd           | Por favor, escriba un e-mail válido                                                                                     |
        | Hola            | hola@email.com            |              | La contraseña no puede estar vacía                                                                                      |
        | Repetido        | julioibanez@ejemplo.com   | xd           | Este e-mail ya está registrado pero aún no fue verificado. Revisa tu correo o solicita un nuevo enlace de verificación. |

    Esquema del escenario: Verificación de cuenta
        Dado que se tiene el usuario con nombre "Enrique López", e-mail "enriquelopez@ejemplo.com" y contraseña "lopezenrique"
        Cuando se quiere guardar al usuario y luego verificar su cuenta
        Entonces se debería obtener el mensaje "OK"
    
    Esquema del escenario: Login de usuario
        Dado que la cuenta con e-mail "enriquelopez@ejemplo.com" ya fue verificada
        Cuando hace login y obtiene el token
        Entonces se debería obtener el mensaje "OK"