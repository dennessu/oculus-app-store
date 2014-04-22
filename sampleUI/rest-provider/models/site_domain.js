/**
 * Created by Haiwei on 2014/4/22.
 */

var ResponseModel = function(){
    this.data = ""; // ResultModel
    this.settings = ""; // SettingModel Array
};

var ResultModel = function(){
    this.status = 200;
    this.data =  ""; // API callback response
};

var RedirectModel = function(){
    this.target = ""; // _self or _blank
    this.url = ""
};

var SettingModel = function(){
    this.type = "";
    this.data = ""; // for cookie {name: key, value: v}
};

exports.ResponseModel = ResponseModel;
exports.ResultModel = ResultModel;
exports.RedirectModel = RedirectModel;
exports.SettingModel = SettingModel;