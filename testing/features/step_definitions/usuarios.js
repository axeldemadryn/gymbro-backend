const { Given, When } = require('@cucumber/cucumber');
const { post } = require('./common');

Given('que se tiene el usuario con nombre {string}, e-mail {string} y contraseña {string}', function(unNombre, unEMail, unaContrasena){
    this.usuario = {
        nombre: unNombre,
        email: unEMail,
        password: unaContrasena
    }
    console.log(`Cargamos usuario con nombre: ${unNombre}, e-mail: ${unEMail} y contraseña: ${unaContrasena}`);
});

When('se guarda al usuario', function(){
    this.usuario = post('users/register', this.usuario);
});