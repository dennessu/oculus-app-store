var api = require('./api');
var config = require('../config');

exports.getPriceTiers = function(req, res){
    console.log("get price tiers");
    api.getEntities(req, res, config.price_tiers_url);
};

exports.getPriceTier = function(req, res){
    console.log("get price tiers");
    api.get(req, res, config.price_tiers_url + req.params.id);
};

exports.createPriceTier = function(req, res){
    console.log("create price tiers");
    api.create(req, res, config.price_tiers_url);
};