
var PageModel = require('../models/page/HomeModel');
exports.Cart = function(req, res){

  var model = new PageModel();
  model.Title = "Store Demo";
  model.BodyClass = "body-bg-10";

  res.render("Cart", {layout: false, PageModel: model });
};