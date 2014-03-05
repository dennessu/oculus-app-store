var Utils = require('../helper/Utils');
var PageModel = require('../models/page/ShopModel');

module.exports = function (req, res) {

    var model = new PageModel();
    Utils.FillAuthInfoToBaseModel(req, res, model);

    model.Title = "Store Demo";
    model.BodyClass = "body-bg-3";

    res.render("Shop", {layout: "Layout", PageModel: model });

};
