const { Given, When } = require('@cucumber/cucumber');
const { postConAgregacion } = require('./common');

Given('que se tiene el músculo con nombre {string}', function(nombre){
    console.log(`Agregamos al músculo con nombre ${nombre}`);
    this.musculo = {nombre};
});

When('se carga dicho músculo en la BD', async function(){
    console.log('Cargamos el músculo...');
    await postConAgregacion('musculos', 'musculos', this.musculo);
});