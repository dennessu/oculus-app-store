var DataProvider = require('store-data-provider').Billing;
var BillingModels = require('store-model').Billing;

var DomainModels = require('../../models/domain');
var Utils = require('../../utils/utils');

var Billing = function () {};
Billing.GetShippingInfos = function (data, callback) {
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var userId = cookies[process.AppConfig.CookiesName.UserId];
    var dataProvider = new DataProvider(process.AppConfig.Billing_API_Host, process.AppConfig.Billing_API_Port);

    dataProvider.GetShippingInfosByUserId(userId, function(result){
        var resultModel = new DomainModels.ResultModel();
       if(result.StatusCode == 200){
           resultModel.status = DomainModels.ResultStatusEnum.Normal;
       }else{
           resultModel.status = DomainModels.ResultStatusEnum.APIError;
       }
        resultModel.data = result.Data;

        callback(Utils.GenerateResponseModel(resultModel));
    });
};

Billing.GetShippingInfoById = function (data, callback) {
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var userId = cookies[process.AppConfig.CookiesName.UserId];
    var shippingId = body["shippingId"];
    var dataProvider = new DataProvider(process.AppConfig.Billing_API_Host, process.AppConfig.Billing_API_Port);

    dataProvider.GetShippingInfoById(userId, shippingId, function(result){
        var resultModel = new DomainModels.ResultModel();
        if(result.StatusCode == 200){
            resultModel.status = DomainModels.ResultStatusEnum.Normal;
        }else{
            resultModel.status = DomainModels.ResultStatusEnum.APIError;
        }
        resultModel.data = result.Data;

        callback(Utils.GenerateResponseModel(resultModel));
    });
};

Billing.PostShippingInfo = function (data, callback) {
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var userId = cookies[process.AppConfig.CookiesName.UserId];

    var model = new BillingModels.ShippingInfoModel();
    model.firstName = body["firstName"];
    model.lastName = body["lastName"];
    model.street = body["street"];
    model.city = body["city"];
    model.state = body["state"];
    model.postalCode = body["postalCode"];
    model.country = body["country"];
    model.phoneNumber = body["phoneNumber"];
    model.userId.id = userId;

    var dataProvider = new DataProvider(process.AppConfig.Billing_API_Host, process.AppConfig.Billing_API_Port);

    dataProvider.PostShippingInfo(userId, model, function(result){
        var resultModel = new DomainModels.ResultModel();

        if(result.StatusCode == 200){
            var shippingInfo = JSON.parse(result.Data);
            var shippingId = new DomainModels.SettingModel();
            resultModel.status = DomainModels.ResultStatusEnum.Normal;
            shippingId = Utils.GenerateCookieModel(process.AppConfig.CookiesName.ShippingId, shippingInfo.self.id);
            resultModel.data = result.Data;
            callback(Utils.GenerateResponseModel(resultModel, shippingId));
        }else{
            resultModel.status = DomainModels.ResultStatusEnum.APIError;
            resultModel.data = result.Data;
            callback(Utils.GenerateResponseModel(resultModel));
        }
    });
};

Billing.DeleteShippingInfo = function (data, callback) {
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var userId = cookies[process.AppConfig.CookiesName.UserId];
    var dataProvider = new DataProvider(process.AppConfig.Billing_API_Host, process.AppConfig.Billing_API_Port);

    dataProvider.DeleteShippingInfo(userId, body["shippingId"], function(result){
        var resultModel = new DomainModels.ResultModel();
        if(result.StatusCode == 200 || result.StatusCode == 204){
            resultModel.status = DomainModels.ResultStatusEnum.Normal;
        }else{
            resultModel.status = DomainModels.ResultStatusEnum.APIError;
        }
        resultModel.data = result.Data;
        callback(Utils.GenerateResponseModel(resultModel));
    });
};

module.exports = Billing;