var Guid = require('guid');
var IdentityProvider = require('store-data-provider').Identity;
var IdentityModels = require('store-model').Identity;
var DomainModels = require('../../models/domain');
var PasswordIntensity = require('../../utils/password_intensity');

module.exports = function(data, cb){
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var pi = new PasswordIntensity();

    var userModel = new IdentityModels.UserModel();
    userModel.userName = Guid.raw();
    userModel.password = "password";
    userModel.passwordStrength = pi.GetIntensity(userModel.password);
    userModel.status = "ACTIVE";

    var dataProvider = new IdentityProvider(process.AppConfig.Identity_API_Host, process.AppConfig.Identity_API_Port);

    dataProvider.PostCreateAccount(userModel, function(resultData){
        var responseModel = new DomainModels.ResponseModel();
        var resultModel = new DomainModels.ResultModel();
        var settingArray = new Array();

        if(resultData.StatusCode == 200){
            var resultUser = JSON.parse(resultData.Data);
            resultModel.status = DomainModels.ResultStatusEnum.Normal;
            resultModel.data = resultUser;

            var userIdCookie = new DomainModels.SettingModel();
            userIdCookie.type = process.AppConfig.SettingTypeEnum.Cookie;
            userIdCookie.data = {name: process.AppConfig.CookiesName.UserId, value: resultUser.self.id};
            settingArray.push(userIdCookie);

            var responseModel = new DomainModels.ResponseModel();
            responseModel.data = resultModel;
            responseModel.settings = settingArray;

            console.log("Get Anonymous User Result:", responseModel);
            cb(responseModel);
        }else{
            resultModel.status = DomainModels.ResultStatusEnum.APIError;
            resultModel.data = resultData.Data;

            var responseModel = new DomainModels.ResponseModel();
            responseModel.data = resultModel;
            responseModel.settings = settingArray;

            console.log("Get Anonymous User Result:", responseModel);
            cb(responseModel);
        }
    });
};