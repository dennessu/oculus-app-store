
var C = require('../config/configuration');
var SessionStore = require('./SessionStore');
var HeaderModel = require('../models/page/warpers/HeaderModel');

var Utils = function(){};

Utils.Base64ToString = function(base64Str){
    return new Buffer(base64Str, 'base64').toString();
};

// target defined fill to original
Utils.MoreFilling = function(original, target){

  for(var p in original){
    var p_type = typeof(original[p]);

    if(p_type != "function"){
      if(p_type == "object"){
        if(typeof(target[p]) != "undefined"){
          original[p] = this.MoreFilling(original[p], target[p]);
        }
      }else{
        if(typeof(target[p]) != "undefined"){
          original[p] = target[p];
        }
      }
    }
  }

  // Append new property
  for(var p in target){
    var p_type = typeof(target[p]);

    if(p_type != "function"){
      if(typeof(original[p]) != "undefined") continue;
      original[p] = target[p];
    }
  }

  return original;
};

Utils.SaveQueryStrings = function(req, res, queryStringArr){
  if(queryStringArr == null || typeof(queryStringArr) == "undefined" || queryStringArr.length == 0) return false;

  for(var i = 0; i < queryStringArr.length; ++i){
    if(req.query[queryStringArr[i]]){
      res.cookie(queryStringArr[i], req.query[queryStringArr[i]], { maxAge: C.CookiesTimeout, domain: '', path: '/', secure: false });
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

Utils.GetBaseHeaderModel = function(){
  var model = new HeaderModel();
  model.LoginLink = C.LoginUrl;
  model.RegisterLink = C.RegisterUrl;

  return model;
};

Utils.FillAuthInfoToBaseModel = function(req, res, model){

    var store = new SessionStore(req, res);

    if(typeof(store.Get(C.SessionKeys.IsAuthenticate)) != "undefined"
        && store.Get(C.SessionKeys.IsAuthenticate) == "true"){
        model.Header.IsAuthenticate = true;
        model.Header.Username = store.Get(C.SessionKeys.Username);
    }else{
        model.Header.IsAuthenticate = false;
    }
};

module.exports = Utils;