var api = require('./api');
var config = require('../config');

exports.getAttributes = function(req, res){
    console.log("get attributes");
    api.getEntities(req, res, config.attributes_url);
};

exports.getAttribute = function(req, res){
    console.log("get attribute");
    api.get(req, res, config.attributes_url + req.params.id);
};

exports.createAttribute = function(req, res){
    console.log("create attribute");
    api.create(req, res, config.attributes_url);
};