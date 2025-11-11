const { Given, When } = require('@cucumber/cucumber');
const { post, postConAgregacion, lunes, domingo } = require('./common');

// Crear rutina semanal en general
Given('que se intenta crear la rutina semanal {string} con descripción {string} y fechas desde {string} hasta {string}', function (name, description, startDate, endDate) {
    console.log(`Ahora crearemos una rutina semanal de nombre ${name} con descripción ${description} que irá desde ${startDate} hasta ${endDate} (si no aparece nada en la descripción es porque ésta es opcional).`);
    this.weeklyRoutine = { name, description, startDate, endDate };
});

When('se intenta generar la rutina semanal', async function () {
    console.log('Guardamos la rutina semanal...');
    try {
        this.weeklyRoutine = await post('weekly-routines', this.weeklyRoutine);
    } catch (error) {
        console.warn('Error al guardar la rutina semanal: ' + error.message);
    }
});

// Guardar rutina semanal
When('se guarda la rutina semanal', async function () {
    console.log('Guardamos la rutina semanal...');
    try {
        this.weeklyRoutine = await postConAgregacion('weekly-routines', 'weekly-routines', this.weeklyRoutine);
    } catch (error) {
        console.warn('Error al guardar la rutina semanal: ' + error.message);
    }
});

// Crear rutina semanal para esta semana
Given('que se intenta crear una rutina semanal para esta semana', function(){
    console.log(`Ahora crearemos una rutina semanal de nombre Rutina de esta semana' con descripción Rutina creada para esta semana para almacenar la rutina diaria del día de hoy, que irá desde ${lunes} hasta ${domingo}, es decir, esta misma semana (si no aparece nada en la descripción es porque ésta es opcional).`);
    // Crea la rutina semanal
    this.weeklyRoutine = {
        name: 'Rutina de esta semana de Enrique para el test',
        description: 'Rutina creada para esta semana para almacenar la rutina diaria del día de hoy.',
        startDate: lunes,
        endDate: domingo
    };
});