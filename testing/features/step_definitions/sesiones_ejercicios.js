const { Given, When } = require('@cucumber/cucumber');
const { get, post } = require('./common');

Given('que se tiene la sesión con nombre {string} y descripción {string}', function(name, description){
    this.sesion = {name, description}; // El usuario se asigna en el back
});

When('se carga la sesión', function(){
    post('sessions', this.session);
});

Given('que se tiene el ejercicio con nombre {string}, tipo {string} y descripción {string}', function(nombre, tipo, descripcion){
    this.ejercicio = {nombre, tipo, descripcion}; // El usuario se asigna, de nuevo, en el back
});

When('se carga el ejercicio', function(){
    post('ejercicios/crear-usuario', this.ejercicio);
});

Given('que se quiere asociar a la sesión {string} con el ejercicio {string}, con una cantidad de {int} sets y {int} reps', function(nombreSesion, nombreEjercicio, sets, reps){
    const session = get(`sessions/name/${nombreSesion}`);
    const exercise = get(`ejercicios/nombre/${nombreEjercicio}`);

    this.sessionExerciseDTO = {
        sets,
        reps,
        sessionId: session.id,
        exerciseId: exercise.id
    };
});

When('se asocia a la sesión y el ejercicio', function(){
    post('sessions-exercises', this.sessionExerciseDTO);
});