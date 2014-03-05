var C = require('../config/configuration');


// target defined fill to original
exports.MoreFilling = function(original, target){

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

exports.SaveQueryStrings = function(req, res, queryStringArr){
  if(queryStringArr == null || typeof(queryStringArr) == "undefined" || queryStringArr.length == 0) return false;

  for(var i = 0; i < queryStringArr.length; ++i){
    if(req.query[queryStringArr[i]]){
      res.cookie(queryStringArr[i], req.query[queryStringArr[i]], { maxAge: C.CookiesTimeout, domain: '', path: '/', secure: false });
    }
  }
  return true;
};

exports.ClearQueryStrings = function(res, queryStringArr){
  if(queryStringArr == null || typeof(queryStringArr) == "undefined" || queryStringArr.length == 0) return false;

  for(var i = 0; i < queryStringArr.length; ++i){
    res.clearCookie(queryStringArr[i], { path: '/'});
  }
  return true;
};
