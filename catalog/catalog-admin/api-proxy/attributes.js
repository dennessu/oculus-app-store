var rest = require('restler');
var qs = require('querystring');

exports.getAttributes = function(req, res){
    console.log("get attributes");
    var query = qs.stringify(req.query);
    var url = "http://localhost:8091/rest/attributes";
    if (query) url = url + "?" + query;
    console.log(url);

    rest.get(url).on('complete', function(result) {
        res.send(result.results);
    });
};

exports.getAttribute = function(req, res){
    console.log("get attribute");
    var query = qs.stringify(req.query);
    console.log(query);
    var url = "http://localhost:8091/rest/attributes/" + req.params.id;
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

exports.createAttribute = function(req, res){
    console.log("create attribute");
    var url = "http://localhost:8091/rest/attributes";
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