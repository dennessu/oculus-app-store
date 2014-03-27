var IdentityProvider = require('store-data-provider').Identity;
var IdentityModels = require('store-model').Identity;
var DomainModels = require('../../models/domain');
var Utils = require('../../utils/utils');

exports.PutUser = function(data, cb){
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var userId = cookies[process.AppConfig.CookiesName.UserId];
    var dataProvider = new IdentityProvider(process.AppConfig.Identity_API_Host, process.AppConfig.Identity_API_Port);
    dataProvider.GetUserById(userId, function(resultData){
        if(resultData.StatusCode == 200){
            var user = JSON.parse(resultData.Data);
            user.password = body["password"];

            dataProvider.PutUser(userId, user, function(resultData){
                var resultModel = new DomainModels.ResultModel();
                if(resultData.StatusCode == 200){
                    resultModel.status = DomainModels.ResultStatusEnum.Normal;
                }else{
                    resultModel.status = DomainModels.ResultStatusEnum.APIError;
                }
                resultModel.data = resultData.Data;
                cb(Utils.GenerateResponseModel(resultModel));
            });
        }else{
            var resultModel = new DomainModels.ResultModel();
            resultModel.status = DomainModels.ResultStatusEnum.APIError;
            resultModel.data = resultData.Data;
            cb(Utils.GenerateResponseModel(resultModel));
        }
    });
};

exports.GetProfile = function(data, cb){
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var userId = cookies[process.AppConfig.CookiesName.UserId];
    var dataProvider = new IdentityProvider(process.AppConfig.Identity_API_Host, process.AppConfig.Identity_API_Port);

    dataProvider.GetPayinProfilesByUserId(userId, function(resultData){
        var resultModel = new DomainModels.ResultModel();
        if(resultData.StatusCode == 200){
            resultModel.status = DomainModels.ResultStatusEnum.Normal;
        }else{
            resultModel.status = DomainModels.ResultStatusEnum.APIError;
        }
        resultModel.data = resultData.Data;
        cb(Utils.GenerateResponseModel(resultModel));
    });
};

exports.PutProfile = function(data, cb){
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var userId = cookies[process.AppConfig.CookiesName.UserId];
    var dataProvider = new IdentityProvider(process.AppConfig.Identity_API_Host, process.AppConfig.Identity_API_Port);

    dataProvider.GetPayinProfilesByUserId(userId, function(resultData){
        if(resultData.StatusCode == 200){
            var profile = JSON.parse(resultData.Data);

            profile.firstName = body["firstName"];
            profile.lastName = body["lastName"];

            dataProvider.PutProfile(profile.self.id, userId, profile, function(resultData){
                var resultModel = new DomainModels.ResultModel();
                if(resultData.StatusCode == 200){
                    resultModel.status = DomainModels.ResultStatusEnum.Normal;
                }else{
                    resultModel.status = DomainModels.ResultStatusEnum.APIError;
                }
                resultModel.data = resultData.Data;
                cb(Utils.GenerateResponseModel(resultModel));
            });
        }else{
            var resultModel = new DomainModels.ResultModel();
            resultModel.status = DomainModels.ResultStatusEnum.APIError;
            resultModel.data = resultData.Data;
            cb(Utils.GenerateResponseModel(resultModel));
        }
    });
};

exports.GetOptIns = function(data, cb){
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var userId = cookies[process.AppConfig.CookiesName.UserId];
    var dataProvider = new IdentityProvider(process.AppConfig.Identity_API_Host, process.AppConfig.Identity_API_Port);

    dataProvider.GetOptIns(userId, function(resultData){
        var resultModel = new DomainModels.ResultModel();
        if(resultData.StatusCode == 200){
            resultModel.status = DomainModels.ResultStatusEnum.Normal;
        }else{
            resultModel.status = DomainModels.ResultStatusEnum.APIError;
        }
        resultModel.data = resultData.Data;
        cb(Utils.GenerateResponseModel(resultModel));
    });
};

exports.PostOptIns = function(data, cb){
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var userId = cookies[process.AppConfig.CookiesName.UserId];
    var dataProvider = new IdentityProvider(process.AppConfig.Identity_API_Host, process.AppConfig.Identity_API_Port);

    if(body["optin"] == undefined){
        var resultModel = new DomainModels.ResultModel();
        resultModel.status = DomainModels.ResultStatusEnum.Normal;
        cb(Utils.GenerateResponseModel(resultModel));
        return;
    }

    var model = new IdentityModels.OptInModel();
    model.user.id = userId;
    model.type = body["optin"];

    dataProvider.PostOptIns(userId, model, function(resultData){
        var resultModel = new DomainModels.ResultModel();
        if(resultData.StatusCode == 200){
            resultModel.status = DomainModels.ResultStatusEnum.Normal;
        }else{
            resultModel.status = DomainModels.ResultStatusEnum.APIError;
        }
        resultModel.data = resultData.Data;
        cb(Utils.GenerateResponseModel(resultModel));
    });
};