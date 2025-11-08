const assert = require('assert');
const request = require('sync-request');
const { BeforeAll, Then, AfterAll } = require('@cucumber/cucumber');

const URL = 'http://backend:8080/api/'; // URL base del backend

let lastResponse = null;

let hoy, lunes, domingo; // días de la semana

/*********************** Token de usuario con sus getters y setters **************************/
let userToken = null; // Token del usuario para todos los steps

function getUserToken(){
    return userToken;
}

function setUserToken(newToken){
    userToken = newToken;
}

/******************************* Funciones de request ************************/

function doRequest(method, path, body = null, token = userToken) {
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

// Consultas GET, PUT y DELETE
function get(path, token = userToken) { return doRequest('GET', path, null, token); }
function put(path, body, token = userToken) { return doRequest('PUT', path, body, token); }
function deleteReq(path, token = userToken) { return doRequest('DELETE', path, null, token); }

/** Clase para mapear los datos que ya estaban en el backend antes del test, o se fueron cargando
 * durante el mismo. Soporta agregado de datos y eliminación
*/

class MapaDatosBackend {

    constructor() {
        // Map para almacenar colecciones de datos del backend: se pueden agregar nuevos tipos aquí posteriormente.
        this.colecciones = new Map([
            ['users', []],
            ['weekly-routines', []],
            ['routine-days', []]
        ]);
        this.prioridad = ['users', 'routine-days', 'weekly-routines'];
        this.rutasPost = {
            'users': 'users/register',
            'routine-days': 'routine-days',
            'weekly-routines': 'weekly-routines'
        };
    }

    // Agrega un elemento a la colección correspondiente
    agregarElemento(tipo, elemento) {
        if (!this.colecciones.has(tipo))
            throw new Error(`No existe la colección: ${tipo}`);
        this.colecciones.get(tipo).push(elemento);
    }
}

let datosAntesTest = new MapaDatosBackend();
let datosDespuesTest = new MapaDatosBackend();

// Consulta POST e inserción de datos
function post(path, body, token = null) {
    const data = doRequest('POST', path, body, token); // Hace el post del body y lo recupera luego
    const clave_almacenamiento = path.startsWith('/') // Elimina barra inicial si existe y luego toma el primer segmento
        ? path.substring(1).split("/")[0] // substring(1) elimina el primer elemento (la barra inicial, si aplica)
        : path.split("/")[0];
    datosDespuesTest.agregarElemento(clave_almacenamiento, data); // Inserta el body entre los almacenados
    return data; // Retorna el body
}

/************************************BeforeAll para manejar user*******************************************/

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

    /*** Obtención y eliminación del backend de datos previamente almacenados ***/

    // Función para cargar datos en datosAntesTest
    for (const tipo of datosAntesTest.prioridad) {
        const datos = get(tipo);
        datosAntesTest.colecciones.set(tipo, Array.isArray(datos) ? datos : []);
    }

    try {
        for (const tipo of datosAntesTest.prioridad) {
            if(Array.isArray(datosAntesTest.colecciones.get(tipo))){
                for (const elemento of datosAntesTest.colecciones.get(tipo).slice().reverse()) {
                    if (elemento?.id) deleteReq(`${tipo}/${elemento.id}`);
                }
            } else {
                console.warn(`Error La colección de tipo ${tipo} no está registrada como un arreglo. Al parecer, se imprime como:\n`);
                console.warn(datosAntesTest.colecciones.get(tipo));
            }
        }
    } catch (e) {
        console.warn('BeforeAll: error eliminando datos previos al testing:', e.message);
    }
    //console.log(datosAntesTest.colecciones);
});

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
    if (responseToCheck.message && responseToCheck.message.includes(expected)) {
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
    console.log(`Se obtuvo el mensaje: ${actualMessage}\n\n`); 
});

/***********AfterAll: eliminar todas las weekly-routines y routine-days usadas para el testing****************/
AfterAll(() => {
    // Primero eliminar los routineDays, porque dependen de weeklyRoutines
    try {
        for (const tipo of datosDespuesTest.prioridad) {
            for (const elemento of datosDespuesTest.colecciones.get(tipo).slice().reverse()) {
                if (elemento?.id) deleteReq(`${tipo}/${elemento.id}`);
            }
        }
    } catch (e) {
        console.warn('AfterAll: error eliminando datos de testing:', e.message);
    }

    //console.log('Limpieza completada.');

    for (const tipo of datosAntesTest.prioridad) {
        if(Array.isArray(datosAntesTest.colecciones.get(tipo))){
            for (const elemento of datosAntesTest.colecciones.get(tipo)) {
                let x = post(datosAntesTest.rutasPost[tipo], elemento);
                //console.log(x);
            }
        }
    }

    //console.log('Recarga de datos previos completada.');
});

/*******************************************************************************************************/

module.exports = { get, post, put, deleteReq, getUserToken, setUserToken, hoy, lunes, domingo };
