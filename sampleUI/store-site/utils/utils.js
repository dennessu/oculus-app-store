
var SessionStore = require('./session_store');
//var HeaderModel = require('../models/page/warpers/header_model');

var Utils = function(){};

Utils.Base64ToString = function(base64Str){
    return new Buffer(base64Str, 'base64').toString();
};

/*
    Fill original object use target object
    @type: full, OneWay
    @return: original object
 */
Utils.FillObject = function(original, target, type){
    for(var p in original){
        var p_type = typeof(original[p]);

        if(p_type != "function"){
            if(p_type == "object"){
                if(typeof(target[p]) != "undefined"){
                    original[p] = this.FillObject(original[p], target[p], type);
                }
            }else{
                if(typeof(target[p]) != "undefined"){
                    original[p] = target[p];
                }
            }
        }
    }

    if (type.toLowerCase() == "full") {
        // Append new property
        for (var p in target) {
            var p_type = typeof(target[p]);

            if (p_type != "function") {
                if (typeof(original[p]) != "undefined") continue;
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
/*
Utils.GetBaseHeaderModel = function(){
  var model = new HeaderModel();
  model.LoginLink = C.LoginUrl;
  model.RegisterLink = C.RegisterUrl;

  return model;
};

Utils.FillAuthInfoToBaseModel = function(req, res, model){

    var store = new SessionStore(req, res);

    if(typeof(store.Get(C.CookiesName.IsAuthenticate)) != "undefined"
        && store.Get(C.CookiesName.IsAuthenticate) == "true"){
        model.Header.IsAuthenticate = true;
        model.Header.Username = store.Get(C.CookiesName.Username);
    }else{
        model.Header.IsAuthenticate = false;
    }
};
*/
module.exports = Utils;