var Async = require('async');
var DataProvider = require('store-data-provider').Billing;
var BillingModels = require('store-model').Billing;

var DomainModels = require('../../models/domain');
var Utils = require('../../utils/utils');

var Billing = function () {};
Billing.GetShippingInfo = function (data, callback) {
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var userId = cookies[process.AppConfig.CookiesName.UserId];
    var dataProvider = new DataProvider(process.AppConfig.Billing_API_Host, process.AppConfig.Billing_API_Port);

    dataProvider.GetShippingInfo(userId, function(result){
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

    dataProvider.GetShippingInfoById(shippingId, userId, function(result){
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
    Utils.FillObject(model, body, 2);
    model.user.id = userId;

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

module.exports = Billing;