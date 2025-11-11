const assert = require('assert');
const { Given, When, Then } = require('@cucumber/cucumber');
const { post, postConAgregacion, get, put, hoy, lunes, domingo } = require('./common');

// Crear rutina diaria
Given('que se intenta crear la rutina diaria para el día {string} con la sesión {string} para la rutina diaria con fechas desde {string} hasta {string}', async function (day, sessionName, startDate, endDate) {
    console.log(`Crearemos una rutina diaria para la rutina semanal del ${startDate} al ${endDate}, en el día ${day}, asociada a la sesión ${sessionName} perteneciente a Enrique López.`);
    const session = await get(`sessions/by-name?name=` + sessionName);
    const routine = await get(`weekly-routines/by-dates?startDate=${startDate}&endDate=${endDate}`);
    this.routineDay = {day, routine, session};
});

// Intentar guardar rutina diaria sin éxito
When('se intenta generar la rutina diaria', async function () {
    console.log('Guardamos la rutina diaria...');
    try {
        this.routineDay = await post(`routine-days`, this.routineDay);
    } catch (error) {
        console.warn('Error al crear la rutina diaria: ' + error.message);
    }
});

// Guardar rutina diaria
When('se guarda la rutina diaria', async function () {
    console.log('Guardamos la rutina diaria...');
    try {
        this.routineDay = await postConAgregacion(`routine-days`, this.routineDay);
    } catch (error) {
        console.warn('Error al crear la rutina diaria: ' + error.message);
    }
});

// Validar estado de rutina diaria
Then('el estado de la rutina debería ser {string}', function(estadoEsperado){
    assert.equal(estadoEsperado, this.routineDay.status);
    console.log(`y el estado de la rutina es: ${this.routineDay.status}.`);
});

/************************** Crear rutina diaria para el día de hoy ******************************/

Given('que se quiere crear una rutina diaria para el día de hoy asociada a la sesión ya existente {string}', async function(nombreSesion){
    console.log(`Crearemos una rutina diaria para la rutina semanal del ${lunes} al ${domingo} (esta semana), en el día de hoy ${hoy}, asociada a la sesión ${nombreSesion} perteneciente a Enrique López.`);
    const sesion = await get(`sessions/by-name?name=` + nombreSesion); // Obtiene la sesión
    const rutinaActual = await get(`weekly-routines/by-dates?startDate=${startDate}&endDate=${endDate}`); // Obtiene la rutina de esta semana
    
    // Crea la rutina diaria
    this.routineDay = {
        day: hoy,
        routine: rutinaActual,
        session: sesion
    };
});


/**************Búsqueda de rutina diaria existente y cambio de estado a COMPLETADA*******************/

// Búsqueda de rutina diaria en general
Given('que se tiene la rutina diaria para {string} con la rutina semanal que dura desde {string} hasta {string}', async function(diaSemana, inicioRutinaSemanal, finRutinaSemanal){
    console.log(`Intentamos cambiar de estado a COMPLETADA a la rutina diaria del día ${diaSemana} de Enrique López, perteneciente a la rutina semanal del ${inicioRutinaSemanal} al ${finRutinaSemanal}.`);
    this.routineDay = await get(`routine-days/by-day-and-weekly-routine-dates?day=${diaSemana}&startDate=${inicioRutinaSemanal}&endDate=${finRutinaSemanal}`);
});

// Cambio de estado
When('se cambia el estado de la rutina diaria a COMPLETADA', async function(){
    console.log('Procede el intento de cambio de estado...');
    this.routineDay = await put('routine-days/completada', this.routineDay);
});

// Búsqueda de rutina de hoy
Given('que ya existe una rutina para el día de hoy', async function(){
    console.log(`Intentamos cambiar de estado a COMPLETADA a la rutina diaria del día de hoy ${hoy} de Enrique López, perteneciente a la rutina semanal del ${lunes} al ${domingo}.`);
    this.routineDay = await get(`routine-days/by-day-and-weekly-routine-dates?day=${hoy}&startDate=${lunes}&endDate=${domingo}`);
});
