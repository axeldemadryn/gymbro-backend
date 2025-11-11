# language: es

Característica: Testing de usuarios

    # El token de este usuario se utilizará posteriormente para ejecutar los otros tests.
    Esquema del escenario: Carga correcta de un usuario
        Dado que se tiene el usuario con nombre "Enrique López", e-mail "enriquelopez@ejemplo.com" y contraseña "lopezenrique"
        Cuando se quiere guardar al usuario correctamente
        Entonces se debería obtener el mensaje "OK"
    

    Esquema del escenario: Carga incorrecta de usuarios
        Dado que se tiene el usuario con nombre "<nombre>", e-mail "<email>" y contraseña "<contrasena>"
        Cuando se intenta guardar al usuario
        Entonces se debería obtener el mensaje "<mensaje>"

        Ejemplos:
        | nombre          | email                     | contrasena   | mensaje                                                                                                                 |
        |                 | nombrevacio@ejemplo.com   | xd           | El nombre no puede estar vacío                                                                                          |
        | Nadie           |                           | xd           | El e-mail no puede estar vacío                                                                                          |
        | Nadie           | emailInvalido             | xd           | Por favor, escriba un e-mail válido                                                                                     |
        | Hola            | hola@email.com            |              | La contraseña no puede estar vacía                                                                                      |
        | Repetido        | enriquelopez@ejemplo.com  | lopezenrique | Este e-mail ya está registrado pero aún no fue verificado. Revisa tu correo o solicita un nuevo enlace de verificación. |

    Esquema del escenario: Verificación de cuenta
        Dado que la cuenta de Enrique López ya fue registrada
        Cuando se verifica su cuenta
        Entonces el HTML debería incluir las respuestas "¡Cuenta verificada!" y "Tu cuenta ha sido verificada correctamente. Ya podés iniciar sesión en GymBro."
    
    Esquema del escenario: Login de usuario
        Dado que la cuenta con e-mail "enriquelopez@ejemplo.com" y contraseña "lopezenrique" ya fue verificada
        Cuando hace login y obtiene el token
        Entonces se debería obtener el mensaje "OK"