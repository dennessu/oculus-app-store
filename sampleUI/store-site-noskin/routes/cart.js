
var PageModel = require('../models/page/home_model');
exports.Cart = function(req, res){

  var model = new PageModel();
  model.Title = "Store Demo";
  model.BodyClass = "body-bg-10";

  res.render("cart", {layout: false, PageModel: model });
};