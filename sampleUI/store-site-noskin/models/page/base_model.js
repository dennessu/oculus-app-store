var Utils = require('../../helper/utils');
var HeaderModel = require('./warpers/header_model');

var BaseModel = function(){};

BaseModel.prototype.Title =  "";
BaseModel.prototype.BodyClass =  "";
BaseModel.prototype.Header =  Utils.GetBaseHeaderModel();

module.exports = BaseModel;
