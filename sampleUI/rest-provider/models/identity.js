
var UserModel = function(){
    this.userName = "";
    this.password = "";
    this.status = "ACTIVE";
};
var ProfileModel = function(){
    this.user = {
        "href": "https://data.oculusvr.com/v1/users/123",
        "id": ""
    };
    this.type = "";
    this.region = "";
    this.firstName = "";
    this.middleName = "";
    this.lastName = "";
    this.dateOfBirth = "";
    this.locale = "";
};
var AuthorizeModel = function(){
    this.cid = "";
    this.username = "";
    this.password = "";
    this.event = "";
};
var CodeModel = function(){
    this.code = "";
    this.grant_type = "authorization_code";
    this.client_id = "client";
    this.client_secret = "secret";
    this.redirect_uri = "";
};
var OptInModel = function(){
    this.user = {
        href: "#",
        id: ""
    };
    this.type = "";
};

exports.UserModel = UserModel;
exports.ProfileModel = ProfileModel;
exports.AuthorizeModel = AuthorizeModel;
exports.CodeModel = CodeModel;
exports.OptInModel = OptInModel;
