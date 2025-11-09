const { Given, When } = require('@cucumber/cucumber');
const { post, lunes, domingo } = require('./common');

// Crear rutina semanal en general
Given('que se intenta crear la rutina semanal {string} con descripción "<descripcion>" y fechas desde {string} hasta {string}', function (name, description, startDate, endDate) {
    this.weeklyRoutine = { name, description, startDate, endDate };
});

// Guardar rutina semanal
When('se guarda la rutina semanal', function () {
    try {
        this.weeklyRoutine = post('weekly-routines', this.weeklyRoutine);
    } catch (error) {
        console.warn('Error al guardar la rutina semanal: ' + error.message);
    }
});

// Crear rutina semanal para esta semana
Given('que se intenta crear una rutina semanal para esta semana', function(){
    // Crea la rutina semanal
    this.weeklyRoutine = {
        name: 'Rutina de esta semana',
        description: 'Rutina creada para esta semana para almacenar la rutina diaria del día de hoy.',
        startDate: lunes,
        endDate: domingo
    };
});