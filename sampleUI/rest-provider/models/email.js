/**
 * Created by Haiwei on 2014/4/16.
 */

var WelcomeModel = function(){
    this.source = "Oculus";
    this.action = "welcome";
    this.locale = "en_US";
    this.recipient = "";
    this.properties =
    {
        "accountname": ""
    };
};

var DevAccountModel = function(){
    this.source = "Oculus";
    this.action = "ActiveDevAccount";
    this.locale = "en_US";
    this.recipient = "";
    this.properties =
    {
        "devname": "ISV",
        "publishername":"Indie"
    };
};

exports.WelcomeModel = WelcomeModel;
exports.DevAccountModel = DevAccountModel;