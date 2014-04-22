
var App = Ember.App = Ember.Application.create();

App.Router.map(function(){
    this.route("login");
    this.route("captcha");
    this.route("tfa");
    this.route("register");
    this.route("pin");
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
    }
});
App.CaptchaRoute = Ember.Route.extend({
    beforeModel: function(){
        console.log("[CaptchaRoute] Before Model");
    }
});
App.TfaRoute = Ember.Route.extend({
    beforeModel: function(){
        console.log("[CaptchaRoute] Before Model");
    }
});
App.RegisterRoute = Ember.Route.extend({
    beforeModel: function(){
        console.log("[RegisterRoute] Before Model");
    }
});
App.PinRoute = Ember.Route.extend({
    beforeModel: function(){
        console.log("[PinRoute] Before Model");
    }
});
App.MyRoute = Ember.Route.extend({
    beforeModel: function(){
        console.log("[MyRoute] Before Model");
    }
});

App.LoginView = Ember.View.extend({
    didInsertElement: function(){
        $("form").validate();
    }
});
App.CaptchaView = Ember.View.extend({
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
    didInsertElement: function(){
        $("form").validate();
    }
});
App.RegisterView = Ember.View.extend({
    didInsertElement: function(){
        console.log("[RegisterView:didInsertElement]");

        jQuery.validator.addMethod("ValidEmpty", function(value, element) {
            return $(element).val().toLowerCase().trim() != "please choose" && $(element).val().toLowerCase().trim() != "";
        }, "Please choose");
        jQuery.validator.addMethod("VerifyNumberValue", function(value, element) {
            if(!isNaN(value)){
                return true;
            }
            return false;
        }, "Please select Birth Date.");

        $("form").validate({
            groups:{
                Birthday: "RMonth RDay RYear"
            },
            errorPlacement:function(error,element) {
                if (element.attr("id") == "RMonth" || element.attr("id") == "RDay" || element.attr("id") == "RYear")
                    error.insertAfter("#RYear");
                else if(element.attr("id") == "RIsAgree")
                    error.appendTo(element.parent());
                else
                    error.insertAfter(element);
            }
        });
    }
});
App.PinView = Ember.View.extend({
    didInsertElement: function(){
        $("form").validate();
    }
});
App.MyView = Ember.View.extend({
    didInsertElement: function(){}
});

App.LoginController = Ember.ObjectController.extend({
    errMessage: null,
    content: {
        username: "",
        password: "",
        cid: Utils.Cookies.Get(AppConfig.CookiesName.ConversationId),
        event: Utils.Cookies.Get(AppConfig.CookiesName.Event)
    },

    actions: {
        Submit: function(){
            console.log("[LoginController:Submit] Click Login");
            $("#Login").validate();
            if(!$("#Login").valid()) return;

            if($("#Login .btn-primary").hasClass('load')) return;
            $("#Login .btn-primary").addClass('load');

            var _self = this;
            var loginType = Utils.Cookies.Get(AppConfig.CookiesName.LoginType);

            if(loginType == "code"){
                var model = this.get("content");

                $.ajax({
                    url: "/api/login",
                    type: "POST",
                    cache: false,
                    async: false,
                    dataType: "json",
                    data: {"model": model},
                    success: function(data, textStatus, jqXHR){
                        _self.set("errMessage", null);
                        var result = data.data;

                        if(result.status == 200){
                            var redirectUrl = Utils.Cookies.Get(AppConfig.CookiesName.RedirectUrl);
                            //location.href = redirectUrl;
                        }else if(result.status == 302){
                            //location.href = result.data.url;
                        }
                    },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                        _self.set("errMessage", Utils.GetErrorMessage(textStatus));
                        $("#Login .btn-primary").removeClass('load');
                    }
                });

                return false;
            }else{
                console.log("[LoginController:Submit] submit form");
                // submit form
                $("#Login").submit();
            }
        },
        Cancel: function(){
            console.log("[LoginController:Cancel] Click Cancel");
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

            /*
            var provider = new IdentityProvider();

            provider.Captcha(Utils.GenerateRequestModel(verifyData), function(data){
                var resultModel = data.data;
                if(resultModel.status == 200){
                    _self.set("content.errMessage", null);

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
            */
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

            /*
            var _self = this;
            var provider = new IdentityProvider();

            var tfaModel = new IdentityModels.TFAModel();
            tfaModel.code = this.get("content.code");
            tfaModel.remember = this.get("content.remember");

            provider.TFA(Utils.GenerateRequestModel(tfaModel), function(data){
                var resultModel = data.data;
                if(resultModel.status == 200){
                    _self.set("content.errMessage", null);

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
            */
        },
        Cancel: function(){
            console.log("[TfaController:Cancel] Click Cancel");
            this.transitionToRouteAnimated('login', {main: 'flip'});
        }
    }
});
App.RegisterController = Ember.ObjectController.extend({
    errMessage: null,
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
        country: "",
        isAgree: false,
        isReceive: false
    },

    actions: {
        Submit: function(){
            console.log("[RegisterController:Submit] Click Continue");

            if($("#BtnRegister").hasClass('load')) return;
            $("#BtnRegister").addClass('load');

            /*
            var _self = this;
            var provider = new IdentityProvider();

            var model = new IdentityModels.RegisterModel();
            Utils.FillObject(model, this.get("content"), 2);
            model.brithday = new Date(parseInt(_self.get("content.year")), parseInt(_self.get("content.month")) - 1, parseInt(_self.get("content.day")) + 1);

            provider.Register(Utils.GenerateRequestModel(model), function(data){
                var resultModel = data.data;
                if(resultModel.status == 200){
                    _self.set("errMessage", null);

                    var redirectUrl = Utils.Cookies.Get(AppConfig.CookiesName.RedirectUrl);
                    // show captcha
                    if(AppConfig.Feature.Captcha){
                        _self.transitionToRouteAnimated('captcha', {main: 'flip'});
                        return;
                    }else if(AppConfig.Feature.PIN){
                        _self.transitionToRouteAnimated('pin', {main: 'flip'});
                        return;
                    }else if(redirectUrl != null && redirectUrl != ""){
                        location.href = redirectUrl;
                    }else{
                        this.transitionToRouteAnimated('my', {main: 'flip'});
                        return;
                    }
                }else if(resultModel.status == 302){
                    // redirect back
                    location.href = unescape(resultModel.data.url);
                }else{
                    // error
                    _self.set("errMessage", Utils.GetErrorMessage(resultModel));
                    $("#BtnRegister").removeClass('load');
                }
            });
            */
        },
        Cancel: function(){
            console.log("[CaptchaController:Cancel] Click Cancel");
        }
    }
});
App.PinController = Ember.ObjectController.extend({
    content:{
        code: "",
        errMessage: ""
    },
    actions: {
        Submit: function(){
            console.log("[PinController:Submit] Click Continue");

            /*
            var _self = this;
            var provider = new IdentityProvider();

            provider.PIN(Utils.GenerateRequestModel({code: this.get("content.code")}), function(data){
                var resultModel = data.data;
                if(resultModel.status == 200){
                    _self.set("content.errMessage", null);

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
            */
        },
        Cancel: function(){
            console.log("[PinController:Cancel] Click Cancel");
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

