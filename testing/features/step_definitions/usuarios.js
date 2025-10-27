const { Given, When } = require('@cucumber/cucumber');
const { post } = require('./common');

Given('que se tiene el usuario con nombre {string}, e-mail {string}, contraseña {string} y registrado en la fecha {string}', function(unNombre, unEMail, unaContrasena, unaFechaYHoraRegistro){
    this.usuario = {
        nombre: unNombre,
        email: unEMail,
        password: unaContrasena,
        fechaYHoraRegistro: unaFechaYHoraRegistro
    }
});

When('se guarda al usuario', function(){
    this.usuario = post('users/register', this.usuario);
});