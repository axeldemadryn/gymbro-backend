const { Given, When } = require('@cucumber/cucumber');
const { get, postConAgregacion } = require('./common');

Given('que se tiene la sesión con nombre {string} y descripción {string}', function(name, description){
    console.log(`Se asociará al usuario Enrique López una sesión con nombre ${name} y descripción ${description}.`);
    this.sesion = {name, description}; // El usuario se asigna en el back
});

When('se carga la sesión', async function(){
    console.log('Cargamos la sesión...')
    await postConAgregacion('sessions', 'sessions', this.sesion);
});

Given('que se tiene el ejercicio con nombre {string}, tipo {string}, descripción {string} y músculo {string}', async function(nombre, tipo, descripcion, nombreMusculo){
    console.log(`Ahora cargamos un ejercicio (personalizado para Enrique López) con nombre ${nombre} y descripción ${descripcion}, de tipo: ${tipo} asociado al músculo ${nombreMusculo}.`);

    // Buscamos el músculo por nombre para obtener su ID y así enviarlo en la propiedad `musculos`
    const musculo = await get(`musculos/nombre/${nombreMusculo}`);
    if (!musculo?.id) {
        throw new Error(`No se encontró el músculo con nombre ${nombreMusculo}. Asegúrate de que se haya creado antes de cargar ejercicios.`);
    }

    // Construimos el objeto ejercicio con la lista de músculos (el backend espera objetos con id)
    this.ejercicio = { nombre, tipo, descripcion, musculos: [{ id: musculo.id }] };
});

When('se carga el ejercicio', async function(){
    console.log('Cargamos el ejercicio...');
    await postConAgregacion('ejercicios/crear-usuario', 'ejercicios', this.ejercicio);
});

Given('que se quiere asociar a la sesión {string} con el ejercicio {string}, con una cantidad de {int} sets y {int} reps', async function(nombreSesion, nombreEjercicio, sets, reps){
    console.log(`Ahora crearemos una asociación entre la sesión ${nombreSesion} con el ejercicio ${nombreEjercicio} (ambos de Enrique López), y dicha asociación estará asociada a una cantidad de ${sets} sets y ${reps} repeticiones.`);

    const session = await get(`sessions/name/${nombreSesion}`);
    const exercise = await get(`ejercicios/nombre/${nombreEjercicio}`);

    this.sessionExerciseDTO = {
        sets,
        reps,
        sessionId: session.id,
        exerciseId: exercise.id
    };
});

When('se asocia a la sesión y el ejercicio', async function(){
    console.log('Ahora asociamos a la sesión con el ejercicio...');
    await postConAgregacion('sessions-exercises', 'sessions-exercises', this.sessionExerciseDTO);
});