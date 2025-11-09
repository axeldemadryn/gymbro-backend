const { Given, When } = require('@cucumber/cucumber');
const { get, post, setUserToken } = require('./common');

/********* Registro de usuarios y verificación de cuenta ***********/
Given('que se tiene el usuario con nombre {string}, e-mail {string} y contraseña {string}', function(nombre, email, password){
    this.usuario = {nombre, email, password};
    console.log(`Cargamos usuario con nombre: ${nombre}, e-mail: ${email} y contraseña: ${password}.`);
});

When('se guarda al usuario', function(){
    console.log('Guardamos al usuario.');
    this.usuario = post('users/register', this.usuario);
});

When('se quiere guardar al usuario y luego verificar su cuenta', function(){
    console.log('Guardamos al usuario y verificamos su cuenta.');
    const registerRes = post('users/register', this.usuario);
    get(`users/verify?token=${registerRes?.token}`);
});

/********* Login de usuario ***********/
Given('que la cuenta con e-mail {string} ya fue verificada', function(email){
    console.log(`Buscamos al usuario con e-mail ${email}.`);
    this.usuario = get(`users/by-email/${email}`);
});

When('hace login y obtiene el token', function(){
    console.log('Dicho usuario ahora hace login.');
    const loginRes = post('users/login', {
        email: this.usuario.email,
        password: this.usuario.password
    });
    if (!loginRes?.token) {
        console.warn('No se pudo loguear al usuario de prueba. La cuenta puede estar inactiva o con credenciales incorrectas.');
    }
    setUserToken(loginRes.token);
});

/**** Código viejo de asignación de usuario (antes en el BeforeAll) ***/
    
    // let tokenToVerify = null;

    // // 1. Intentar registrar el usuario. Si ya existe, capturar el error.
    // try {
    //     const registerRes = post('users/register', testUser);
    //     tokenToVerify = registerRes?.token; // Token si el registro es nuevo
    // } catch (e) {
    //     console.log('Usuario de prueba ya registrado. Intentando reenviar verificación...');
        
    //     // 2. Si el registro falló, forzar el reenvío para obtener un token fresco.
    //     try {
    //         const resendRes = post('users/resend-verification', { email: testUser.email.slice().reverse() });
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