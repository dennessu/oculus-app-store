var Utils = require('../helper/utils');
var PageModel = require('../models/page/shop_model');

module.exports = function (req, res) {

    var model = new PageModel();
    Utils.FillAuthInfoToBaseModel(req, res, model);

    model.Title = "Store Demo";
    model.BodyClass = "body-bg-3";

    res.render("Shop", {layout: "layout", PageModel: model });

};
