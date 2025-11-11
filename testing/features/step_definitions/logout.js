const { Given, When } = require('@cucumber/cucumber');
const { post } = require('./common');

Given('que el usuario Enrique López desea hacer logout', function(){
    console.log('\n\nFinalmente el usuario Enrique López decide hacer logout..');
});

When('hace logout', async function(){
    console.log('Procede a hacer el logout...');
    await post('users/logout');
});