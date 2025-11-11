const assert = require('assert');
const request = require('sync-request');
const { BeforeAll, Then, AfterAll } = require('@cucumber/cucumber');

const URL = 'http://backend:8080/api/'; // URL base del backend

let lastResponse = null;

let hoy, lunes, domingo; // días de la semana

/***************** Token de usuario para todos los steps, con sus getters y setters ****************/
let userToken = null;

function getUserToken(){
    return userToken;
}

function setUserToken(newToken){
    userToken = newToken;
}

let usuarioId = 0;

function asignarIdUsuario(id){
    usuarioId = id;
}

/** Literal que almacena objetos cargados en el backend durante el test, para su futura eliminación
 * Aunque no se crea, a pesar de ser constante, los arreglos son variables (la referencia a datosTest
 * es constante, pero se puede agregar o quitar elementos a los arreglos de datosTest).
*/
const datosTest = {
    'musculos': [],
    'sessions': [],
    'ejercicios': [],
    'sessions-exercises': [],
    'weekly-routines': [],
    'routine-days': []
};

// Función para esperar una cantidad de milisegundos, dando tiempo a la operación anterior para validarse
function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

/******************************* Funciones de request ************************/

async function doRequest(method, path, body = null, token = userToken) {
    try {
        await sleep(200); // Espera 200ms para asegurar que la operación se haya completado
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
        await sleep(200);
        return lastResponse ? lastResponse.data : null;
    } catch (err) {
        lastResponse = { status: err.status, message: err.message || 'Error desconocido', data: err.data };
        await sleep(200);
        return err.data;
    }
}

function get(path, token = userToken) { return doRequest('GET', path, null, token); }
function put(path, body, token = userToken) { return doRequest('PUT', path, body, token); }
function deleteReq(path, token = userToken) { return doRequest('DELETE', path, null, token); }
function post(path, body = null, token = userToken) { return doRequest('POST', path, body, token); } // Post común y corriente
/** postConAgregación hace un POST, agrega el resultado del POST a uno de los arreglos de datosTest, y
 * devuelve el valor del POST.
*/
function postConAgregacion(pathUrl, pathDatos, body, token = userToken){
    const data = doRequest('POST', pathUrl, body, token);
    datosTest[pathDatos].push(data);
    return data;
}

/*************************** BeforeAll para inicializar fechas y día de semana de hoy ******************************/

BeforeAll(function () {
    /*** Seteo del día de semana de hoy y las fechas del lunes y domingo de esta semana ***/

    const diaHoy = new Date(); // Fecha actual según UTC, o GMT-3 (según el formato de horario en el backend o docker-compose.yml)

    const diaSemana = diaHoy.getDay(); // Obtener día de la semana (0 = domingo, 1 = lunes, ..., 6 = sábado)

    // Asignar el nombre del día de hoy (en mayúsculas, igual que en el backend)
    const nombresDias = ["DOMINGO", "LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES", "SABADO"];
    hoy = nombresDias[diaSemana];

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
        const anio = d.getFullYear(); // año
        const mes = String(d.getMonth() + 1).padStart(2, '0');
        const dia = String(d.getDate()).padStart(2, '0');
        return `${anio}-${mes}-${dia}`;
    };

    // Se setean los días lunes y domingo de semana en el formato adecuado
    lunes = formatoISO(diaLunes);
    domingo = formatoISO(diaDomingo);
});

// Getters para exponer los valores actuales después de que se ejecute BeforeAll
function getHoy() { return hoy; }
function getLunes() { return lunes; }
function getDomingo() { return domingo; }

/******************************************* Assert *******************************************************/
Then('se debería obtener el mensaje {string}', function (expected) {
    if (!lastResponse) throw new Error('No hay respuesta disponible del backend.');
    
    let found = false;
    let actualMessage = 'Respuesta no coincidente';
    let responseToCheck = lastResponse;
    
    // --- LÓGICA DE LIMPIEZA DE MENSAJE DE ERROR ---
    // Si la respuesta es una excepción de sync-request (cadena sucia), extraemos el JSON limpio.
    if (typeof lastResponse.message === 'string' && lastResponse.message.startsWith('Server responded to')) {
        const rawMessage = lastResponse.message;
        // Regex para extraer el objeto JSON que empieza con '{' y termina con '}'
        const jsonMatch = rawMessage.match(/\{.*\}$/s); 
        
        if (jsonMatch) {
            try {
                // 1. Reemplazamos el objeto a chequear con el JSON limpio extraído
                responseToCheck = JSON.parse(jsonMatch[0]); 
            } catch (e) {
                // Si falla el parseo del JSON incrustado, mantenemos el mensaje original para que el assert falle si es necesario.
            }
        }
    }
    // --- FIN LÓGICA DE LIMPIEZA ---

    // Check 1: Buscar en el mensaje principal (incluye "OK" y errores de servicio 409)
    if (responseToCheck.message?.includes(expected)) {
        found = true;
        actualMessage = responseToCheck.message; 
    }
    
    // Check 2: Buscar en los campos de datos (Errores de validación 400)
    if (responseToCheck.data && typeof responseToCheck.data === 'object') {
        for (const key in responseToCheck.data) {
            if (typeof responseToCheck.data[key] === 'string' && responseToCheck.data[key].includes(expected)) {
                found = true;
                actualMessage = responseToCheck.data[key]; // El mensaje de error específico (ej. "El nombre no puede estar vacío")
                break;
            }
        }
    }

    // Fallback: Si no se encontró (test fallido), usamos el mensaje limpio o el mensaje completo del backend.
    if (!found) {
        // Para el mensaje de fallo del assert, mostramos la versión más limpia posible
        actualMessage = responseToCheck.message || JSON.stringify(responseToCheck);
    }
    
    assert(
        found,
        `Se esperaba "${expected}", pero se obtuvo: ${actualMessage}`
    );
    
    // El log ahora mostrará la cadena limpia almacenada en actualMessage
    console.log(`Se obtuvo el mensaje: ${actualMessage}.`); 
});

/***********AfterAll: eliminar todas las weekly-routines y routine-days usadas para el testing****************/
AfterAll(async () => {
    // Primero eliminar los routineDays, porque dependen de weeklyRoutines
    try {
        for (const tipo of ['routine-days', 'weekly-routines', 'sessions-exercises', 'ejercicios', 'sessions', 'musculos']) {
            for (const elemento of datosTest[tipo]) {
                if (elemento?.id) await deleteReq(`${tipo}/${elemento.id}`);
            }
        }
        await deleteReq(`users/delete-id/${usuarioId}`);
    } catch (e) {
        console.warn('AfterAll: error eliminando datos de testing:', e.message);
    }

    console.log('\n\nLimpieza completada.');
});

/*******************************************************************************************************/

module.exports = { get, put, post, postConAgregacion, deleteReq, getUserToken, setUserToken, asignarIdUsuario, getHoy, getLunes, getDomingo };
