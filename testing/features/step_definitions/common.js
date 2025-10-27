const assert = require('assert');
const request = require('sync-request');
const { BeforeAll, Then, AfterAll } = require('@cucumber/cucumber');

const URL = 'http://backend:8080/api/'; // URL base del backend

let lastResponse = null;

let userToken = null; // Token del usuario para todos los steps

// Pegar acá abajo (sobre <mi-token>) el token creado localmente
const miToken =
    '<mi-token>'
;

// Datos de usuario de testings
const testUser = {
    nombre: 'Usuario Test',
    email: 'testuser@example.com',
    password: '123456'
};

function doRequest(method, path, body = null, token = miToken) {
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
function get(path, token = miToken) { return doRequest('GET', path, null, token); }
function put(path, body, token = miToken) { return doRequest('PUT', path, body, token); }
function deleteReq(path, token = miToken) { return doRequest('DELETE', path, null, token); }

class MapaDatosBackend {

    constructor() {
        // Map para almacenar colecciones de datos del backend: se pueden agregar nuevos tipos aquí posteriormente.
        this.colecciones = new Map([
            ['users', []]
            // ['users', []],
            // ['weekly-routines', []],
            // ['routine-days', []]
        ]);
        // this.prioridad = ['users', 'routine-days', 'weekly-routines'];
        this.prioridad = ['users'];
        this.rutasPost = {
            'users': 'users/register'
            // 'users': 'users/register',
            // 'routine-days': 'routine-days',
            // 'weekly-routines': 'weekly-routines'
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
    // /****Asignación de usuario ***/ (Descomentar para cuando se refactoricen los otros tests)
    
    // let tokenToVerify = null;

    // // 1. Intentar registrar el usuario. Si ya existe, capturar el error.
    // try {
    //     const registerRes = post('users/register', testUser);
    //     tokenToVerify = registerRes?.token; // Token si el registro es nuevo
    // } catch (e) {
    //     console.log('Usuario de prueba ya registrado. Intentando reenviar verificación...');
        
    //     // 2. Si el registro falló, forzar el reenvío para obtener un token fresco.
    //     try {
    //         const resendRes = post('users/resend-verification', { email: t.slice().reverse()estUser.email });
    //         tokenToVerify = resendRes?.token;
    //     } catch (resendError) {
    //         console.log('No se pudo reenviar la verificación. Asumiendo que el usuario está ACTIVO o que la cuenta ya fue verificada.');
    //     }
    // }

    // // 3. Verificar la cuenta si tenemos un token.
    // if (tokenToVerify) {
    //     try {
    //         get(`users/verify?token=${tokenToVerify}`);
    //         console.log('Cuenta verificada.');
    //     } catch (e) {
    //         console.warn('Error durante la verificación, pero continuamos (posiblemente ya estaba activo):', e.message);
    //     }
    // } else {
    //     console.log('Token de verificación no disponible, asumiendo usuario activo.');
    // }

    // // 4. Login del usuario
    // const loginRes = post('users/login', {
    //     email: testUser.email,
    //     password: testUser.password
    // });

    // if (!loginRes || !loginRes.token) {
    //     throw new Error('No se pudo loguear al usuario de prueba. La cuenta puede estar inactiva o con credenciales incorrectas.');
    // }

    // userToken = loginRes.token;
    // this.userToken = userToken; // disponible para todos los steps

    /*** Obtención y eliminación del backend de datos previamente almacenados ***/

    // Función para cargar datos en datosAntesTest
    for (const tipo of datosAntesTest.prioridad) {
        const datos = get(tipo);
        console.log(datos);
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
    console.log(datosAntesTest.colecciones);
});

/*******************************************Asserts*******************************************************/
Then('se debería obtener el mensaje {string}', function (expected) {
    if (!lastResponse) throw new Error('No hay respuesta disponible del backend.');
    
    let found = false;
    let actualMessage = JSON.stringify(lastResponse); // Mensaje por defecto para la aserción
    
    // Check 1: Buscar en el mensaje principal (Funciona para "OK", y mensajes 409/500 no validados)
    if (lastResponse.message && lastResponse.message.includes(expected)) {
        found = true;
    }
    
    // Check 2: Buscar en los campos de datos (Necesario para errores de validación 400)
    // El backend pone los errores de campo como {"email": "mensaje"} dentro de 'data'.
    if (lastResponse.data && typeof lastResponse.data === 'object') {
        for (const key in lastResponse.data) {
            if (typeof lastResponse.data[key] === 'string' && lastResponse.data[key].includes(expected)) {
                found = true;
                actualMessage = lastResponse.data[key]; // Capturar el mensaje específico de la data
                break;
            }
        }
    }
    
    assert(
        found,
        `Se esperaba "${expected}", pero se obtuvo: ${actualMessage}`
    );
});

Then('se obtiene un error con mensaje {string}', function (expected) {
    if (!lastResponse) throw new Error('No hay respuesta disponible del backend.');
    
    let found = false;
    let actualMessage = JSON.stringify(lastResponse);

    // Búsqueda en el mensaje principal (error o message)
    if ((lastResponse.error && lastResponse.error.includes(expected)) ||
        (lastResponse.message && lastResponse.message.includes(expected))) {
        found = true;
    }

    // Búsqueda en los campos de datos (si es un error 400)
    if (lastResponse.data && typeof lastResponse.data === 'object') {
        for (const key in lastResponse.data) {
            if (typeof lastResponse.data[key] === 'string' && lastResponse.data[key].includes(expected)) {
                found = true;
                actualMessage = lastResponse.data[key];
                break;
            }
        }
    }

    assert(
        found,
        `Se esperaba error "${expected}", pero se obtuvo: ${actualMessage}`
    );
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

    console.log('Limpieza completada.');

    for (const tipo of datosAntesTest.prioridad) {
        if(Array.isArray(datosAntesTest.colecciones.get(tipo))){
            for (const elemento of datosAntesTest.colecciones.get(tipo)) {
                let x = post(datosAntesTest.rutasPost[tipo], elemento);
                console.log(x);
            }
        }
    }

    console.log('Recarga de datos previos completada.');
});

/*******************************************************************************************************/

module.exports = { get, post, put, deleteReq };
