const assert = require('assert');
const { Given, When, Then } = require('@cucumber/cucumber');
const post = require('./common').post;
const get = require('./common').get;
const put = require('./common').put;
const addWeeklyRoutine = require('./common').addWeeklyRoutine;
const addRoutineDay = require('./common').addRoutineDay;

/********Creación inicial de rutinas semanales***********/

let hoy, lunes, domingo;

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
    addWeeklyRoutine(this.weeklyRoutine);
});

/**************Creación y validación de estados de rutinas diarias***************/

Given('que se intenta crear la rutina diaria para {string} con la rutina semanal que dura desde {string} hasta {string}, y con la sesión {string}', function(diaSemana, inicioRutinaSemanal, finRutinaSemanal, nombreSesion){
    const rutinaSemanal = get(`weekly-routines/by-dates?startDate=${inicioRutinaSemanal}&endDate=${finRutinaSemanal}`);
    const sesion = get(`sessions/by-name?name=` + nombreSesion);
    this.routineDay = {
        day: diaSemana,
        routine: rutinaSemanal,
        session: sesion
    }
});

When('se crea la rutina diaria', function(){
    this.routineDay = post('routine-days', this.routineDay); // Esta asignación guardará la rutina y además, la devolverá con ID y un estado fijado por el backend
    addRoutineDay(this.routineDay);
});

Then('el estado de la rutina debería ser {string}', function(estadoEsperado){
    assert.equal(estadoEsperado, this.routineDay.status);
});

/**************Búsqueda de rutina diaria existente y cambio de estado a COMPLETADA*******************/

Given('que se tiene la rutina diaria para {string} con la rutina semanal que dura desde {string} hasta {string}', function(diaSemana, inicioRutinaSemanal, finRutinaSemanal){
    this.routineDay = get(`routine-days/by-day-and-weekly-routine-dates?day=${diaSemana}&startDate=${inicioRutinaSemanal}&endDate=${finRutinaSemanal}`);
});

When('se cambia el estado de la rutina diaria a COMPLETADA', function(){
    this.routineDay = put('routine-days/completada', this.routineDay);
    // No es necesario acá modificar nada en el common
});

/**************************Día de hoy******************************/

Given('que se quiere crear una rutina diaria para el día de hoy asociada a la sesión ya existente {string}', function(nombreSesion){
    const sesion = get(`sessions/by-name?name=` + nombreSesion); // Obtiene la sesión

    const diaHoy = new Date(); // Fecha actual según UTC, o GMT-3 (según el formato de horario en el backend o docker-compose.yml)

    const diaSemana = diaHoy.getDay(); // Obtener día de la semana (0 = domingo, 1 = lunes, ..., 6 = sábado)

    // Asignar el nombre del día de hoy (en mayúsculas, igual que en el backend)
    const nombresDias = ["DOMINGO", "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO"];
    hoy = nombresDias[diaSemana]; // IMPORTANTE, después se usa en el siguiente escenario

    // Variables lunes y domingo, que luego se calcularán para obtener los días lunes y domingo de la semana actual
    const diaLunes = new Date(diaHoy);
    const diaDomingo = new Date(diaHoy);

    // Ajustar para obtener lunes (si hoy es domingo, retroceder 6 días)
    const offsetLunes = diaSemana === 0 ? -6 : 1 - diaSemana;
    diaLunes.setDate(diaHoy.getDate() + offsetLunes);

    // Ajustar para obtener domingo
    const offsetDomingo = diaSemana === 0 ? 0 : 7 - diaSemana;
    diaDomingo.setDate(diaHoy.getDate() + offsetDomingo);

    // Función para guardar en formato YYYY-MM-DD para enviar al backend
    const formatoISO = d => {
        const anio = d.getFullYear();
        const mes = String(d.getMonth() + 1).padStart(2, '0');
        const dia = String(d.getDate()).padStart(2, '0');
        return `${anio}-${mes}-${dia}`;
    };

    // Se setean los días lunes y domingo de semana en el formato adecuado
    lunes = formatoISO(diaLunes);
    domingo = formatoISO(diaDomingo);

    // Crea la rutina semanal
    this.weeklyRoutine = {
        name: 'Rutina de esta semana',
        description: 'Rutina creada para esta semana para almacenar la rutina diaria del día de hoy.',
        startDate: lunes,
        endDate: domingo
    };

    this.weeklyRoutine = post('weekly-routines', this.weeklyRoutine); // Guarda la rutina semanal en la BD
    addWeeklyRoutine(this.weeklyRoutine);

    // Crea la rutina diaria
    this.routineDay = {
        day: hoy,
        routine: this.weeklyRoutine,
        session: sesion
    };
});

Given('que ya existe una rutina para el día de hoy', function(){
    this.routineDay = get(`routine-days/by-day-and-weekly-routine-dates?day=${hoy}&startDate=${lunes}&endDate=${domingo}`);
});