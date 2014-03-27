var Async = require('async');
var Session = require('../../utils/session_store');

var CodeMode = require('store-model').Identity.CodeModel;
var IdentityDataProvider = require('store-data-provider').Identity;

exports.Login = function (req, res) {

    var code = req.query[process.AppConfig.QueryStrings.Code];

    if (code != undefined && code != null) {

        var store = new Session(req, res);
        var identity = new IdentityDataProvider(process.AppConfig.Identity_API_Host, process.AppConfig.Identity_API_Port);
        // 1 Get user id
        // 2 Get user profile
        Async.waterfall([
            function (cb) {
                var model = new CodeMode();
                model.code = code;
                model.redirect_uri = process.AppConfig.Urls.GetCallbackLoginUrl(req);

                identity.PostTokenInfoByCode(model, function (result) {
                    if (result.StatusCode == 200) {
                        if (typeof(result.Data) != "undefined" && result.Data != null) {
                            var resObj = JSON.parse(result.Data);
                            if (typeof(resObj[process.AppConfig.FieldNames.AccessToken]) != "undefined") {
                                store.Set(process.AppConfig.CookiesName.AccessToken, resObj[process.AppConfig.FieldNames.AccessToken]);

                                cb(null, resObj[process.AppConfig.FieldNames.AccessToken]);
                            } else {
                                console.log("Get token info 200, but user id is undefined, Result: ", result);
                                cb("error", result);
                            }
                        } else {
                            console.log("Get token info 200, but data is NULL, Result: ", result);
                            cb("error", result);
                        }
                    } else {
                        console.log("Get token info Failed! Result: ", result);
                        cb("error", result);
                    }
                });
            },
            function (accessToken, cb) {
                identity.GetTokenInfo(accessToken, null, function (result) {
                    if (result.StatusCode == 200) {
                        if (typeof(result.Data) != "undefined" && result.Data != null) {
                            var resObj = JSON.parse(result.Data);
                            if (typeof(resObj[process.AppConfig.FieldNames.TokenInfoUser]) != "undefined") {
                                store.Set(process.AppConfig.CookiesName.UserId, resObj[process.AppConfig.FieldNames.TokenInfoUser].id);

                                cb(null, resObj[process.AppConfig.FieldNames.TokenInfoUser].id);
                            } else {
                                console.log("Get token info 200, but user id is undefined, Result: ", result);
                                cb("error", result);
                            }
                        } else {
                            console.log("Get token info 200, but data is NULL, Result: ", result);
                            cb("error", result);
                        }
                    } else {
                        console.log("Get token info Failed! Result: ", result);
                        cb("error", result);
                    }
                });
            },
            function (userId, cb) {
                identity.GetUser(userId, function(result){
                    if (result.StatusCode == 200) {
                        if (typeof(result.Data) != "undefined" && result.Data != null) {
                            var resObj = JSON.parse(result.Data);
                            store.Set(process.AppConfig.CookiesName.Username, resObj[process.AppConfig.FieldNames.Username]);
                            cb(null, 200);
                        } else {
                            console.log("Get payin profiles 200, but data is NULL, Result: ", result);
                            cb("error", result);
                        }
                    } else {
                        console.log("Get payin profiles Failed! Result: ", result);
                        cb("error", result);
                    }
                });
            }],
            function (err, result) {

                res.redirect("/");
                res.end();
            });
    }else{
        console.log("access_token is NULL or id_token is NULL!");
        res.redirect("/");
        res.end();
    }
};

exports.Logout = function(req, res){

    var store = new Session(req, res);
    for(var p in process.AppConfig.CookiesName){
        store.Remove(process.AppConfig.CookiesName[p]);
    }

    res.redirect("/");
    res.end();
};

exports.Register = function(req, res){

    res.redirect('/');
    res.end();
};
