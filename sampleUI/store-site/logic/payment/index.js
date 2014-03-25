var Async = require('async');
var Guid = require('guid');
var DataProvider = require('store-data-provider').Payment;
var PaymentModels = require('store-model').Payment;

var DomainModels = require('../../models/domain');
var Utils = require('../../utils/utils');

var Payment = function () {};
Payment.GetPayment = function (data, callback) {
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var userId = cookies[process.AppConfig.CookiesName.UserId];
    var dataProvider = new DataProvider(process.AppConfig.Payment_API_Host, process.AppConfig.Payment_API_Port);

    dataProvider.GetPaymentInstruments(userId, function(result){
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

Payment.PostPayment = function (data, callback) {
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var userId = cookies[process.AppConfig.CookiesName.UserId];

    var model = new PaymentModels.CreditCardModel();
    Utils.FillObject(model, body, 2);
    model.creditCardRequest.expireDate = body["expireDate"];
    model.creditCardRequest.encryptedCvmCode = body["encryptedCvmCode"];
    model.address.addressLine1 = body["addressLine1"];
    model.address.city = body["city"];
    model.address.state = body["state"];
    model.address.country = body["country"];
    model.address.postalCode = body["postalCode"];
    model.phone.number = body["phoneNumber"];
    model.trackingUuid = Guid.create();

    var dataProvider = new DataProvider(process.AppConfig.Payment_API_Host, process.AppConfig.Payment_API_Port);

    dataProvider.PostPayment(userId, model, function(result){
        var resultModel = new DomainModels.ResultModel();

        if(result.StatusCode == 200){
            var payment = JSON.parse(result.Data);
            var paymentId = new DomainModels.SettingModel();
            resultModel.status = DomainModels.ResultStatusEnum.Normal;
            paymentId = Utils.GenerateCookieModel(process.AppConfig.CookiesName.PaymentId, payment.self.id);
            resultModel.data = result.Data;
            callback(Utils.GenerateResponseModel(resultModel, paymentId));
        }else{
            resultModel.status = DomainModels.ResultStatusEnum.APIError;
            resultModel.data = result.Data;
            callback(Utils.GenerateResponseModel(resultModel));
        }
    });
};

Payment.DeletePayment = function (data, callback) {
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var userId = cookies[process.AppConfig.CookiesName.UserId];
    var paymentId = body["paymentId"];

    var dataProvider = new DataProvider(process.AppConfig.Payment_API_Host, process.AppConfig.Payment_API_Port);

    dataProvider.DeletePayment(paymentId, userId, function(result){
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

module.exports = Payment;