const assert = require('assert');
const request = require('sync-request');
const { BeforeAll, Then, AfterAll } = require('@cucumber/cucumber');

const URL = 'http://backend:8080/api/'; // URL base del backend

let lastResponse = null;

let userToken = null; // Token del usuario para todos los steps

// Pegar acá abajo (sobre <mi-token>) el token creado localmente
const miToken =
    'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJnYWJpQGVtYWlsLmNvbSIsInRpcG8iOiJzZXNpb24iLCJpYXQiOjE3NjE2ODMyOTgsImV4cCI6MTc2MjI4ODA5OH0.g9faRnekEiJlmSr0d_rUJbF-l2XJoujvJP831XCp2vo'
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

/*******************************************Asserts*******************************************************/
/*******************************************Asserts*******************************************************/
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
    console.log(`Se esperaba "${expected}", pero se obtuvo: ${actualMessage}`); 
});


// NOTA: Para el otro Then ('se obtiene un error con mensaje {string}'), aplique la misma lógica de 'LÓGICA DE LIMPIEZA DE MENSAJE DE ERROR' 
// para la variable 'lastResponse' antes de realizar los checks de 'found'.

Then('se obtiene un error con mensaje {string}', function (expected) {
    if (!lastResponse) throw new Error('No hay respuesta disponible del backend.');
    
    let found = false;
    let actualMessage = 'Respuesta no coincidente'; 

    // Búsqueda en el mensaje principal (error o message)
    if ((lastResponse.error && lastResponse.error.includes(expected))) {
        found = true;
        actualMessage = lastResponse.error; // ⬅️ ASIGNAMOS LA CADENA LIMPIA
    } else if (lastResponse.message && lastResponse.message.includes(expected)) {
        found = true;
        actualMessage = lastResponse.message; // ⬅️ ASIGNAMOS LA CADENA LIMPIA
    }

    // Búsqueda en los campos de datos (si es un error 400)
    if (lastResponse.data && typeof lastResponse.data === 'object') {
        for (const key in lastResponse.data) {
            if (typeof lastResponse.data[key] === 'string' && lastResponse.data[key].includes(expected)) {
                found = true;
                actualMessage = lastResponse.data[key]; // ⬅️ ASIGNAMOS LA CADENA LIMPIA
                break;
            }
        }
    }
    
    // Fallback para el mensaje de error
    if (!found) {
        actualMessage = lastResponse.message || JSON.stringify(lastResponse.data || lastResponse);
    }

    assert(
        found,
        `Se esperaba error "${expected}", pero se obtuvo: ${actualMessage}`
    );

    // LIMPIEZA: NO usar JSON.stringify aquí.
    console.log(`Se esperaba error "${expected}", pero se obtuvo: ${actualMessage}\n\n`);
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

module.exports = { get, post, put, deleteReq };
