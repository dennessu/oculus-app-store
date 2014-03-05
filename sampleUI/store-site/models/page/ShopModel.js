
var Util = require('util');
var BaseModel = require('./BaseModel');

var Model = function(){
  this.SelfField = "";
};

Util.inherits(Model, BaseModel);

module.exports = Model;