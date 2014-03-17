var IdentityProvider = require('store-data-provider').Identity;
var DomainModels = require('../../models/domain');

module.exports = function(data, cookies, query, cb){

    var dataProvider = new IdentityProvider(process.AppConfig.Identity_API_Host, process.AppConfig.Identity_API_Port);

    dataProvider.PostAuthenticate(data, function(resultData){
        var resultModel = new DomainModels.ResultModel();
        var settingArr = new Array();

        if (resultData.StatusCode == 200) {

            if (process.AppConfig.EnabledCaptcha) { // can't ship captcha
                resultModel.status = DomainModels.ResultStatusEnum.Normal;
                resultModel.data = "";
            } else { // need back and ship captcha
                var redirectUrl = cookies[process.AppConfig.QueryStrings.RedirectUrl];

                var redirectModel = new DomainModels.RedirectModel;
                redirectModel.target = DomainModels.RedirectTargetsEnum.Self;

                if (typeof(redirectUrl) != "undefined" && redirectUrl != null && redirectUrl != "") {
                    redirectModel.url = "/back";
                } else {
                    redirectModel.url = "/my";
                }

                resultModel.status = DomainModels.ResultStatusEnum.Redirect;
                resultModel.data = redirectModel;

                // set access_token to cookie
                var resObj = JSON.parse(resultData.Data);
                var accessTokenCookie = new DomainModels.SettingModel();
                accessTokenCookie.type = DomainModels.SettingTypeEnum.Cookie;
                accessTokenCookie.data = {name: process.AppConfig.SessionKeys.AccessToken, value: resObj[process.AppConfig.FieldNames.AccessToken] };
                var idTokenCookie = new DomainModels.SettingModel();
                idTokenCookie.type = DomainModels.SettingTypeEnum.Cookie;
                idTokenCookie.data = {name: process.AppConfig.SessionKeys.IdToken, value: resObj[process.AppConfig.FieldNames.IdToken] };

                settingArr.push(accessTokenCookie);
                settingArr.push(idTokenCookie);
            }
        }else if(resultData.StatusCode == 302){ // need redirect, only user login

            var redirectModel = new DomainModels.RedirectModel();
            redirectModel.target = DomainModels.RedirectTargetsEnum.Self;
            redirectModel.url = resultData.Headers.location;

            resultModel.status = DomainModels.ResultStatusEnum.Redirect;
            resultModel.data = redirectModel;

            // set access_token to cookie
            var accessTokenCookie = new DomainModels.SettingModel();
            accessTokenCookie.type = DomainModels.SettingTypeEnum.Cookie;
            accessTokenCookie.data = {name: process.AppConfig.SessionKeys.AccessToken, value: "test" };
            settingArr.push(accessTokenCookie);
        }else{ // Error
            resultModel.action = DomainModels.ResultStatusEnum.Error;
            resultModel.data = JSON.parse(resultData.Data);
        }
        var responseModel = new DomainModels.ResponseModel();
        responseModel.data = resultModel;
        responseModel.settings = settingArr;

        console.log("Login Result:", responseModel);
        cb(responseModel);
    });
};