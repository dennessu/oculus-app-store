var Async = require('async');
var IdentityProvider = require('store-data-provider').APIProvider.Identity;
var EmailsProvider = require('store-data-provider').APIProvider.Emails;
var IdentityModels = require('store-model').Identity;
var EmailsModels = require('store-model').Emails;
var DomainModels = require('../../models/domain');

module.exports = function(data, callback){
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var userModel = new IdentityModels.UserModel();
    userModel.username = body.username;
    userModel.isAnonymous = false;

    var credentialModel = new IdentityModels.UserCredentialModel();
    credentialModel.type = "PASSWORD";
    credentialModel.value = body.password;

    var emailModel = new IdentityModels.PersonalInfoModel();
    //emailModel.user.id = null;
    emailModel.type = "EMAIL";
    emailModel.value.value = body.email;

    var birthdayModel = new IdentityModels.PersonalInfoModel();
    //emailModel.user.id = null;
    emailModel.type = "DOB";
    emailModel.value.value = body.brithday;

    var dataProvider = new IdentityProvider(process.AppConfig.Identity_API_Host, process.AppConfig.Identity_API_Port);
    var mailProvider = new EmailsProvider(process.AppConfig.Emails_API_Host, process.AppConfig.Emails_API_Port);

    Async.waterfall([
        //POST user
        function(cb){
            dataProvider.PostUser(userModel, function(result){
                    if(result.StatusCode.toString()[0] == 2) {
                        var resultUser = JSON.parse(result.Data);
                        cb(null, result.self.id);
                    }else{
                        cb("Can't create an user!", result);
                    }
            });
        },
        //POST credential
        function(userId, cb){
            dataProvider.PostChangeCredentials(userId, credentialModel, function(result){
                if(result.StatusCode.toString()[0] == 2) {
                    cb(null, userId);
                }else{
                    cb("Can't post a user credential!", result);
                }
            });
        },
        //POST personal info for email
        function(userId, cb){
            emailModel.user.id = userId;

            dataProvider.PostPersonalInfo(emailModel, function(result){
                if(result.StatusCode.toString()[0] == 2) {
                    cb(null, userId);
                }else{
                    cb("Can't post a user credential!", result);
                }
            });
        },
        //POST personal info for birthday
        function(userId, cb){
            birthdayModel.user.id = userId;

            dataProvider.PostPersonalInfo(birthdayModel, function(result){
                if(result.StatusCode.toString()[0] == 2) {
                    cb(null, userId);
                }else{
                    cb("Can't post a user credential!", result);
                }
            });
        }
    ],
        function(err, result){
        var responseModel = new DomainModels.ResponseModel();
        var resultModel = new DomainModels.ResultModel();
        var settingArray = new Array();

        if(err){
            resultModel.status = DomainModels.ResultStatusEnum.APIError;
            resultModel.data = result.Data;

            responseModel.data = resultModel;
            responseModel.settings = settingArray;

            callback(responseModel);
        }

        //Send mail
        try{
            var welcomeModel = new EmailsModels.WelcomeModel();
            welcomeModel.recipient = userModel.userName;
            welcomeModel.properties.accountname = userModel.userName;
            mailProvider.Send(welcomeModel, function(result){});
        }catch(e){
            console.log("Send mail failed! Error Message: ", e);
        }
        resultModel.status = DomainModels.ResultStatusEnum.Normal;

        responseModel.data = resultModel;
        responseModel.settings = settingArray;

        callback(responseModel);
    });
};