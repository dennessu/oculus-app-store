var api = require('./api');

exports.getOffers = function(req, res){
    console.log("get offers");
    api.getEntities(req, res, config.offers_url);
};

exports.getOffer = function(req, res){
    console.log("get offer");
    api.get(req, res, config.offers_url + req.params.id);
};

exports.updateOffer = function(req, res){
    console.log("update offer");
    api.update(req, res, config.offers_url + req.params.id);
};

exports.createOffer = function(req, res){
    console.log("create offer");
    api.create(req, res, config.offers_url);
};