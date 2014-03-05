var Utils = require('../../helper/Utils');
var HeaderModel = require('./warpers/HeaderModel');

var BaseModel = function(){};

BaseModel.prototype.Title =  "";
BaseModel.prototype.BodyClass =  "";
BaseModel.prototype.Header =  Utils.GetBaseHeaderModel();

module.exports = BaseModel;
