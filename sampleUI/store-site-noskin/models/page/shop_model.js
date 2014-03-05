
var Util = require('util');
var BaseModel = require('./base_model');

var Model = function(){
  this.SelfField = "";
};

Util.inherits(Model, BaseModel);

module.exports = Model;