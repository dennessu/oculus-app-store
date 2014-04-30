
exports.ResultModel = function(){
    this.status = 200;
    this.data =  "";
};

exports.ResultStatusEnum = {
    Normal: 200,
    Redirect: 302, // return '{target: _blank or _self, url: '' }'
    APIError: 1000, // API throw an error
    Exception: 2000  // code throw an exception
};

exports.RedirectModel = function(){
    this.target = "";
    this.url = ""
};

exports.RedirectTargetsEnum = {
    Blank: "_blank",
    Self: "_self"
};

exports.SettingModel = function(){
    this.type = "";
    this.data = "";
};

exports.ResponseModel = function(){
    this.data = ""; // ResultModel
    this.settings = ""; // SettingModel Array
};
