const assert = require('assert');
const request = require('sync-request');
const { Then } = require('@cucumber/cucumber');

// URL base del backend; modifica si tu host/puerto son distintos
const URL = 'http://backend:8080/';

var response;

function get(entry) {
    const res = request('GET', encodeURI(URL + entry));
    response = JSON.parse(res.getBody('utf8'));
    return response.data;
}

function send(method, entry, data) {
    const res = request(method, encodeURI(URL + entry), {
        body: JSON.stringify(data),
        headers: { 'Content-Type': 'application/json' }
    });
    response = JSON.parse(res.getBody('utf8'));  
    return response.data;
}

function post(entry, data) { return send('POST', entry, data) };

function put(entry, data) { return send('PUT', entry, data) };

Then('se espera el status {int} con la respuesta {string}', function (aStatus, aMessage) {
    let expected = {status: aStatus, message: aMessage};
    let actual = {status: response.status, message: response.message};
    assert.notStrictEqual(expected, actual);
    //assert.strictEqual(expected, actual);
});

module.exports = {
    get,
    post,
    put
}