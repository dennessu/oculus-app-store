
exports.WelcomeModel = function(){
    this.source = "Oculus";
    this.action = "welcome";
    this.locale = "en_US";
    this.recipient = "";
    this.properties =
    {
        "accountname": ""
    };
};

exports.DevAccountModel = function(){
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