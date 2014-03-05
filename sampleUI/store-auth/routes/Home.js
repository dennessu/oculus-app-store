
var C = require('../config/configuration');
var Utils = require('../helper/utils');

module.exports = function(req, res){

  res.render("index", {layout:false, title: "Store Demo", IP: req.ip});
}