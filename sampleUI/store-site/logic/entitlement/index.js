var Async = require('async');
var DataProvider = require('store-data-provider').APIProvider.Entitlements;
var EmailsProvider = require('store-data-provider').APIProvider.Emails;
var Models = require('store-model').Entitlement;
var EmailsModels = require('store-model').Emails;

var DomainModels = require('../../models/domain');
var Utils = require('../../utils/utils');

var Entitlement = function () {};

Entitlement.GetEntitlements = function (data, callback) {
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var userId = cookies[process.AppConfig.CookiesName.UserId];

    var dataProvider = new DataProvider(process.AppConfig.Entitlement_API_Host, process.AppConfig.Entitlement_API_Port);

    dataProvider.GetEntitlementsByUserId(userId, function(result){
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
    var mailProvider = new EmailsProvider(process.AppConfig.Emails_API_Host, process.AppConfig.Emails_API_Port);

    dataProvider.PostEntitlement(model, function(result){
        var resultModel = new DomainModels.ResultModel();

        if(result.StatusCode == 200){
            var devModel = new EmailsModels.DevAccountModel();
            devModel.recipient = cookies[process.AppConfig.CookiesName.Username];
            devModel.properties.devname = "1234";
            devModel.properties.publishername = "1234";
            mailProvider.Send(devModel, function(result){

            });

            resultModel.status = DomainModels.ResultStatusEnum.Normal;
        }else{
            resultModel.status = DomainModels.ResultStatusEnum.APIError;
        }
        resultModel.data = result.Data;
        callback(Utils.GenerateResponseModel(resultModel));
    });
};

module.exports = Entitlement;