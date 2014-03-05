
var PageModel = require('../models/page/products_model');

exports.Product = function(req, res){

    var model = new PageModel();
    model.Title = "Store Demo";
    model.BodyClass = "body-bg-10";

    res.render("product", {layout: false, PageModel: model });
};

exports.Category = function (req, res) {

  var model = new PageModel();
  model.Title = "Store Demo";
  model.BodyClass = "body-bg-2";

  res.render("products/category", {layout: "Layout", PageModel: model });
};

exports.Games = function (req, res) {

  var model = new PageModel();
  model.Title = "Store Demo";
  model.BodyClass = "body-bg-3";

  res.render("products/games", {layout: "Layout", PageModel: model });
};


exports.Detail = function (req, res) {

  var model = new PageModel();
  model.Title = "Store Demo";
  model.BodyClass = "body-bg-5";

  res.render("products/product_detail", {layout: "layout", PageModel: model });
};