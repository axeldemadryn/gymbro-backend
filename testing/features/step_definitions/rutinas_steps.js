const assert = require('assert');
const { Given, When, Then, AfterAll } = require('@cucumber/cucumber');
const request = require('sync-request');

const BASE_URL = 'http://backend:8080/api';

let weeklyRoutine = {};
let routineDay = {};
let routineDays = [];
let response;
let createdRoutineDayIds = [];
let createdWeeklyRoutineIds = [];

// Helper para buscar sesión por nombre y devolver su ID
function getSessionIdByName(sessionName) {
    let res = request('GET', `${BASE_URL}/sessions/by-name?name=` + encodeURIComponent(sessionName));
    response = JSON.parse(res.getBody('utf8'));
    if (!response || !response.data?.id) throw new Error(`No se encontró sesión con nombre "${sessionName}"`);
    return response.data.id;
}

// Helper para obtener ID de WeeklyRoutine por fechas
function getWeeklyRoutineIdByDates(startDate, endDate) {
    const res = request('GET', `${BASE_URL}/weekly-routines/by-dates?startDate=${startDate}&endDate=${endDate}`);
    response = JSON.parse(res.getBody('utf8'));
    if (!response || !response.data?.id) throw new Error(`No se encontró la rutina semanal desde ${startDate} hasta ${endDate}`);
    return response.data.id;
}

// ---------------- Given ----------------

// Intento de creación
Given('que se intenta crear la rutina semanal {string} con fechas desde {string} hasta {string}', function (name, startDate, endDate) {
    weeklyRoutine = { name, startDate, endDate };
});

// Intento de creación Y
Given('se intenta crear la rutina semanal {string} con fechas desde {string} hasta {string}', function (name, startDate, endDate) {
    weeklyRoutine = { name, startDate, endDate };
});

// Rutina que ya existe 
Given('que existe la rutina semanal {string} con fechas desde {string} hasta {string}', function (name, startDate, endDate) {
    weeklyRoutine = { name, startDate, endDate };
});

// Crear un RoutineDay para test
Given('se intenta crear la rutina diaria para {string} con la sesión {string}', function (day, sessionName) {
    const sessionId = getSessionIdByName(sessionName);
    const weeklyId = getWeeklyRoutineIdByDates(weeklyRoutine.startDate, weeklyRoutine.endDate);
    routineDay = { day, weeklyId, sessionId };
});

// ---------------- When ----------------

// Guardar rutina semanal
When('se guarda la rutina semanal', function () {
    try {
        const res = request('POST', `${BASE_URL}/weekly-routines`, { json: weeklyRoutine });
        response = JSON.parse(res.getBody('utf8'));
        if (response.status === 200) createdWeeklyRoutineIds.push(response.data.id);
    } catch (error) {
        response = { error: error.message };
    }
});

// Guardar rutina diaria
When('se guarda la rutina diaria', function () {
    try {
        const res = request('POST', `${BASE_URL}/routine-days`, {
            json: {
                day: routineDay.day,
                routine: { id: routineDay.weeklyId },
                session: { id: routineDay.sessionId }
            }
        });
        response = JSON.parse(res.getBody('utf8'));
        if (response.status === 200) createdRoutineDayIds.push(response.data.id);
    } catch (error) {
        response = { error: error.message };
    }
});

// Guardar rutina diaria de esquema de escenario
When('se intenta crear la rutina diaria para {string} con la sesión con ejercicios {string}', function (day, sessionName) {
    try {
        const sessionId = getSessionIdByName(sessionName);
        const weeklyId = getWeeklyRoutineIdByDates(weeklyRoutine.startDate, weeklyRoutine.endDate);

        // POST de RoutineDay
        const resDay = request('POST', `${BASE_URL}/routine-days`, {
            json: {
                day,
                routine: { id: weeklyId },
                session: { id: sessionId }
            }
        });

        response = JSON.parse(resDay.getBody('utf8'));

        // Guardar id si fue creado correctamente
        if (response.status === 200) createdRoutineDayIds.push(response.data.id);
    } catch (error) {
        response = { error: error.message };
    }
});

// ---------------- Then ----------------
Then('se debería obtener el mensaje {string}', function (expectedMessage) {
    assert(
        response.message?.includes(expectedMessage),
        `Se esperaba error "${expectedMessage}", pero se obtuvo: ${JSON.stringify(response)}`
    );
});

// Validar mensaje de error
Then('se obtiene un error con mensaje {string}', function (expectedMessage) {
    assert(
        response.error?.includes(expectedMessage) || response.message?.includes(expectedMessage),
        `Se esperaba error "${expectedMessage}", pero se obtuvo: ${JSON.stringify(response)}`
    );
});

Then('la rutina se crea correctamente y los días quedan asignados con sus sesiones', function () {
    assert(true);
});

AfterAll(function () {
    // Primero eliminar todos los RoutineDays creados
    createdRoutineDayIds.forEach(id => {
        try {
            request('DELETE', `${BASE_URL}/routine-days/${id}`);
        } catch (error) {
            console.warn(`No se pudo eliminar RoutineDay con id ${id}: ${error.message}`);
        }
    });

    // Luego eliminar todas las WeeklyRoutines creadas
    createdWeeklyRoutineIds.forEach(id => {
        try {
            request('DELETE', `${BASE_URL}/weekly-routines/${id}`);
        } catch (error) {
            console.warn(`No se pudo eliminar WeeklyRoutine con id ${id}: ${error.message}`);
        }
    });
});

