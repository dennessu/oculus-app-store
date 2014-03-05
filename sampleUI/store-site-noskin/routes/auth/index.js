var Async = require('async');
var C = require('../../config/configuration');
var Utils = require('../../helper/utils');
var Session = require('../../helper/session_store');

var CodeMode = require('store-model').Identity.CodeModel;
var IdentityDataProvider = require('store-data-provider').Identity;

exports.Login = function (req, res) {

    var code = req.query[C.QueryStrings.Code];

    if (code != undefined && code != null) {

        //store.Set(C.SessionKeys.AccessToken, accessToken);
        //store.Set(C.SessionKeys.IdToken, idToken);

        var store = new Session(req, res);
        var identity = new IdentityDataProvider(C.Identity_API_Host, C.Identity_API_Port);
        // 1 Get user id
        // 2 Get user profile
        Async.waterfall([
            function (cb) {

                var model = new CodeMode();
                model.code = code;
                model.redirect_uri = C.LoginBackUrl;

                identity.PostTokenInfoByCode(model, function (result) {
                    if (result.StatusCode == 200) {
                        if (typeof(result.Data) != "undefined" && result.Data != null) {
                            var resObj = JSON.parse(result.Data);
                            if (typeof(resObj[C.FieldNames.AccessToken]) != "undefined") {
                                store.Set(C.SessionKeys.AccessToken, resObj[C.FieldNames.AccessToken]);

                                cb(null, resObj[C.FieldNames.AccessToken]);
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
                            if (typeof(resObj[C.FieldNames.TokenInfoUserId]) != "undefined") {

                                store.Set(C.SessionKeys.IsAuthenticate, true);
                                store.Set(C.SessionKeys.UserId, resObj[C.FieldNames.TokenInfoUserId]);

                                cb(null, resObj[C.FieldNames.TokenInfoUserId]);
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
                identity.GetPayinProfilesByUserId(userId, null, function (result) {
                    if (result.StatusCode == 200) {
                        if (typeof(result.Data) != "undefined" && result.Data != null) {
                            var resObj = JSON.parse(result.Data);
                            store.Set(C.SessionKeys.Username, resObj[0][C.FieldNames.ProfileFirstname] + " " + resObj[0][C.FieldNames.ProfileLastname]);
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
    store.Remove(C.SessionKeys.AccessToken);
    store.Remove(C.SessionKeys.IdToken);
    store.Remove(C.SessionKeys.IsAuthenticate);
    store.Remove(C.SessionKeys.UserId);
    store.Remove(C.SessionKeys.Username);

    res.redirect("/");
    res.end();
};

exports.Register = function(req, res){

    res.redirect('/');
    res.end();
};
