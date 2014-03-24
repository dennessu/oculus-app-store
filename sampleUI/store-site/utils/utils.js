
var SessionStore = require('./session_store');
var DomainModels = require('../models/domain');


var Utils = function(){};

Utils.Base64ToString = function(base64Str){
    return new Buffer(base64Str, 'base64').toString();
};

Utils.IsEmpty = function(obj){
    return typeof(obj) === "undefined" || obj === null || obj === "";
}

Utils.IsAuthenticated = function(cookiesObj){
    return !Utils.IsEmpty(cookiesObj[process.AppConfig.CookiesName.UserId]) && !Utils.IsEmpty(cookiesObj[process.AppConfig.CookiesName.AccessToken]);
};

/*
    Fill original object use target object
    @type: 0:full, 1:ChildFull, 2:OneWay
    @return: original object
 */
Utils.FillObject = function(original, target, type){
    if(type == 0 && (original == undefined || original == null)) return target;

    for(var p in original){
        var p_type = typeof(original[p]);

        if(p_type != "function"){
            if(p_type == "object"){
                if(typeof(target[p]) != "undefined" && target[p] != null){
                    if(type == 1){
                        original[p] = this.FillObject(original[p], target[p], 0);
                    }else{
                        original[p] = this.FillObject(original[p], target[p], type);
                    }
                }
            }else{
                if(typeof(target[p]) != "undefined" && target[p] != null){
                    original[p] = target[p];
                }
            }
        }
    }

    if (type == 0) {
        // Append new property
        for (var p in target) {
            var p_type = typeof(target[p]);

            if (p_type != "function") {
                if (typeof(original[p]) != "undefined" && original[p] != null) continue;
                original[p] = target[p];
            }
        }
    }

    return original;
};

// {1} is {2}
Utils.Format = function(str, args) {
    var result = str;
    if (arguments.length > 0) {
        if (arguments.length == 1 && typeof (args) == "object") {
            for (var key in args) {
                if(args[key]!=undefined){
                    var reg = new RegExp("({" + key + "})", "g");
                    result = result.replace(reg, args[key]);
                }
            }
        }
        else {
            for (var i = 0; i < arguments.length; i++) {
                if (arguments[i] != undefined) {
                    var reg= new RegExp("({)" + i + "(})", "g");
                    result = result.replace(reg, arguments[i]);
                }
            }
        }
    }
    return result;
};

Utils.SaveQueryStrings = function(req, res, queryStringArr){
  if(queryStringArr == null || typeof(queryStringArr) == "undefined" || queryStringArr.length == 0) return false;

  for(var i = 0; i < queryStringArr.length; ++i){
    if(req.query[queryStringArr[i]]){
      res.cookie(queryStringArr[i], req.query[queryStringArr[i]], { maxAge: process.AppConfig.CookiesTimeout, domain: '', path: '/', secure: false });
    }
  }
  return true;
};

Utils.ClearQueryStrings = function(res, queryStringArr){
  if(queryStringArr == null || typeof(queryStringArr) == "undefined" || queryStringArr.length == 0) return false;

  for(var i = 0; i < queryStringArr.length; ++i){
    res.clearCookie(queryStringArr[i], { path: '/'});
  }
  return true;
};


Utils.GetCurrentDomain = function(req){
    return Utils.Format("{1}://{2}", req.protocol, req.headers['host']);
};

Utils.GenerateCookieModel = function(name, value){
    var cookie = new DomainModels.SettingModel();
    cookie.type = process.AppConfig.SettingTypeEnum.Cookie;
    cookie.data = {name: name, value: value};

    return cookie;
};

/*
    first parameter is ResultModel
    other parameter is SettingModel Array
 */
Utils.GenerateResponseModel = function(){

    var responseModel = new DomainModels.ResponseModel();
    responseModel.data = arguments[0];
    responseModel.settings = new Array();

    for(var i = 1; i < arguments.length; ++i) responseModel.settings.push(arguments[i]);

    return responseModel;
};

module.exports = Utils;