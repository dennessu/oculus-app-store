
exports.ResponseModelActionsEnum = {
  Normal: 200, // return null content
  Redirect: 302, // return '{target: _blank or _self, url: '' }'
  Error: 1000, // return error json model
  JSON: 1001 // return json model from api callback
};

// for AJAX
exports.ResponseModel = function(){
  this.action = 200;
  this.data = "";
};

exports.RedirectModelTargetsEnum = {
  Blank: "_blank",
  Self: "_self"
};
exports.RedirectModel = function(){
  this.target = "";
  this.url =  "";
};
