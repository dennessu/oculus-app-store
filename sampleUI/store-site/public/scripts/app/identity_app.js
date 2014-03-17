var App = Ember.App = Ember.Application.create();

App.Router.map(function(){
    this.route("login");
    this.route("register");
    this.route("captcha");
    this.route("tfa");
    this.route("my");
});

App.IndexRoute = Ember.Route.extend({
    beforeModel: function(){
        //this.transitionTo('login');
        this.transitionToAnimated('login', {main: 'flip'});
    }
});
App.LoginRoute = Ember.Route.extend({
    beforeModel: function(){
        console.log("[LoginRoute] Before Model");
        Utils.GetViews(AppConfig.Templates.Login);
    }
});
App.RegisterRoute = Ember.Route.extend({
    beforeModel: function(){
        console.log("[RegisterRoute] Before Model");
        Utils.GetViews(AppConfig.Templates.Register);
    }
});
App.CaptchaRoute = Ember.Route.extend({
    beforeModel: function(){
        console.log("[CaptchaRoute] Before Model");
        Utils.GetViews(AppConfig.Templates.Captcha);
    }
});
App.TfaRoute = Ember.Route.extend({
    beforeModel: function(){
        console.log("[CaptchaRoute] Before Model");
        Utils.GetViews(AppConfig.Templates.TFA);
    }
});
App.MyRoute = Ember.Route.extend({
    beforeModel: function(){
        console.log("[MyRoute] Before Model");
        Utils.GetViews(AppConfig.Templates.My);
    }
});

App.LoginView = Ember.View.extend({
    template: Ember.TEMPLATES[AppConfig.Templates.Login.name],
    didInsertElement: function(){
        $("form").validate();
    }
});
App.RegisterView = Ember.View.extend({
    template: Ember.TEMPLATES[AppConfig.Templates.Register.name],
    didInsertElement: function(){
        jQuery.validator.addMethod("VerifyNumberValue", function(value, element) {
            if(!isNaN(value)){
                return true;
            }
            return false;
        }, "Please select Birth Date.");

        $("form").validate({
            groups:{
                username:"Month Day Year"
            },
            errorPlacement:function(error,element) {
                if (element.attr("name") == "Month" || element.attr("name") == "Day" || element.attr("name") == "Year")
                    error.insertAfter("#RYear");
                else
                    error.insertAfter(element);
            }
        });
    }
});
App.CaptchaView = Ember.View.extend({
    template: Ember.TEMPLATES[AppConfig.Templates.Captcha.name],
    didInsertElement: function(){
        Recaptcha.create(AppConfig.Google_Captcha_PublicKey, 'captchadiv',
            {
                tabindex: 1,
                theme: "clean",
                callback: Recaptcha.focus_response_field
            });
    }
});
App.TfaView = Ember.View.extend({
    template: Ember.TEMPLATES[AppConfig.Templates.TFA.name],
    didInsertElement: function(){
        $("form").validate();
    }
});
App.MyView = Ember.View.extend({
    template: Ember.TEMPLATES[AppConfig.Templates.My.name],
    didInsertElement: function(){}
});

App.LoginController = Ember.ObjectController.extend({
    content: {
        username: "",
        password: "",

        errMessage: null
    },

    actions: {
        Submit: function(){
            console.log("[LoginController:Submit] Click Login");

            var _self = this;
            var provider = new IdentityProvider();

            var model = new IdentityModels.LoginModel();
            model.event = Utils.Cookies.Get(AppConfig.CookiesName.Event);
            model.cid = Utils.Cookies.Get(AppConfig.CookiesName.ConversationId);
            model.username = this.get("content.username");
            model.password = this.get("content.password");

            var requestModel = new RequestDataModel();
            requestModel.data = model;
            requestModel.cookies = Utils.Cookies.GetAll();

            provider.Login(requestModel, function(data){
                var resultModel = data.data;
                if(resultModel.status == 200){
                    var redirectUrl = Utils.Cookies.Get(AppConfig.CookiesName.RedirectUrl);
                    // show captcha
                    if(AppConfig.Feature.Captcha){
                        _self.transitionToRouteAnimated('captcha', {main: 'flip'});
                        return;
                    }else if(AppConfig.Feature.TFA){
                        _self.transitionToRouteAnimated('tfa', {main: 'flip'});
                        return;
                    }else if(redirectUrl != null && redirectUrl != ""){
                        location.href = redirectUrl;
                    }else{
                        this.transitionToRouteAnimated('my', {main: 'flip'});
                        return;
                    }
                }else if(resultModel.status == 302){
                    // redirect back
                    location.href = resultModel.data.url;
                }else{
                    // error
                    _self.set("content.errMessage", Utils.GetErrorMessage(resultModel));
                }
            });
        },
        Cancel: function(){
            console.log("[LoginController:Cancel] Click Cancel");
        }
    }
});

App.RegisterController = Ember.ObjectController.extend({
    errMessage: "Please try again later!",
    months:(function(){
        var result = new Array();
        result.push({t: "Month", v: ""});
        for(var i = 1; i <= 12; ++i) result.push({t: i, v: i});
        return result;
    }()),
    days:(function(){
        var result = new Array();
        result.push({t: "Day", v: ""});
        for(var i = 1; i <= 31; ++i) result.push({t: i, v: i});
        return result;
    }()),
    years:(function(){
        var result = new Array();
        result.push({t: "Year", v: ""});
        for(var i = new Date().getFullYear(); i >= 1950; --i) result.push({t: i, v: i});
        return result;
    }()),
    countries: (function(){
        var result = new Array();
        for(var i = 0; i < AppConfig.Countries.length; ++i) result.push({t: AppConfig.Countries[i].name, v: AppConfig.Countries[i].value});
        return result;
    }()),

    content: {
        username: "",
        email:"",
        password:"",
        phone: "",
        month:"",
        day:"",
        year:"",
        country: ""
    },

    actions: {
        Submit: function(){

        },
        Cancel: function(){

        }
    }
});

App.CaptchaController = Ember.ObjectController.extend({
    content: {
        errMessage: null
    },
    actions: {
        Submit: function(){
            console.log("[CaptchaController:Submit] Click Continue");

            var _self = this;
            var isFailed = false;

            var verifyData = {
                recaptcha_challenge_field: $("#recaptcha_challenge_field").val(),
                recaptcha_response_field: $("#recaptcha_response_field").val()
            };

            var provider = new IdentityProvider();
            var requestModel = new RequestDataModel();
            requestModel.data = verifyData;
            requestModel.cookies = Utils.Cookies.GetAll();

            provider.Captcha(requestModel, function(data){
                var resultModel = data.data;
                if(resultModel.status == 200){
                    var redirectUrl = Utils.Cookies.Get(AppConfig.CookiesName.RedirectUrl);
                    // show captcha
                    if(AppConfig.Feature.TFA){
                        _self.transitionToRouteAnimated('tfa', {main: 'flip'});
                        return;
                    }else if(redirectUrl != null && redirectUrl != ""){
                        location.href = redirectUrl;
                    }else{
                        _self.transitionToRouteAnimated('my', {main: 'flip'});
                        return;
                    }
                }else{
                    // error
                    isFailed = true;
                    _self.set("content.errMessage", "Please try again!");
                    Recaptcha.reload();
                }
            });
        },
        Cancel: function(){
            console.log("[CaptchaController:Cancel] Click Cancel");
            this.transitionToRouteAnimated('login', {main: 'flip'});
        }
    }
});
App.TfaController = Ember.ObjectController.extend({
    content:{
        code: "",
        remember: false
    },
    actions: {
        Submit: function(){
            console.log("[TfaController:Submit] Click Verify");

            var _self = this;
            var provider = new IdentityProvider();

            var tfaModel = new IdentityModels.TFAModel();
            tfaModel.code = this.get("content.code");
            tfaModel.remember = this.get("content.remember");

            var requestModel = new RequestDataModel();
            requestModel.data = tfaModel;
            requestModel.cookies = Utils.Cookies.GetAll();

            provider.TFA(requestModel, function(data){
                var resultModel = data.data;
                if(resultModel.status == 200){
                    // redirect
                    var redirectUrl = Utils.Cookies.Get(AppConfig.CookiesName.RedirectUrl);
                    // show captcha
                   if(redirectUrl != null && redirectUrl != ""){
                        location.href = redirectUrl;
                    }else{
                       _self.transitionToRouteAnimated('my', {main: 'flip'});
                        return;
                    }
                    _self.set("content.errMessage", null);
                }else{
                    // error
                    _self.set("content.errMessage", "Please try again!");
                }
            });
        },
        Cancel: function(){
            console.log("[TfaController:Cancel] Click Cancel");
            this.transitionToRouteAnimated('login', {main: 'flip'});
        }
    }
});
App.MyController = Ember.ObjectController.extend({
    actions: {
        Logout: function(){
            this.transitionToRouteAnimated('login', {main: 'flip'});
        }
    }
});

