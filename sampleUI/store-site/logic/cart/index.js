var Async = require('async');
var CartDataProvider = require('store-data-provider').Cart;
var CartModels = require('store-model').Cart;
var DomainModels = require('../../models/domain');

var Cart = function(){};
Cart.GetCart = function(data, cb){
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var dataProvider = new CartDataProvider(process.AppConfig.Cart_API_Host, process.AppConfig.Cart_API_Port);

    Async.waterfall([
        function(cb){
            dataProvider.GetPrimaryCart(cookies[process.AppConfig.CookiesName.UserId], null, function(result){
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
        }
    ], function(error, result){
        var resultModel = new DomainModels.ResultModel;
        var settingArray = new Array();
        var resModel = new DomainModels.ResponseModel();

        if(error){
            resultModel.status = DomainModels.ResultStatusEnum.APIError;
            resultModel.data = result;
        }else{
            resultModel.status = DomainModels.ResultStatusEnum.Normal;
            resultModel.data = result;

            var cardIdCookie = new DomainModels.SettingModel();
            cardIdCookie.type = process.AppConfig.SettingTypeEnum.Cookie;
            cardIdCookie.data = {name: process.AppConfig.CookiesName.CartId, value: JSON.parse(result).self.id};
        }

        resModel.data = resultModel;
        resModel.settings = settingArray;

        cb(resModel);
    });
};

Cart.MergeCartItem = function(data, cb){
    Cart.CartProcess('merge', data, cb);
};

Cart.UPdateCartItem = function(data, cb){
    Cart.CartProcess('update',data, cb);
};

Cart.AddCartItem = function(data, cb){
    Cart.CartProcess('add', data, cb);
};

Cart.RemoveCartItem = function(data, cb){
    Cart.CartProcess('remove', data, cb);
};

Cart.CartProcess = function(action, data, cb){
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var userId = cookies["user_id"];
    var cartItems =body["cart_items"];

    var resultModel = new DomainModels.ResultModel();
    var resModel = new DomainModels.ResponseModel();
    if(userId == undefined || cartItems == undefined || userId == null || cartItems == null){
        resultModel.status = DomainModels.ResultStatusEnum.Exception;
        resModel.data = resultModel;
        cb(resModel);
    }

    if(cartItems.length <= 0){
        resultModel.status = DomainModels.ResultStatusEnum.Normal;
        resModel.data = resultModel;
        cb(resModel);
    }

    var dataProvider = new CartDataProvider(process.AppConfig.Cart_API_Host, process.AppConfig.Cart_API_Port);
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

            var offerItems = new Array();
            for(var i = 0; i < cartItems.length; ++i){
                var offer = new CartModels.OfferItemModel();
                offer.self.id = cartId;
                offer.offer.id = cartItems[i].product_id;
                offer.quantity = cartItems[i].qty;

                offerItems.push(offer);
            }

            var needUpdate = false;
            for(var i = 0; i < offerItems.length; ++i){
                var currentOffer = offerItems[i];
                var offerIndex = Cart._getIndexByOffers(currentOffer.offer.id, cartObj.offers);

                switch (action){
                    case "merge":
                        if(offerIndex == -1){
                            cartObj.offers.push(currentOffer);
                        }else{
                            cartObj.offers(offerIndex).quantity == currentOffer.quantity;
                        }
                        needUpdate = true;
                        break;

                    case "update":
                        if(offerIndex == -1){
                            cartObj.offers.push(currentOffer);
                        }else{
                            cartObj.offers[offerIndex].quantity = currentOffer.quantity;
                        }
                        needUpdate = true;
                        break;

                    case "add":
                        if(offerIndex == -1){
                            cartObj.offers.push(currentOffer);
                        }else{
                            cartObj.offers[offerIndex].quantity += parseInt(currentOffer.quantity);
                        }
                        needUpdate = true;
                        break;

                    case "remove":
                        if(offerIndex == -1){
                            needUpdate = false;
                        }else{
                            needUpdate = true;
                            cartObj.offers.splice(offerIndex, 1);
                        }
                        break;
                }
            }

            if(needUpdate){
                dataProvider.PutCartUpdate(userId, cartId, cartObj, function(result){
                    if(result.StatusCode == 200){
                        cb(null, result);
                    }else{
                        cb("Can't update cart. Offer: "+ JSON.stringify(offerItems), result);
                    }
                });
            }else{
                cb(null, null);
            }
        }
    ], function(error, result){
        if(error){
            resultModel.status = DomainModels.ResultStatusEnum.APIError;
            resultModel.data = result;
        }else{
            resultModel.status = DomainModels.ResultStatusEnum.Normal;
            resultModel.data = result;
        }
        resModel.data = resultModel;

        cb(resModel);
    });
};

Cart._getIndexByOffers = function(offerId, offers){
    for(var i = 0; i < offers.length; ++i){
        var currentOfferId = offers[i].offer.id;
        if(currentOfferId == offerId){
            return i;
        }
    }
    return -1;
};

module.exports = Cart;