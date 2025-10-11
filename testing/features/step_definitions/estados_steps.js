const assert = require('assert');
const { Given, When, Then } = require('@cucumber/cucumber');
const post = require('./common').post;
const get = require('./common').get;

Given('que se intenta crear la rutina semanal {string} con descripción {string} y fechas desde {string} hasta {string}', function(nombre, descripcion, fechaInicio, fechaFin){
    this.weeklyRoutine = {
        name: nombre,
        description: descripcion,
        startDate: fechaInicio,
        endDate: fechaFin
    };
});

When('se crea la rutina semanal con descripción', function(){
    this.weeklyRoutine = post('weekly-routines', this.weeklyRoutine);
});

Given('que se intenta crear la rutina diaria para {string} con la rutina semanal que dura desde {string} hasta {string}, y con la sesión {string}', function(diaSemana, inicioiRutinaSemanal, finRutinaSemanal, nombreSesion){
    const rutinaSemanal = get(`weekly-routines/by-dates?startDate=${inicioiRutinaSemanal}&endDate=${finRutinaSemanal}`);
    const sesion = get(`sessions/by-name?name=` + nombreSesion);
    this.routineDay = {
        day: diaSemana,
        routine: rutinaSemanal,
        session: sesion
    }
});

When('se crea la rutina diaria', function(){
    this.routineDay = post('routine-days', this.routineDay); // Esta asignación guardará la rutina y además, la devolverá con ID y un estado fijado por el backend
});

Then('el estado de la rutina debería ser {string}', function(estadoEsperado){
    assert.equal(estadoEsperado, this.routineDay.status);
});