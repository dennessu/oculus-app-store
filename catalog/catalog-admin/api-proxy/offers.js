var rest = require('restler');
var qs = require('querystring');

exports.getOffers = function(req, res){
    console.log("get offers");
    var query = qs.stringify(req.query);
    var url = "http://localhost:8080/rest/offers";
    if (query) url = url + "?" + query;
    console.log(url);

    rest.get(url).on('complete', function(result) {
        res.send(result.results);
    });
};

exports.getOffer = function(req, res){
    console.log("get offer");
    var query = qs.stringify(req.query);
    console.log(query);
    var url = "http://localhost:8080/rest/offers/" + req.params.id;
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

exports.updateOffer = function(req, res){
    console.log("update offer");
    var url = "http://localhost:8080/rest/offers/" + req.params.id;
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