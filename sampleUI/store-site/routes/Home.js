var Utils = require('../helper/Utils');
var C = require('../config/configuration');
var PageModel = require('../models/page/HomeModel');
var CatalogDataProvider = require('store-dataProvider').Catalog;
var ProductModel = require('../models/domain').Product;

module.exports = function (req, res) {

    var model = new PageModel();
    Utils.FillAuthInfoToBaseModel(req, res, model);

    model.Title = "Store Demo";
    model.BodyClass = "body-bg-1";

    var products = new Array();

    var catalog = new CatalogDataProvider(C.Catalog_API_Host, C.Catalog_API_Port);
    catalog.GetOffers(null, function (result) {
        if (result.StatusCode == 200) {
            if (typeof(result.Data) != "undefined" && result.Data != null) {
                var resObj = JSON.parse(result.Data).results;

                if(resObj.length > 0){
                    for(var i = 0; i < resObj.length; ++i){
                        var product = new ProductModel();
                        product.id = resObj[i].self.id;
                        product.name = resObj[i].name;
                        product.price = resObj[i].prices.US.amount;
                        product.picture_url = "/images/P1.jpg";

                        products.push(product);
                    }
                }
            } else {
                console.log("Get token info 200, but user id is undefined, Result: ", result);
            }

            res.render("index", {layout: "Layout", PageModel: model, Products: products });
        } else {
            console.log("Get token info 200, but data is NULL, Result: ", result);
            res.render("index", {layout: "Layout", PageModel: model, Products: products });
        }
    });
}