
var UserModel = require('store-model').Identity.UserModel;
var ProfileModel = require('store-model').Identity.ProfileModel;
var AuthModel = require('store-model').Identity.AuthenticateModel;
var IdentityProvider = require('store-data-provider').Identity;

var C = require('../../config/configuration');
var PasswordIntensity = require('../../helper/password_intensity');
var DomainModel = require('../../models');

exports.Login = function(req, res){

  var model = new AuthModel();

  var conversationId = req.cookies[C.QueryStringConstants.ConversationId];
  if(typeof(conversationId) != "undefined" && conversationId != null){
    model.cid = conversationId;
  }else{
    model.cid = null;
  }

  model.event = req.cookies[C.QueryStringConstants.Event];
  model.username = req.body["email"];
  model.password = req.body["password"];

  var dataProvider = new IdentityProvider(C.Identity_API_Host, C.Identity_API_Port);

  dataProvider.PostAuthenticate(model, function(resultModel){
    var resModel = new DomainModel.ResponseModel();

    if (resultModel.StatusCode == 200) {

      if (C.EnabledCaptcha) { // can't ship captcha
        resModel.action = DomainModel.ResponseModelActionsEnum.Normal;
        resModel.data = "";
      } else { // need back and ship captcha
        var redirectUrl = req.cookies[C.QueryStringConstants.RedirectUrl];

        var redirectModel = new DomainModel.RedirectModel();
        redirectModel.target = DomainModel.RedirectModelTargetsEnum.Self;

        if (typeof(redirectUrl) != "undefined" && redirectUrl != null && redirectUrl != "") {
          redirectModel.url = "/SiteAuth";
        } else {
          redirectModel.url = "/My";
        }

        resModel.action = DomainModel.ResponseModelActionsEnum.Redirect;
        resModel.data = JSON.stringify(redirectModel);

        // set access_token to cookie
        var resObj = JSON.parse(resultModel.Data);
        res.cookie(C.CookiesKeyConstants.AccessToken, resObj[C.APIFileds.AccessToken], { maxAge: C.CookiesTimeout, domain: '', path: '/', secure: false });
        res.cookie(C.CookiesKeyConstants.IdToken, resObj[C.APIFileds.IdToken], { maxAge: C.CookiesTimeout, domain: '', path: '/', secure: false });
      }
    }else if(resultModel.StatusCode == 302){ // need redirect, only user login

      var redirectModel = new DomainModel.RedirectModel();
      redirectModel.target = DomainModel.RedirectModelTargetsEnum.Self;
      redirectModel.url = resultModel.Headers.location;

      resModel.action = DomainModel.ResponseModelActionsEnum.Redirect;
      resModel.data = JSON.stringify(redirectModel);

      // set access_token to cookie
      res.cookie(C.CookiesKeyConstants.AccessToken, "TestAccessToken", { maxAge: C.CookiesTimeout, domain: '', path: '/', secure: false });

    }else{ // Error
      res.statusCode = 422;
      resModel.action = DomainModel.ResponseModelActionsEnum.Error;
      resModel.data = resultModel.Data;
    }
    console.log("Login Response Model:", resModel);

    res.write(JSON.stringify(resModel));
    res.end();
  });
};

exports.Register = function(req, res){

    var pi = new PasswordIntensity();

  var userModel = new UserModel();
  userModel.userName = req.body["email"];
  userModel.password = req.body["password"];
  userModel.passwordStrength = pi.GetIntensity(req.body["password"]);
  userModel.status = "ACTIVE";

  var birthday = new Date(parseInt(req.body["year"]), parseInt(req.body["month"]) - 1, parseInt(req.body["day"]) + 1);
  var profileModel = new ProfileModel();
  profileModel.user = null;
  profileModel.type = "PAYIN";
  profileModel.region = "en_US";
  profileModel.firstName = req.body["firstname"];
  profileModel.middleName = "";
  profileModel.lastName = req.body["lastname"];
  profileModel.dateOfBirth = birthday;
  profileModel.locale = "en_US";

  var dataProvider = new IdentityProvider(C.Identity_API_Host, C.Identity_API_Port);

  dataProvider.PostCreateAccount(userModel, function(resultModel){
    var model = new DomainModel.ResponseModel();

      if(resultModel.StatusCode == 200){
        var resultUser = JSON.parse(resultModel.Data);

        profileModel.user = resultUser.self;

        dataProvider.PostCreateProfiles(profileModel.user.id, profileModel, function(result){
          var model = new DomainModel.ResponseModel();

          if (result.StatusCode == 200) {

            if (C.EnabledCaptcha) { // can't ship captcha
              model.action = DomainModel.ResponseModelActionsEnum.Normal;
              model.data = "";
            } else { // need back and ship captcha
              var redirectUrl = req.cookies[C.QueryStringConstants.RedirectUrl];

              var redirectModel = new DomainModel.RedirectModel();
              redirectModel.target = DomainModel.RedirectModelTargetsEnum.Self;

              if (typeof(redirectUrl) != "undefined" && redirectUrl != null && redirectUrl != "") {
                redirectModel.url = "/SiteAuth";
              } else {
                redirectModel.url = "/My";
              }

              model.action = DomainModel.ResponseModelActionsEnum.Redirect;
              model.data = JSON.stringify(redirectModel);
            }

            // set access_token to cookie
            res.cookie(C.CookiesKeyConstants.AccessToken, "TestAccessToken", { maxAge: C.CookiesTimeout, domain: '', path: '/', secure: false });

          }else if(result.StatusCode == 302){ // need redirect, only user login

            var redirectModel = new DomainModel.RedirectModel();
            redirectModel.target = DomainModel.RedirectModelTargetsEnum.Self;
            redirectModel.url = resultModel.Headers.location;

            model.action = DomainModel.ResponseModelActionsEnum.Redirect;
            model.data = JSON.stringify(redirectModel);

            // set access_token to cookie
            res.cookie(C.CookiesKeyConstants.AccessToken, "TestAccessToken", { maxAge: C.CookiesTimeout, domain: '', path: '/', secure: false });

          }else{ // Error
            res.statusCode = 422;
            model.action = DomainModel.ResponseModelActionsEnum.Error;
            model.data = result.Data;
          }

          console.log("Register Response Model:", model);
          res.write(JSON.stringify(model));
          res.end();
        });
      }else{
        res.statusCode = 422;
        model.action = DomainModel.ResponseModelActionsEnum.Error;
        model.data = resultModel.Data;

        console.log("Register Response Model:", model);
        res.write(JSON.stringify(model));
        res.end();
      }
  });
}