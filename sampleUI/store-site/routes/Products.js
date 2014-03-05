
var PageModel = require('../models/page/ProductsModel');

exports.Product = function(req, res){

    var model = new PageModel();
    model.Title = "Store Demo";
    model.BodyClass = "body-bg-10";

    res.render("Product", {layout: false, PageModel: model });
};

exports.Category = function (req, res) {

  var model = new PageModel();
  model.Title = "Store Demo";
  model.BodyClass = "body-bg-2";

  res.render("products/Category", {layout: "Layout", PageModel: model });
};

exports.Games = function (req, res) {

  var model = new PageModel();
  model.Title = "Store Demo";
  model.BodyClass = "body-bg-3";

  res.render("products/Games", {layout: "Layout", PageModel: model });
};


exports.Detail = function (req, res) {

  var model = new PageModel();
  model.Title = "Store Demo";
  model.BodyClass = "body-bg-5";

  res.render("products/ProductDetail", {layout: "Layout", PageModel: model });
};