var IdentityProvider = require('store-data-provider').Identity;
var EmailsProvider = require('store-data-provider').Emails;
var IdentityModels = require('store-model').Identity;
var EmailsModels = require('store-model').Emails;
var DomainModels = require('../../models/domain');
var PasswordIntensity = require('../../utils/password_intensity');

module.exports = function(data, cb){
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var pi = new PasswordIntensity();

    var userModel = new IdentityModels.UserModel();
    userModel.userName = body.email;
    userModel.password = body.password;
    //userModel.passwordStrength = pi.GetIntensity(body.password);
    userModel.status = "ACTIVE";

    var profileModel = new IdentityModels.ProfileModel();
    profileModel.user = null;
    profileModel.type = "PAYIN";
    profileModel.region = "en_" + body.country;
    profileModel.firstName = "";
    profileModel.middleName = "";
    profileModel.lastName = "";
    profileModel.dateOfBirth = body.brithday;
    profileModel.locale = "en_" + body.country;

    var dataProvider = new IdentityProvider(process.AppConfig.Identity_API_Host, process.AppConfig.Identity_API_Port);
    var mailProvider = new EmailsProvider(process.AppConfig.Emails_API_Host, process.AppConfig.Emails_API_Port);

    dataProvider.PostUser(userModel, function(resultData){
        var responseModel = new DomainModels.ResponseModel();
        var resultModel = new DomainModels.ResultModel();
        var settingArray = new Array();

        if(resultData.StatusCode == 200){
            var resultUser = JSON.parse(resultData.Data);
            profileModel.user = resultUser.self;

            dataProvider.PostProfile(profileModel.user.id, profileModel, function(result){
                if (result.StatusCode == 200) {

                    var welcomeModel = new EmailsModels.WelcomeModel();
                    welcomeModel.recipient = userModel.userName;
                    welcomeModel.properties.accountname = userModel.userName;
                    mailProvider.Send(welcomeModel, function(result){

                    });

                    if (process.AppConfig.Feature.Captcha) { // can't ship captcha
                        resultModel.status = DomainModels.ResultStatusEnum.Normal;
                    }else if(process.AppConfig.Feature.TFA){
                        resultModel.status = DomainModels.ResultStatusEnum.Normal;
                    } else { // need back and ship captcha
                        var redirectUrl = cookies[process.AppConfig.QueryStrings.RedirectUrl];

                        var redirectModel = new DomainModels.RedirectModel;
                        redirectModel.target = DomainModels.RedirectTargetsEnum.Self;

                        if (typeof(redirectUrl) != "undefined" && redirectUrl != null && redirectUrl != "") {
                            redirectModel.url = redirectUrl;
                        } else {
                            redirectModel.url = "/my";
                        }

                        resultModel.status = DomainModels.ResultStatusEnum.Redirect;
                        resultModel.data = redirectModel;
                    }
                }else{ // Error
                    resultModel.status = DomainModels.ResultStatusEnum.APIError;
                    resultModel.data = resultData.Data;
                }

                var responseModel = new DomainModels.ResponseModel();
                responseModel.data = resultModel;
                responseModel.settings = settingArray;

                console.log("Register Result:", responseModel);
                cb(responseModel);
            });
        }else{
            resultModel.status = DomainModels.ResultStatusEnum.APIError;
            resultModel.data = resultData.Data;

            var responseModel = new DomainModels.ResponseModel();
            responseModel.data = resultModel;
            responseModel.settings = settingArray;

            console.log("Register Result:", responseModel);
            cb(responseModel);
        }
    });
};