var api = require('./api');

exports.getItems = function(req, res){
    console.log("get items");
    api.getEntities(req, res, config.items_url);
};

exports.getItem = function(req, res){
    console.log("get item");
    api.get(req, res, config.items_url + req.params.id);
};

exports.updateItem = function(req, res){
    console.log("update item");
    api.update(req, res, config.items_url + req.params.id);
};

exports.createItem = function(req, res){
    console.log("create item");
    api.create(req, res, config.items_url);
};