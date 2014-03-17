
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
    }
};