var http = require('http');
var query = require('querystring');
var C = require('../../config/configuration');
var DomainModel = require('../../models');

module.exports = function(req, res){

  var privatekey = C.Google_Captcha_PrivateKey;
  var remoteip = req.ip;
  var challenge = req.body["recaptcha_challenge_field"];
  var response = req.body["recaptcha_response_field"];

  var post_data = {"privatekey":privatekey, "remoteip": remoteip, "challenge": challenge, "response": response };
  var post_content = query.stringify(post_data);

  var options = {
    port: 80,
    hostname: C.Google_Captcha_Hostname,
    path: C.Google_Captcha_VerifyUrl,
    method: 'POST',
    headers:{
      'Content-Type':'application/x-www-form-urlencoded',
      'Content-Length': post_content.length
    }
  };

  console.log("-------------------------------------------------------");
  console.log("Google Recaptcha Verify");
  console.log("Post content:\n", post_content);
  console.log("Http options:\n", options);

  var model = new DomainModel.ResponseModel();

  var request = http.request(options, function(result){
    console.log('Status: ' + result.statusCode);

    result.setEncoding("utf8");

    var _data='';
    result.on('data', function(chunk){
      _data += chunk;
    });
    result.on('end', function(){
      console.log("Response Data: \n", _data);

      if (_data.indexOf("true") > -1) {

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
      } else {
        res.statusCode = 422;
        model.action = DomainModel.ResponseModelActionsEnum.Error;
        model.data = "";
      }
      console.log("------------------------------------------------------");

      console.log("Captcha Response Model:", model);
      res.write(JSON.stringify(model));
      res.end();
      return;
    });
  });

  request.on("error", function(e){
    console.log("Request error:" + e.message);

    res.statusCode = 422;
    model.action = DomainModel.ResponseModelActionsEnum.Error;
    model.data = "";

    console.log("Captcha Response Model:", model);
    res.write(JSON.stringify(model));
    res.end();
  });

  request.write(post_content);
  request.end();
};