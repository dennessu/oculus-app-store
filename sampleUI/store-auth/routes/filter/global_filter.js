
var C = require('../../config/configuration');
var Utils = require('../../helper/utils');

module.exports = function(req, res, next){

    Utils.SaveQueryStrings(req, res, C.SaveQueryStringArray);

    res.setHeader("PID:", process.pid);
  next();
};