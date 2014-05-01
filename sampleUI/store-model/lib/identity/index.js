
var User = function(){
    this.username = "";
    this.isAnonymous = false;
};

var UserCredential = function(){
    this.type = "PASSWORD";
    this.value = "password";
};

var PersonalInfo = function(){
    this.user = {
        "href": "http://api.oculusvr-demo.com:8081/v1/users/6BD4FDF53C9F",
        "id": ""
    };
    this.type = "EMAIL";
    this.value = {
        "value": "TomSlick@yahoo.com"
    };
};

var AuthCode = function(){
    this.cid = "";
    this.username = "";
    this.password = "";
    this.event = "";
};

var Code = function(){
    this.code = "";
    this.grant_type = "authorization_code";
    this.client_id = "client";
    this.client_secret = "secret";
    this.redirect_uri = "";
};

var OptIn = function(){
    this.user = {
        href: "#",
        id: ""
    };
    this.type = "";
};

exports.UserModel = User;
exports.UserCredentialModel = UserCredential;
exports.PersonalInfoModel = PersonalInfo;
exports.AuthenticateModel = AuthCode;
exports.CodeModel = Code;
exports.OptInModel = OptIn;
