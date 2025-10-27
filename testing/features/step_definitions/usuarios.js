const { Given, When } = require('@cucumber/cucumber');
const { post } = require('./common');

Given('que se tiene el usuario con nombre {string}, e-mail {string} y contraseña {string}', function(unNombre, unEMail, unaContrasena){
    this.usuario = {
        nombre: unNombre,
        email: unEMail,
        password: unaContrasena
    }
});

When('se guarda al usuario', function(){
    this.usuario = post('users/register', this.usuario);
});