const { Given, When } = require('@cucumber/cucumber');
const get = require('./common').get;
const post = require('./common').post;

// ---------------- Given ----------------

// Intento de creación
Given('que se intenta crear la rutina semanal {string} con fechas desde {string} hasta {string}', function (name, startDate, endDate) {
    this.weeklyRoutine = { name, startDate, endDate };
});

// Intento de creación Y
Given('se intenta crear la rutina semanal {string} con fechas desde {string} hasta {string}', function (name, startDate, endDate) {
    this.weeklyRoutine = { name, startDate, endDate };
});

// Rutina que ya existe (acá la obtenemos completa directamente del backend)
Given('que existe la rutina semanal {string} con fechas desde {string} hasta {string}', function (name, startDate, endDate) {
    this.weeklyRoutine = get(`weekly-routines/by-dates?startDate=${startDate}&endDate=${endDate}`);
});

// Crear un RoutineDay para test
Given('se intenta crear la rutina diaria para {string} con la sesión {string}', function (day, sessionName) {
    let session = get(`sessions/by-name?name=` + sessionName);
    this.routineDay = {
        day: day,
        routine: this.weeklyRoutine,
        session: session
    };
});

// ---------------- When ----------------

// Guardar rutina semanal
When('se guarda la rutina semanal', function () {
    try {
        post('weekly-routines', this.weeklyRoutine);
    } catch (error) {
        console.warn('Error al guardar la rutina semanal: ' + error.message);
    }
});

// Guardar rutina diaria
When('se guarda la rutina diaria', function () {
    try {
        post(`routine-days`, this.routineDay);
    } catch (error) {
        console.warn('Error al crear la rutina diaria: ' + error.message);
    }
});