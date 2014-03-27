var Guid = require('guid');
var Async = require('async');
var DataProvider = require('store-data-provider');
var IdentityModels = require('store-model').Identity;
var DomainModels = require('../../models/domain');
var Utils = require('../../utils/utils');
var PasswordIntensity = require('../../utils/password_intensity');

module.exports = function (data, callback) {
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var pi = new PasswordIntensity();

    var userModel = new IdentityModels.UserModel();
    userModel.userName = Guid.raw();
    userModel.password = "password";
    //userModel.passwordStrength = pi.GetIntensity(userModel.password);
    userModel.status = "ACTIVE";

    var dataProvider = new DataProvider.Identity(process.AppConfig.Identity_API_Host, process.AppConfig.Identity_API_Port);
    var cartProvider = new DataProvider.Cart(process.AppConfig.Cart_API_Host, process.AppConfig.Cart_API_Port);

    Async.waterfall([function (cb) {
        dataProvider.PostCreateAccount(userModel, function (resultData) {
            if (resultData.StatusCode == 200) {
                var resultUser = JSON.parse(resultData.Data);
                console.log("Get Anonymous User Result:", resultUser);
                cb(null, resultUser.self.id);
            } else {
                cb("Can't create an anonymous user!", resultData);
            }
        });
    },
        function (userId, cb) {
            cartProvider.GetPrimaryCart(userId, function (result) {
                if (result.StatusCode == 302) {
                    cb(null, result.Headers.location);
                } else {
                    cb("Can't get primary cart", result);
                }
            });
        },
        function (url, cb) {
            cartProvider.GetCartByUrl(url, null, function (result) {
                if (result.StatusCode == 200) {
                    cb(null, result);
                } else {
                    cb("Can't get cart by url. URL: " + url, result);
                }
            });
        }], function (err, result) {
        var resultModel = new DomainModels.ResultModel();
        if (err) {
            resultModel.status = DomainModels.ResultStatusEnum.APIError;

            callback(Utils.GenerateResponseModel(resultModel));
        } else {

            var resultCart = JSON.parse(result.Data);
            resultModel.status = DomainModels.ResultStatusEnum.Normal;
            resultModel.data = resultCart;

            var userIdCookie = Utils.GenerateCookieModel(process.AppConfig.CookiesName.AnonymousUserId, resultCart.user.id);
            var cartIdCookie = Utils.GenerateCookieModel(process.AppConfig.CookiesName.AnonymousCartId, resultCart.self.id);
            var responseModel = Utils.GenerateResponseModel(resultModel, userIdCookie, cartIdCookie);

            console.log("Get Anonymous User Result:", responseModel);
            callback(responseModel);
        }
    });

};