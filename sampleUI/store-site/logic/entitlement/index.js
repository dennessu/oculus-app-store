var Async = require('async');
var DataProvider = require('store-data-provider').Entitlement;
var Models = require('store-model').Entitlement;

var DomainModels = require('../../models/domain');
var Utils = require('../../utils/utils');

var Entitlement = function () {};

Entitlement.GetEntitlements = function (data, callback) {
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var devId = 1234;
    var devType = "DEVELOPER";
    var userId = cookies[process.AppConfig.CookiesName.UserId];

    var dataProvider = new DataProvider(process.AppConfig.Entitlement_API_Host, process.AppConfig.Entitlement_API_Port);

    dataProvider.GetEntitlements(userId, devId, devType, function(result){
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

Entitlement.PostEntitlement = function (data, callback) {
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var devId = 1234;
    var devType = "DEVELOPER";
    var userId = cookies[process.AppConfig.CookiesName.UserId];

    var model = new Models.EntitlementModel();
    model.user.id = userId;
    model.developer.id = devId;
    model.type = devType;

    var dataProvider = new DataProvider(process.AppConfig.Billing_API_Host, process.AppConfig.Billing_API_Port);

    dataProvider.PostEntitlement(userId, devId, model, function(result){
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

module.exports = Entitlement;