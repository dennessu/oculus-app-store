
var QueryString = require('querystring');
var http = require('http');
var DomainModels = require('../../models/domain');

module.exports = function(data, cb){
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var privatekey = process.AppConfig.Google_Captcha_PrivateKey;
    var remoteip = query.ip;
    var challenge = body["recaptcha_challenge_field"];
    var response = body["recaptcha_response_field"];

    var post_data = {"privatekey":privatekey, "remoteip": remoteip, "challenge": challenge, "response": response };
    var post_content = QueryString.stringify(post_data);

    var options = {
        port: 80,
        hostname: process.AppConfig.Google_Captcha_Hostname,
        path: process.AppConfig.Google_Captcha_VerifyUrl,
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

    var model = new DomainModels.ResponseModel();

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

               var resultModel  = new DomainModels.ResultModel;
                resultModel.status = DomainModels.ResultStatusEnum.Normal;
                model.data = resultModel;
            } else {
                var resultModel  = new DomainModels.ResultModel;
                resultModel.status = DomainModels.ResultStatusEnum.Exception;
                model.data = resultModel;
            }
            console.log("------------------------------------------------------");

            console.log("Captcha Response Model:", model);

            cb(model);
            return;
        });
    });
    request.on("error", function(e){
        console.log("Request error:" + e.message);

        var resultModel  = new DomainModels.ResultModel;
        resultModel.status = DomainModels.ResultStatusEnum.Exception;
        model.data = resultModel;

        console.log("Captcha Response Model:", model);
        cb(model);
    });
    request.write(post_content);
};