const assert = require('assert');
const request = require('sync-request');
const { Then, AfterAll } = require('@cucumber/cucumber');

// URL base del backend
const URL = 'http://backend:8080/api/';

let lastResponse = null;

let weeklyRoutines = [];
let routineDays = [];

// AGREGADO para manejar user
function doRequest(method, path, body = null, token = null) {
    try {
        const opts = body ? {
            body: JSON.stringify(body),
            headers: {
                'Content-Type': 'application/json',
                ...(token ? { Authorization: `Bearer ${token}` } : {})
            }
        } : { headers: token ? { Authorization: `Bearer ${token}` } : {} };

        const res = request(method, encodeURI(URL + path), opts);
        const txt = res.getBody('utf8') || '';
        lastResponse = txt ? JSON.parse(txt) : null;
        return lastResponse ? lastResponse.data : null;
    } catch (err) {
        lastResponse = { status: err.status, message: err.message || 'Error desconocido', data: err.data };
        return err.data;
    }
}

function post(path, body, token = null) { return doRequest('POST', path, body, token); }
function get(path, token = null) { return doRequest('GET', path, null, token); }
function put(path, body, token = null) {
    return doRequest('PUT', path, body, token);
}
function deleteReq(path, token = null) {
    try {
        // permitimos DELETE que devuelva 204/no content
        doRequest('DELETE', path, null, token);
        return true;
    } catch (err) {
        // devolver false en limpieza; caller puede loguear
        return false;
    }
}


/* Asserts */
Then('se debería obtener el mensaje {string}', function (expected) {
    if (!lastResponse) throw new Error('No hay respuesta disponible del backend.');
    assert(
        lastResponse.message && lastResponse.message.includes(expected),
        `Se esperaba "${expected}", pero se obtuvo: ${JSON.stringify(lastResponse)}`
    );
});

Then('se obtiene un error con mensaje {string}', function (expected) {
    if (!lastResponse) throw new Error('No hay respuesta disponible del backend.');
    assert(
        (lastResponse.error && lastResponse.error.includes(expected)) ||
        (lastResponse.message && lastResponse.message.includes(expected)),
        `Se esperaba error "${expected}", pero se obtuvo: ${JSON.stringify(lastResponse)}`
    );
});

/* AfterAll: eliminar todas las weekly-routines y routine-days usadas para el testing */
AfterAll(() => {
    // Primero eliminar los routineDays, porque dependen de weeklyRoutines
    try {
        routineDays.forEach(day => {
            if (day && day.id) {
                deleteReq(`routine-days/${day.id}`, userToken);
            }
        });
    } catch (e) {
        console.warn('AfterAll: error eliminando routineDays:', e.message);
    }

    // Luego eliminar las weeklyRoutines
    try {
        weeklyRoutines.forEach(weekly => {
            if (weekly && weekly.id) {
                deleteReq(`weekly-routines/${weekly.id}`, userToken);
            }
        });
    } catch (e) {
        console.warn('AfterAll: error eliminando weeklyRoutines:', e.message);
    }

    console.log('Limpieza completada.');
});


function addWeeklyRoutine(r) {
    weeklyRoutines.push(r);
}

function addRoutineDay(r) {
    routineDays.push(r);
}

module.exports = { get, post, put, deleteReq, addWeeklyRoutine, addRoutineDay };

// AGREGADO para manejar user

const { Before } = require('@cucumber/cucumber');

const testUser = {
    nombre: 'Usuario Test',
    email: 'testuser@example.com',
    password: '123456'
};

// Token del usuario para todos los steps
let userToken = null;

Before(function () {
    // Registrar usuario
    let registerRes = null;
    try {
        registerRes = post('users/register', testUser);
    } catch (e) {
        console.log('Usuario ya registrado, continuando...');
    }

    // Extraer token de verificación del registro
    const verifyToken = registerRes?.token;
    if (verifyToken) {
        // Verificar cuenta usando GET
        try {
            const verifyRes = get(`users/verify?token=${verifyToken}`);
            console.log('Cuenta verificada:', verifyRes);
        } catch (e) {
            console.log('Error verificando cuenta:', e.message);
        }
    } else {
        console.log('Token de verificación no disponible, se asume usuario ya activo.');
    }

    // Login del usuario
    const loginRes = post('users/login', {
        email: testUser.email,
        password: testUser.password
    });

    if (!loginRes || !loginRes.token) throw new Error('No se pudo loguear al usuario de prueba');

    userToken = loginRes.token;
    this.userToken = userToken; // disponible para todos los steps
});

