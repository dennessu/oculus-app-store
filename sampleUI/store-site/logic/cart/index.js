var Async = require('async');
var Guid = require('guid');
var CartDataProvider = require('store-data-provider').Cart;
var OrderDataProvider = require('store-data-provider').Order;

var CartModels = require('store-model').Cart;
var OrderModels = require('store-model').Order;
var DomainModels = require('../../models/domain');
var Utils = require('../../utils/utils');

var Cart = function () {};

Cart.GetCart = function (data, callback) {
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var userId = null;
    var cardId = null;
    var cartIdCookieName = null;
    if (Utils.IsAuthenticated(cookies)) {
        userId = cookies[process.AppConfig.CookiesName.UserId];
        cardId = cookies[process.AppConfig.CookiesName.CartId];
        cartIdCookieName = process.AppConfig.CookiesName.CartId;
    } else {
        userId = cookies[process.AppConfig.CookiesName.AnonymousUserId];
        cardId = cookies[process.AppConfig.CookiesName.AnonymousCartId];
        cartIdCookieName = process.AppConfig.CookiesName.AnonymousCartId;
    }

    var dataProvider = new CartDataProvider(process.AppConfig.Cart_API_Host, process.AppConfig.Cart_API_Port);
    if (!Utils.IsEmpty(cardId)) {
        dataProvider.GetCartById(userId, cardId, function (result) {
            var resultModel = new DomainModels.ResultModel;
            if (result.StatusCode == 200) {
                resultModel.status = DomainModels.ResultStatusEnum.Normal;
            } else {
                resultModel.status = DomainModels.ResultStatusEnum.APIError;
            }
            resultModel.data = result.Data;

            callback(Utils.GenerateResponseModel(resultModel));
        });
    } else {
        Async.waterfall([
            function (cb) {
                dataProvider.GetPrimaryCart(userId, function (result) {
                    if (result.StatusCode == 302) {
                        cb(null, result.Headers.location);
                    } else {
                        cb("Can't get primary cart", result);
                    }
                });
            },
            function (url, cb) {
                dataProvider.GetCartByUrl(url, null, function (result) {
                    if (result.StatusCode == 200) {
                        cb(null, result.Data);
                    } else {
                        cb("Can't get cart by url. URL: " + url, result);
                    }
                });
            }
        ], function (error, result) {
            var resultModel = new DomainModels.ResultModel;
            var responseModel;

            if (error) {
                resultModel.status = DomainModels.ResultStatusEnum.APIError;
                resultModel.data = result;

                responseModel = Utils.GenerateResponseModel(resultModel);
            } else {
                resultModel.status = DomainModels.ResultStatusEnum.Normal;
                resultModel.data = result;

                responseModel = Utils.GenerateResponseModel(resultModel, Utils.GenerateCookieModel(cartIdCookieName, JSON.parse(result).self.id));
            }

            console.log("Get Cart Response: ", responseModel);
            callback(responseModel);
        });
    }
};

Cart.MergeCartItem = function (data, callback) {
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var userId = cookies[process.AppConfig.CookiesName.UserId];
    var cartId = cookies[process.AppConfig.CookiesName.CartId];
    var anonymousUserId = cookies[process.AppConfig.CookiesName.AnonymousUserId];
    var anonymousCardId = cookies[process.AppConfig.CookiesName.AnonymousCartId];

    if(Utils.IsEmpty(userId) || Utils.IsEmpty(anonymousUserId) || Utils.IsEmpty(anonymousCardId)){
        var resultModel = new DomainModels.ResultModel;
        resultModel.status = DomainModels.ResultStatusEnum.Normal;

        console.log("[MergeCart] No Cart need merge");
        callback(Utils.GenerateResponseModel(resultModel));
    }

    var cartModel = new CartModels.CartModel();
    cartModel.self.id = anonymousCardId;
    cartModel.user.id = anonymousUserId;

    var dataProvider = new CartDataProvider(process.AppConfig.Cart_API_Host, process.AppConfig.Cart_API_Port);

    if(!Utils.IsEmpty(cartId)){
        dataProvider.PostMerge(userId, cartId, cartModel, function (result) {
            var resultModel = new DomainModels.ResultModel;
            if (result.StatusCode == 200) {
                console.log("[MergeCart] Success");

                resultModel.status = DomainModels.ResultStatusEnum.Normal;
            } else {
                console.log("[MergeCart] Failed");

                resultModel.status = DomainModels.ResultStatusEnum.APIError;
            }
            resultModel.data = result.Data;

            console.log("[MergeCart] Response: ", resultModel);
            callback(Utils.GenerateResponseModel(resultModel));
        });
    }else{
        Async.waterfall([
            function (cb) {
                dataProvider.GetPrimaryCart(userId, function (result) {
                    if (result.StatusCode == 302) {
                        cb(null, result.Headers.location);
                    } else {
                        cb("Can't get primary cart", result);
                    }
                });
            },
            function (url, cb) {
                dataProvider.GetCartByUrl(url, null, function (result) {
                    if (result.StatusCode == 200) {
                        cb(null, result.Data);
                    } else {
                        cb("Can't get cart by url. URL: " + url, result);
                    }
                });
            }
        ], function (error, result) {
            var resultModel = new DomainModels.ResultModel;
            var responseModel;

            if (error) {
                resultModel.status = DomainModels.ResultStatusEnum.APIError;
                resultModel.data = result;

                responseModel = Utils.GenerateResponseModel(resultModel);
                console.log("[MergeCart] Response: ", responseModel);
                callback(responseModel);
            } else {
                var resultObj = JSON.parse(result);
                cartId = resultObj.self.id;

                dataProvider.PostMerge(userId, cartId, cartModel, function (result) {
                    var resultModel = new DomainModels.ResultModel;
                    if (result.StatusCode == 200) {
                        console.log("[MergeCart] Success");

                        resultModel.status = DomainModels.ResultStatusEnum.Normal;
                    } else {
                        console.log("[MergeCart] Failed");

                        resultModel.status = DomainModels.ResultStatusEnum.APIError;
                    }
                    resultModel.data = result.Data;

                    console.log("[MergeCart] Response: ", resultModel);
                    callback(Utils.GenerateResponseModel(resultModel));
                });
            }
        });
    }
};

Cart.UPdateCartItem = function (data, cb) {
    Cart.CartProcess('update', data, cb);
};

Cart.AddCartItem = function (data, cb) {
    Cart.CartProcess('add', data, cb);
};

Cart.RemoveCartItem = function (data, cb) {
    Cart.CartProcess('remove', data, cb);
};

Cart.CartProcess = function (action, data, callback) {
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var userId = null;
    var cardId = null;
    if (Utils.IsAuthenticated(cookies)) {
        userId = cookies[process.AppConfig.CookiesName.UserId];
        cardId = cookies[process.AppConfig.CookiesName.CartId];
    } else {
        userId = cookies[process.AppConfig.CookiesName.AnonymousUserId];
        cardId = cookies[process.AppConfig.CookiesName.AnonymousCartId];
    }
    var cartItems = body["cart_items"];

    var resultModel = new DomainModels.ResultModel();
    var resModel = new DomainModels.ResponseModel();
    if (userId == undefined || cartItems == undefined || userId == null || cartItems == null) {
        resultModel.status = DomainModels.ResultStatusEnum.Exception;
        resModel.data = resultModel;
        callback(resModel);
    }

    if (cartItems.length <= 0) {
        resultModel.status = DomainModels.ResultStatusEnum.Normal;
        resModel.data = resultModel;
        callback(resModel);
    }

    var dataProvider = new CartDataProvider(process.AppConfig.Cart_API_Host, process.AppConfig.Cart_API_Port);
    Async.waterfall([
        function (cb) {
            dataProvider.GetPrimaryCart(userId, function (result) {
                if (result.StatusCode == 302) {
                    cb(null, result.Headers.location);
                } else {
                    cb("Can't get primary cart", result);
                }
            });
        },
        function (url, cb) {
            dataProvider.GetCartByUrl(url, null, function (result) {
                if (result.StatusCode == 200) {
                    cb(null, result.Data);
                } else {
                    cb("Can't get cart by url. URL: " + url, result);
                }
            });
        },
        function (data, cb) {
            var cartObj = JSON.parse(data);
            var cartId = cartObj.self.id;

            var offerItems = new Array();
            for (var i = 0; i < cartItems.length; ++i) {
                var offer = new CartModels.OfferItemModel();
                offer.offer.id = cartItems[i].product_id;
                offer.quantity = cartItems[i].qty;
                offer.selected = cartItems[i].selected;

                offerItems.push(offer);
            }

            var needUpdate = false;
            for (var i = 0; i < offerItems.length; ++i) {
                var currentOffer = offerItems[i];
                var offerIndex = Cart._getIndexByOffers(currentOffer.offer.id, cartObj.offers);

                switch (action) {
                    case "merge":
                        if (offerIndex == -1) {
                            cartObj.offers.push(currentOffer);
                        } else {
                            cartObj.offers(offerIndex).quantity == currentOffer.quantity;
                        }
                        needUpdate = true;
                        break;

                    case "update":
                        if (offerIndex == -1) {
                            cartObj.offers.push(currentOffer);
                        } else {
                            cartObj.offers[offerIndex].quantity = currentOffer.quantity;
                        }
                        needUpdate = true;
                        break;

                    case "add":
                        if (offerIndex == -1) {
                            cartObj.offers.push(currentOffer);
                        } else {
                            cartObj.offers[offerIndex].quantity += parseInt(currentOffer.quantity);
                        }
                        needUpdate = true;
                        break;

                    case "remove":
                        if (offerIndex == -1) {
                            needUpdate = false;
                        } else {
                            needUpdate = true;
                            cartObj.offers.splice(offerIndex, 1);
                        }
                        break;
                }
            }

            if (needUpdate) {
                dataProvider.PutCartUpdate(userId, cartId, cartObj, function (result) {
                    if (result.StatusCode == 200) {
                        cb(null, result);
                    } else {
                        cb("Can't update cart. Offer: " + JSON.stringify(offerItems), result);
                    }
                });
            } else {
                cb(null, null);
            }
        }
    ], function (error, result) {
        if (error) {
            resultModel.status = DomainModels.ResultStatusEnum.APIError;
            resultModel.data = result;
        } else {
            resultModel.status = DomainModels.ResultStatusEnum.Normal;
            resultModel.data = result;
        }
        resModel.data = resultModel;

        callback(resModel);
    });
};

Cart.GetOrders = function(data, callback){
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var userId = cookies[process.AppConfig.CookiesName.UserId];

    var orderProvider = new OrderDataProvider(process.AppConfig.Order_API_Host, process.AppConfig.Order_API_Port);
    orderProvider.GetOrdersByUserId(userId, function(resultData){
        var resultModel = new DomainModels.ResultModel;
        if(resultData.StatusCode == 200){
            resultModel.status = DomainModels.ResultStatusEnum.Normal;
        }else{
            resultModel.status = DomainModels.ResultStatusEnum.APIError;
        }
        resultModel.data = resultData.Data;

        callback(Utils.GenerateResponseModel(resultModel));
    });
};

Cart.GetOrderById = function(data, callback){
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var userId = cookies[process.AppConfig.CookiesName.UserId];
    var orderId = cookies[process.AppConfig.CookiesName.OrderId];

    var orderProvider = new OrderDataProvider(process.AppConfig.Order_API_Host, process.AppConfig.Order_API_Port);
    orderProvider.GetOrderById(orderId, function(resultData){

        var resultModel = new DomainModels.ResultModel;
        if(resultData.StatusCode == 200){
            resultModel.status = DomainModels.ResultStatusEnum.Normal;
        }else{
            resultModel.status = DomainModels.ResultStatusEnum.APIError;
        }
        resultModel.data = resultData.Data;

        callback(Utils.GenerateResponseModel(resultModel));
    });
};

Cart.PostOrder = function(data, callback){
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var userId = null;
    var cardId = null;
    var cartIdCookieName = null;
    if (Utils.IsAuthenticated(cookies)) {
        userId = cookies[process.AppConfig.CookiesName.UserId];
        cardId = cookies[process.AppConfig.CookiesName.CartId];
    } else {
        userId = cookies[process.AppConfig.CookiesName.AnonymousUserId];
        cardId = cookies[process.AppConfig.CookiesName.AnonymousCartId];
    }
    var dataProvider = new CartDataProvider(process.AppConfig.Cart_API_Host, process.AppConfig.Cart_API_Port);

    Async.waterfall([
        function (cb) {
            if (Utils.IsEmpty(cardId)) {
            dataProvider.GetPrimaryCart(userId, function (result) {
                if (result.StatusCode == 302) {
                    cb(null, result.Headers.location);
                } else {
                    cb("Can't get primary cart", result);
                }
            });
            }else{
                cb(null, null);
            }
        },
        // Get cart by url
        function (url, cb) {
            if (Utils.IsEmpty(cardId)) {
            dataProvider.GetCartByUrl(url, null, function (result) {
                if (result.StatusCode == 200) {
                    cb(null, result.Data);
                } else {
                    cb("Can't get cart by url. URL: " + url, result);
                }
            });
            }else{
                cb(null, null);
            }
        },
        // Get cart by id
        function(result, cb){
            if (!Utils.IsEmpty(cardId)) {
                dataProvider.GetCartById(userId, cardId, function (resultData) {
                    if (resultData.StatusCode == 200) {
                        cb(null, resultData.Data);
                    } else {
                        cb("Can't get cart by id. Cart Id: " + cardId, resultData);
                    }
                });
            }else{
                cb(null, result);
            }
        },
        // POST Order
        function(result, cb){
            var cartObj = JSON.parse(result);
            var orderModel = new OrderModels.OrderModel();
            orderModel.user.id = userId;
            orderModel.trackingUuid = Guid.create();
            orderModel.type = "PAY_IN";
            orderModel.country = "US";
            orderModel.currency = "USD";
            orderModel.tentative = true;
            orderModel.shippingMethodId = null;
            orderModel.shippingAddressId = null;
            orderModel.paymentInstruments = null;
            orderModel.orderItems = new Array();

            var indexArray = new Array();
            for(var i = 0; i < cartObj.offers.length; ++i){
                var item = cartObj.offers[i];
                if(item.selected == true){
                    var orderItem = new OrderModels.OrderItemModel();
                    orderItem.offer = item.offer;
                    orderItem.quantity = item.quantity;
                    orderModel.orderItems.push(orderItem);
                    indexArray.push(i);
                }
            }

            var orderProvider = new OrderDataProvider(process.AppConfig.Order_API_Host, process.AppConfig.Order_API_Port);
            orderProvider.PostOrder(orderModel, function(resultData){
                if(resultData.StatusCode == 200){
                    cb(null, resultData.Data, result, indexArray)
                }else{
                    cb("Can't Post a Order, Error:", resultData);
                }
            });
        },
        // Update Cart
        function(orderResult, result, indexArray, cb){
            var cartObj = JSON.parse(result);
            for(var i = 0; i < indexArray.length; ++i){
                cartObj.offers.splice(indexArray[i], 1);
            }

            dataProvider.PutCartUpdate(userId, cardId, cartObj, function (result) {
                if (result.StatusCode == 200) {
                    cb(null, orderResult);
                } else {
                    cb("Can't update cart. Error: ", result.Data);
                }
            });

        }
    ], function (error, result) {
        var resultModel = new DomainModels.ResultModel;
        var responseModel;

        if (error) {
            resultModel.status = DomainModels.ResultStatusEnum.APIError;
            resultModel.data = result;

            responseModel = Utils.GenerateResponseModel(resultModel);
        } else {
            resultModel.status = DomainModels.ResultStatusEnum.Normal;
            resultModel.data = result;

            responseModel = Utils.GenerateResponseModel(resultModel);
        }

        console.log("Post Order Response: ", responseModel);
        callback(responseModel);
    });

};

Cart.PutOrder = function(data, callback){
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var userId = cookies[process.AppConfig.CookiesName.UserId];
    var orderId = cookies[process.AppConfig.CookiesName.OrderId];
    var payment = {
        id: cookies[process.AppConfig.CookiesName.PaymentId]
    };
    var shippingAddressId = cookies[process.AppConfig.CookiesName.ShippingId];
    var shippingMethodId = cookies[process.AppConfig.CookiesName.ShippingMethodId];

    var orderProvider = new OrderDataProvider(process.AppConfig.Order_API_Host, process.AppConfig.Order_API_Port);

    Async.waterfall([
        function (cb) {
            orderProvider.GetOrderById(orderId, function(resultData){
                if(resultData.StatusCode == 200){
                    cb(null, resultData.Data);
                }else{
                    cb("Can't get order by id, OrderId:" + orderId, resultData.Data);
                }
            });
        },
        // Get cart by url
        function (result, cb) {
           var order = JSON.parse(result);
            order.shippingMethodId = shippingMethodId;
            order.shippingAddressId = shippingAddressId;
            order.paymentInstruments.push(payment);
            order.trackingUuid = Guid.create();

            orderProvider.PutOrder(orderId, order, function(resultData){
               if(resultData.StatusCode == 200){
                   cb(null, JSON.stringify(order));
               }else{
                   cb("Can't update order", resultData.Data);
               }
            });
        }
    ], function (error, result) {
        var resultModel = new DomainModels.ResultModel;
        var responseModel;

        if (error) {
            resultModel.status = DomainModels.ResultStatusEnum.APIError;
            resultModel.data = result;

            responseModel = Utils.GenerateResponseModel(resultModel);
        } else {
            resultModel.status = DomainModels.ResultStatusEnum.Normal;
            resultModel.data = result;

            responseModel = Utils.GenerateResponseModel(resultModel);
        }

        console.log("Post Order Response: ", responseModel);
        callback(responseModel);
    });

};

Cart.PurchaseOrder = function(data, callback){
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var orderId = cookies[process.AppConfig.CookiesName.OrderId];
    var dataProvider = new OrderDataProvider(process.AppConfig.Order_API_Host, process.AppConfig.Order_API_Port);

    var resultModel = new DomainModels.ResultModel();
    dataProvider.GetOrderById(orderId, function(resultData){
        if(resultData.StatusCode == 200){
            var order = JSON.parse(resultData.Data);

            order.trackingUuid = Guid.create();
            order.tentative = false;

            dataProvider.PutOrder(orderId, order, function(result){
               if(result.StatusCode == 200){
                   resultModel.status == DomainModels.ResultStatusEnum.Normal;
               } else{
                   resultModel.status == DomainModels.ResultStatusEnum.APIError;
               }
               resultModel.data = result.Data;

                callback(Utils.GenerateResponseModel(resultModel));
            });
        }else{
            resultModel.status == DomainModels.ResultStatusEnum.APIError;
            resultModel.data = resultData.Data;
            callback(Utils.GenerateResponseModel(resultModel));
        }
    });
};


Cart._getIndexByOffers = function (offerId, offers) {
    for (var i = 0; i < offers.length; ++i) {
        var currentOfferId = offers[i].offer.id;
        if (currentOfferId == offerId) {
            return i;
        }
    }
    return -1;
};

module.exports = Cart;