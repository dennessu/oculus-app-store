
var Async = require('async');
var DomainModel = require('../../models/domain');
var ProductModel = require('../../models/domain').Product;
var CatalogDataProvider = require('store-dataProvider').Catalog;
var CartDataProvider = require('store-dataProvider').Cart;
var OfferItemModel = require('store-model').Cart.OfferItemModel;
var C = require('../../config/configuration');

var API = {};

API.Products = function(req, res){
    var dataProvider = new CatalogDataProvider(C.Catalog_API_Host, C.Catalog_API_Port);

    var id = req.params['id'];

    if(id != undefined){

        dataProvider.GetOfferById(id, null, function(result){
            if(result.StatusCode == 200){
                var resObj = JSON.parse(result.Data);

                var model = new ProductModel();
                model.id = id;
                model.name = resObj.name;
                model.price = resObj.prices.US.amount;
                model.picture_url = "/images/p1.jpg";

                res.json({"Product": model});
                res.end();
            }else{
                // error
                res.end();
            }
        });

    }else{
        dataProvider.GetOffers(null, function(result){
            if(result.StatusCode == 200){
                var products = new Array();
                var resObj = JSON.parse(result.Data).results;

                if(resObj.length > 0){
                    for(var i = 0; i < resObj.length; ++i){
                        var product = new ProductModel();
                        product.id = resObj[i].self.id;
                        product.name = resObj[i].name;
                        product.price = resObj[i].prices.US.amount;
                        product.picture_url = "/images/P1.jpg";

                        products.push({"Product": product});
                    }
                }

                res.json({"Products": products});
                res.end();
            }else{
                // error
                res.end();
            }
        });
    }
};

API.GetCartItems = function(req, res){

    var userId = req.query["userId"];

    var dataProvider = new CartDataProvider(C.Cart_API_Host, C.Cart_API_Port);
    var offerDataProvider = new CatalogDataProvider(C.Catalog_API_Host, C.Catalog_API_Port);

    Async.waterfall([
        function(cb){
            dataProvider.GetPrimaryCart(userId, null, function(result){
                if(result.StatusCode == 302){
                    cb(null, result.Headers.location);
                }else{
                    cb("Can't get primary cart", result);
                }
            });
        },
        function(url, cb){
            dataProvider.GetCartByUrl(url, null, function(result){
                if(result.StatusCode == 200){
                    cb(null, result.Data);
                }else{
                    cb("Can't get cart by url. URL: " + url, result);
                }
            });
        },
        function(data, cb){
            var cartObj = JSON.parse(data);
            var resArr = new Array();

            offers = cartObj.offers;
            //{CartItems:{CartItem:{product_id}}}
            if(offers.length > 0){
                for(var i = 0; i < offers.length; ++i){
                    var cartItem = new DomainModel.CartItem();
                    cartItem.id = i+1;
                    cartItem.product_id = offers[i].offer.id;
                    cartItem.count = offers[i].quantity;
                    cartItem.unit_price = 0;
                    resArr[resArr.length] = {"CartItem": cartItem};
                }
            }
            cb(null, {"CartItems": resArr});
        }
    ], function(error, result){
        var resModel = new DomainModel.ResponseModel();
        if(error){
            resModel.action = DomainModel.ResponseModelActionsEnum.Error;
        }else{
            resModel.action = DomainModel.ResponseModelActionsEnum.Normal;
            resModel.data = JSON.stringify(result);
        }

        res.json(resModel);
        res.end();
    });
};

API.AddCartItem = function(req, res){

    var userId = req.body["userId"];
    var offerId = req.body["productId"];
    var quantity = req.body["count"];

    var offerItem = new OfferItemModel();
    offerItem.offer.id = offerId;
    offerItem.quantity = quantity;

    var dataProvider = new CartDataProvider(C.Cart_API_Host, C.Cart_API_Port);
    Async.waterfall([
        function(cb){
            dataProvider.GetPrimaryCart(userId, null, function(result){
                if(result.StatusCode == 302){
                    cb(null, result.Headers.location);
                }else{
                    cb("Can't get primary cart", result);
                }
            });
        },
        function(url, cb){
            dataProvider.GetCartByUrl(url, null, function(result){
                if(result.StatusCode == 200){
                    cb(null, result.Data);
                }else{
                    cb("Can't get cart by url. URL: " + url, result);
                }
            });
        },
        function(data, cb){
            var cartObj = JSON.parse(data);
            var cartId = cartObj.self.id;

            offerItem.self.id = cartId;

            var isExists = false;
            var needUpdate = true;
            for(var i = 0; i < cartObj.offers.length; ++i){
                var item = cartObj.offers[i];
                if(item.offer.id == offerItem.offer.id){
                    isExists = true;
                    break;
                }
            }

            if(!isExists){
                cartObj.offers.push(offerItem);
            }

            if(needUpdate){
                dataProvider.PutCartUpdate(userId, cartId, cartObj, function(result){
                    if(result.StatusCode == 200){
                        cb(null, result);
                    }else{
                        cb("Can't update cart. Offer: "+ JSON.stringify(offerItem), result);
                    }
                });
            }else{
                cb(null, null);
            }
        }
    ], function(error, result){
        var resModel = new DomainModel.ResponseModel();
        if(error){
            resModel.action = DomainModel.ResponseModelActionsEnum.Error;
        }else{
            resModel.action = DomainModel.ResponseModelActionsEnum.Normal;
        }

        res.json(resModel);
        res.end();
    });
};
API.RemoveCartItem = function(req, res){

    var userId = req.body["userId"];
    var offerId = req.body["productId"];

    var dataProvider = new CartDataProvider(C.Cart_API_Host, C.Cart_API_Port);
    Async.waterfall([
        function(cb){
            dataProvider.GetPrimaryCart(userId, null, function(result){
                if(result.StatusCode == 302){
                    cb(null, result.Headers.location);
                }else{
                    cb("Can't get primary cart", result);
                }
            });
        },
        function(url, cb){
            dataProvider.GetCartByUrl(url,null, function(result){
                if(result.StatusCode == 200){
                    cb(null, result.Data);
                }else{
                    cb("Can't get cart by url. URL: " + url, result);
                }
            });
        },
        function(data, cb){
            var cartObj = JSON.parse(data);
            var cartId = cartObj.self.id;

            var index = -1;
            for(var i = 0; i < cartObj.offers.length; ++i){
                var item = cartObj.offers[i];
                if(item.offer.id == offerId){
                    index = i;
                    break;
                }
            }

            if(index != -1){
                cartObj.offers.splice(index, 1);

                dataProvider.PutCartUpdate(userId, cartId, cartObj, function(result){
                    if(result.StatusCode == 200){
                        cb(null, result);
                    }else{
                        cb("Can't update cart. Offer: "+ JSON.stringify(offerItem), result);
                    }
                });
            }
        }
    ], function(error, result){
        var resModel = new DomainModel.ResponseModel();
        if(error){
            resModel.action = DomainModel.ResponseModelActionsEnum.Error;
        }else{
            resModel.action = DomainModel.ResponseModelActionsEnum.Normal;
        }

        res.json(resModel);
        res.end();
    });
};


module.exports = API;