var rest = require('restler');
var qs = require('querystring');

exports.getEntities = function(req, res, url){
    console.log("get entities");
    var query = qs.stringify(req.query);
    if (query) url = url + "?" + query;
    console.log(url);

    rest.get(url).on('complete', function(result) {
        res.send(result.items);
    });
};

exports.get = function(req, res, url){
    console.log("get entity");
    var query = qs.stringify(req.query);
    console.log(query);
    if (query) url = url + "?" + query;
    console.log(url);

    rest.get(url).on('complete', function(result) {
        if (result instanceof Error) {
            console.log('Error:', result.message);
            res.send(result.message);
        } else {
            res.send(result);
        }
    });
};

exports.update = function(req, res, url){
    console.log("update entity");
    console.log(url);
    console.log(req.body);
    rest.putJson(url, req.body).on('complete', function(result) {
        if (result instanceof Error) {
            console.log('Error:', result.message);
            res.send(result.message);
        } else {
            res.send(result);
        }
    });
};

exports.create = function(req, res, url){
    console.log("create offer");
    console.log(url);
    console.log(req.body);
    rest.postJson(url, req.body).on('complete', function(result) {
        if (result instanceof Error) {
            console.log('Error:', result.message);
            res.send(result.message);
        } else {
            res.send(result);
        }
    });
};