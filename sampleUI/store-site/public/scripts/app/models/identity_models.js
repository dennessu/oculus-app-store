
var IdentityModels = {
    LoginModel: function(){
        this.event = "";
        this.cid = "";
        this.username = "";
        this.password = "";
    },

    TFAModel: function(){
        this.code = "";
        this.remember = false
    },

    RegisterModel: function () {
        this.username = "";
        this.email = "";
        this.password = "";
        this.phone = "";
        this.brithday = "";
        this.country = "";
        this.isAgree = false;
        this.isReceive = false;
    }

};