const { Given, When, Then } = require('@cucumber/cucumber');
const { post, asignarIdUsuario, setUserToken } = require('./common');
const request = require('sync-request');
const assert = require('assert');

var tokenRegistro;

/********* Registro de usuarios y verificación de cuenta ***********/
Given('que se tiene el usuario con nombre {string}, e-mail {string} y contraseña {string}', function(nombre, email, password){
    console.log(`\n\nCargamos usuario con nombre: ${nombre}, e-mail: ${email} y contraseña: ${password}.`);
    this.usuario = {nombre, email, password};
});

When('se intenta guardar al usuario', async function(){
    console.log('Guardamos al usuario...');
    this.usuario = await post('users/register', this.usuario);
});

When('se quiere guardar al usuario correctamente', async function(){
    console.log('Guardamos al usuario...');
    const registerRes = await post('users/register', this.usuario);
    this.usuario = registerRes.usuario;
    asignarIdUsuario(this.usuario.id);
    tokenRegistro = registerRes.token;
});

/********* Verificación de cuenta ***********/
Given('que la cuenta de Enrique López ya fue registrada', function(){
    console.log(`\n\nVerificaremos la cuenta del usuario Enrique López.`);
});

When('se verifica su cuenta', async function(){
    console.log('El usuario verifica su cuenta...');
    const res = await request('GET', `http://backend:8080/api/users/verify?token=${tokenRegistro}`);
    this.htmlValidacion = res.getBody('utf8');
});

Then('el HTML debería incluir las respuestas {string} y {string}', function(respuesta1, respuesta2){
    // Verifica que el HTML recibido contiene el mensaje clave
    assert.ok(this.htmlValidacion.includes(respuesta1) && this.htmlValidacion.includes(respuesta2), 'No se encontró el mensaje esperado en el HTML');
});

/********* Login de usuario ***********/
Given('que la cuenta con e-mail {string} y contraseña {string} ya fue verificada', function(email, password){
    console.log(`\n\nLoguearemos al usuario con e-mail ${email} y contraseña ${password} (y nombre Enrique López).`);
    this.cuerpoLogin = {email, password}
});

When('hace login y obtiene el token', async function(){
    console.log('Dicho usuario ahora hace login...');
    const loginRes = await post('users/login', this.cuerpoLogin);
    if (!loginRes?.token) {
        console.warn('No se pudo loguear al usuario de prueba. La cuenta puede estar inactiva o con credenciales incorrectas.');
    }
    setUserToken(loginRes.token);
});