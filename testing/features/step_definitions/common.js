const assert = require('assert');
const request = require('sync-request');
const { Then, AfterAll } = require('@cucumber/cucumber');

// URL base del backend
const URL = 'http://backend:8080/api/';

let lastResponse = null;

function doRequest(method, path, body = null) {
    try {
        const opts = body ? { body: JSON.stringify(body), headers: { 'Content-Type': 'application/json' } } : undefined;
        const res = request(method, encodeURI(URL + path), opts);
        const txt = res.getBody('utf8') || '';
        lastResponse = txt ? JSON.parse(txt) : null;
        return lastResponse ? lastResponse.data : null;
    } catch (err) {
        // sync-request deja respuesta accesible en err.response
        try {
            const buf = err.response && (err.response.getBody ? err.response.getBody() : err.response.body);
            const txt = buf ? buf.toString('utf8') : null;
            lastResponse = txt ? JSON.parse(txt) : { message: txt || err.message };
        } catch (e) {
            lastResponse = { message: err.message || 'Error desconocido' };
        }
        // re-throw para que quien llamó pueda decidir (steps usan try/catch para logging)
        lastResponse = { status: err.status, message: err.message || 'Error desconocido', data: err.data };
        return err.data;
    }
}

function get(path) { return doRequest('GET', path); }
function post(path, body) { return doRequest('POST', path, body); }
function put(path, body) { return doRequest('PUT', path, body); }
function deleteReq(path) {
    try {
        // permitimos DELETE que devuelva 204/no content
        doRequest('DELETE', path);
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

/* AfterAll: eliminar todas las weekly-routines y routine-days */
AfterAll(() => {
    try {
        const days = get('routine-days') || [];
        if (Array.isArray(days)) {
            days.forEach(d => { if (d.id) deleteReq(`routine-days/${d.id}`); });
        } else if (days && days.id) {
            deleteReq(`routine-days/${days.id}`);
        }
    } catch (e) {
        console.warn('AfterAll: error limpiando routine-days:', e.message);
    }

    try {
        const weekly = get('weekly-routines') || [];
        if (Array.isArray(weekly)) {
            weekly.forEach(w => {
                if (w.id) deleteReq(`weekly-routines/${w.id}`);
            });
        } else if (weekly && weekly.id) {
            deleteReq(`weekly-routines/${weekly.id}`);
        }
    } catch (e) {
        console.warn('AfterAll: error limpiando weekly-routines:', e.message);
    }

    console.log('Limpieza completada.');
});

module.exports = { get, post, put, deleteReq };