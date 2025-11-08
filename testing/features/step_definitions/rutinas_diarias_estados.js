const assert = require('assert');
const { Given, When } = require('@cucumber/cucumber');
const { post, get, put, hoy, lunes, domingo } = require('./common');

// Crear rutina diaria
Given('que se intenta crear la rutina diaria para el día {string} con la sesión {string} para la rutina diaria con fechas desde "<fechaDesde>" hasta "<fechaHasta>"', function (day, sessionName, startDate, endDate) {
    const session = get(`sessions/by-name?name=` + sessionName);
    const routine = get(`weekly-routines/by-dates?startDate=${startDate}&endDate=${endDate}`);
    this.routineDay = {day, routine, session};
});

// Guardar rutina diaria
When('se guarda la rutina diaria', function () {
    try {
        this.routineDay = post(`routine-days`, this.routineDay);
    } catch (error) {
        console.warn('Error al crear la rutina diaria: ' + error.message);
    }
});

// Validar estado de rutina diaria
Then('el estado de la rutina debería ser {string}', function(estadoEsperado){
    assert.equal(estadoEsperado, this.routineDay.status);
});

/************************** Crear rutina para el día de hoy ******************************/

Given('que se quiere crear una rutina diaria para el día de hoy asociada a la sesión ya existente {string}', function(nombreSesion){
    const sesion = get(`sessions/by-name?name=` + nombreSesion); // Obtiene la sesión
    const rutinaActual = get(`weekly-routines/by-dates?startDate=${startDate}&endDate=${endDate}`); // Obtiene la rutina de esta semana
    
    // Crea la rutina diaria
    this.routineDay = {
        day: hoy,
        routine: rutinaActual,
        session: sesion
    };
});


/**************Búsqueda de rutina diaria existente y cambio de estado a COMPLETADA*******************/

// Búsqueda de rutina diaria en general
Given('que se tiene la rutina diaria para {string} con la rutina semanal que dura desde {string} hasta {string}', function(diaSemana, inicioRutinaSemanal, finRutinaSemanal){
    this.routineDay = get(`routine-days/by-day-and-weekly-routine-dates?day=${diaSemana}&startDate=${inicioRutinaSemanal}&endDate=${finRutinaSemanal}`);
});

// Cambio de estado
When('se cambia el estado de la rutina diaria a COMPLETADA', function(){
    this.routineDay = put('routine-days/completada', this.routineDay);
});

// Búsqueda de rutina de hoy
Given('que ya existe una rutina para el día de hoy', function(){
    this.routineDay = get(`routine-days/by-day-and-weekly-routine-dates?day=${hoy}&startDate=${lunes}&endDate=${domingo}`);
});
