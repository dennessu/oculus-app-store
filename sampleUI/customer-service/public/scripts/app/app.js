/**
 * Created by Haiwei on 2014/4/11.
 */
var IdentityProvider = new DataProvider.Identity(AppConfig.Identity_API_Host, AppConfig.Identity_API_Port);
var EntitlementsProvider = new DataProvider.Entitlements(AppConfig.Entitlement_API_Host, AppConfig.Entitlement_API_Port);
var PaymentProvider = new DataProvider.Payment(AppConfig.Payment_API_Host, AppConfig.Payment_API_Port);
var BillingProvider = new DataProvider.Billing(AppConfig.Billing_API_Host, AppConfig.Billing_API_Port);
var OrderProvider = new DataProvider.Order(AppConfig.Order_API_Host, AppConfig.Order_API_Port);

var App = Ember.App = Ember.Application.create();
App.AuthKey = AuthKeyModel;

App.Router.map(function(){
    this.resource("login");
    this.resource("lookup");
    this.resource("error");
});

App.ApplicationRoute = Ember.Route.extend({
    init: function(){
        console.log("[ApplicationRoute:Init]");

        //App.AuthManager = AuthManager.create();
    },
    actions: {
        Logout: function() {
            var logoutUrl = Utils.Format(AppConfig.Runtime.LogoutUrl, null, AppConfig.Runtime.LogoutCallbackUrl, Utils.Cookies.Get(AppConfig.CookiesName.IdToken));
            location.href = logoutUrl;
            App.AuthManager.reset();
        }
    }
});
App.IndexRoute = Ember.Route.extend({
    init: function(){

    },
    beforeModel: function(){
        App.AuthManager = AuthManager.create();

        var _self = this;
        console.log("[IndexRoute:beforeModel] Is Authenticated: ", App.AuthManager.isAuthenticated());

        if(!App.AuthManager.isAuthenticated()){
            var code = Utils.Cookies.Get(AppConfig.CookiesName.Code);
            var userId = Utils.Cookies.Get(AppConfig.CookiesName.UserId);

            if(code == null && userId == null) {
                this.transitionTo("login");
            }else{
                var model = new Identity.TokenInfo();
                model.code = code;
                model.redirect_uri = Utils.Cookies.Get(AppConfig.CookiesName.RedirectUrl);
                IdentityProvider.PostTokenInfoByCode({async: false}, model, function(result){
                    Utils.Cookies.Remove(AppConfig.CookiesName.Code);

                    if(result.status == 200){
                        var data = JSON.parse(result.data);

                        var accessToken = data[AppConfig.FieldNames.AccessToken];
                        Utils.Cookies.Set(AppConfig.CookiesName.AccessToken, accessToken);
                        Utils.Cookies.Set(AppConfig.CookiesName.IdToken, data[AppConfig.FieldNames.IdToken]);

                        IdentityProvider.GetTokenInfo(accessToken, {async: false}, function(tokenInfoResult){
                            if(tokenInfoResult.status == 200){
                                var tokenInfo = JSON.parse(tokenInfoResult.data);
                                var userId = tokenInfo[AppConfig.FieldNames.TokenInfoUser].id;
                                Utils.Cookies.Set(AppConfig.CookiesName.UserId, userId);

                                IdentityProvider.GetUserById(userId, {async: false}, function(userResult){
                                    if(userResult.status == 200){
                                        var user = JSON.parse(userResult.data);
                                        var username = user[AppConfig.FieldNames.Username];

                                        Utils.Cookies.Set(AppConfig.CookiesName.Username, username);

                                        console.log("Login Success");
                                        _self.transitionTo("lookup");
                                        return;
                                    }else{
                                        //TODO: Error
                                        _self.transitionTo("error");
                                    }
                                });

                            }else{
                                //TODO: Error
                                _self.transitionTo("error");
                            }
                        });
                    }else{
                        //TODO: Error
                        _self.transitionTo("error");
                    }
                });

                console.log("[IndexRoute:beforeModel] Has code and get user info done!");
            }
        }else{
            var customerId = Utils.Cookies.Get(AppConfig.CookiesName.CustomerId);
            console.log("[IndexRoute:beforeModel] Customer ID: ", customerId);

            if(customerId == null){
                this.transitionTo("lookup");
            }else {
                this.transitionTo("index");
            }
        }
    },
    renderTemplate: function(controller, model) {
        this.render('index');
        this.render('head', {outlet:'head',into: 'index', controller: 'head'});
        this.render('sidebar', {outlet:'sidebar', into: 'index', controller: 'sidebar'});
        this.render('profiles', {outlet:'profiles', into: 'index', controller: 'profiles'});
        this.render('entitlements', {outlet:'entitlements', into: 'index', controller: 'entitlements'});
        this.render('payments', {outlet:'payments', into: 'index', controller: 'payments'});
        this.render('shipping', {outlet:'shipping', into: 'index', controller: 'shipping'});
        this.render('histories', {outlet:'histories', into: 'index', controller: 'histories'});
    },
    setupController: function(controller, model) {
        var _self = this;

        var customerId = Utils.Cookies.Get(AppConfig.CookiesName.CustomerId);

        this.controllerFor('sidebar').set('isLoading', true);
        IdentityProvider.GetUserById(customerId, {async: true, cache: true}, function(result){
            if(result.status == 200){
                _self.controllerFor("sidebar").set("content.user", JSON.parse(result.data));
                _self.controllerFor("sidebar").set("isLoading", false);
            }else{
                //TODO: Error
            }
        });

        this.controllerFor("profiles").set("isLoading", true);
        IdentityProvider.GetPayinProfilesByUserId(customerId, {async: true, cache: true}, function(result){
            if(result.status == 200){
                _self.controllerFor("profiles").set("content.list", JSON.parse(result.data)[AppConfig.FieldNames.Results]);
                _self.controllerFor("profiles").set('isLoading', false);
            }else{
                // TODO: Error
            }
        });

        this.controllerFor("entitlements").set("isLoading", true);
        EntitlementsProvider.GetEntitlementsByUserId(customerId, {async: true, cache: true}, function(result){
            if(result.status == 200){
                _self.controllerFor("entitlements").set("content.list", JSON.parse(result.data)[AppConfig.FieldNames.Results]);
                _self.controllerFor("entitlements").set('isLoading', false);
            }else{
                // TODO: Error
            }
        });

        this.controllerFor("payments").set("isLoading", true);
        PaymentProvider.GetPaymentInstrumentsByUserId(customerId, {async: true, cache: true}, function(result){
            if(result.status == 200){
                _self.controllerFor("payments").set("content.list", JSON.parse(result.data)[AppConfig.FieldNames.Results]);
                _self.controllerFor("payments").set('isLoading', false);
            }else{
                // TODO: Error
            }
        });

        this.controllerFor("shipping").set("isLoading", true);
        BillingProvider.GetShippingInfosByUserId(customerId, {async: true, cache: true}, function(result){
            if(result.status == 200){
                _self.controllerFor("shipping").set("content.list", JSON.parse(result.data)[AppConfig.FieldNames.Results]);
                _self.controllerFor("shipping").set('isLoading', false);
            }else{
                // TODO: Error
            }
        });

        this.controllerFor("histories").set("isLoading", true);
        OrderProvider.GetOrdersByUserId(customerId, {async: true, cache: true}, function(result){
            if(result.status == 200){
                _self.controllerFor("histories").set("content.list", JSON.parse(result.data)[AppConfig.FieldNames.Results]);
                _self.controllerFor("histories").set('isLoading', false);
            }else{
                // TODO: Error
            }
        });
    }
});
App.LoginRoute = Ember.Route.extend({});
App.LookupRoute = Ember.Route.extend({});
App.ErrorRoute = Ember.Route.extend({});

App.LoginView = Ember.View.extend({});
App.LookupView = Ember.View.extend({});
App.ErrorView = Ember.View.extend({});
App.HeadView = Ember.View.extend({
    didInsertElement: function(){
        console.log("[HeadView:didInsertElement]");
    }
});
App.SidebarView = Ember.View.extend({});
App.ProfilesView = Ember.View.extend({
    didInsertElement: function(){
        var loading = this.controller.get("isLoading");
        if(loading){
            $("#Profiles .panel-loading").show();
        }else{
            $("#Profiles .panel-loading").hide();
        }
        $("button.close").click(function(){
            $(this).parent().hide();
        });
    }
});
App.EntitlementsView = Ember.View.extend({
    didInsertElement: function(){
        var loading = this.controller.get("isLoading");
        if(loading){
            $("#Profiles .panel-loading").show();
        }else{
            $("#Profiles .panel-loading").hide();
        }
        $("button.close").click(function(){
            $(this).parent().hide();
        });
    }
});
App.PaymentsView = Ember.View.extend({
    didInsertElement: function(){
        var loading = this.controller.get("isLoading");
        if(loading){
            $("#Profiles .panel-loading").show();
        }else{
            $("#Profiles .panel-loading").hide();
        }
        $("button.close").click(function(){
            $(this).parent().hide();
        });
    }
});
App.ShippingView = Ember.View.extend({
    didInsertElement: function(){
        var loading = this.controller.get("isLoading");
        if(loading){
            $("#Profiles .panel-loading").show();
        }else{
            $("#Profiles .panel-loading").hide();
        }
        $("button.close").click(function(){
            $(this).parent().hide();
        });
    }
});
App.HistoriesView = Ember.View.extend({
    didInsertElement: function(){
        var loading = this.controller.get("isLoading");
        if(loading){
            $("#Profiles .panel-loading").show();
        }else{
            $("#Profiles .panel-loading").hide();
        }
        $("button.close").click(function(){
            $(this).parent().hide();
        });
    }
});

App.LoginController = Ember.ObjectController.extend({

});
App.LookupController = Ember.ObjectController.extend({
    isValid: false,
    content:{
        email: ""
    },
    actions: {
        Submit: function(){
            // TODO: Get user id by email

            if(this.get("isValid") != true) {
                this.set("isValid", true);
            }else{
                this.set("isValid", false);
                //$("#BtnSubmit").addClass("disabled");

                Utils.Cookies.Set(AppConfig.CookiesName.CustomerId, 789);
                this.transitionToRoute("index");
            }
        }
    }
});
App.HeadController = Ember.ObjectController.extend({
    context:{
        eid: "Head",
        username: Utils.Cookies.Get(AppConfig.CookiesName.Username)
    },

    actions:{

    }
});
App.SidebarController = Ember.ObjectController.extend({
    isLoading: true,
    content:{
        eid: "Sidebar",
        user: null
    },
    actions:{
        Filter: function(){
            console.log(this.get('content').id);
        }
    }
});
App.ProfilesController = Ember.ObjectController.extend({
    isLoading: true,
    content:{
        eid: "Profiles"
    },
    actions:{
        Filter: function(){
            var eid = this.get("content").eid;
            $("#" + eid + " .panel-body .panel-filter").show();
        }
    }
});
App.EntitlementsController = Ember.ObjectController.extend({
    isLoading: true,
    content:{
        eid: "Entitlements"
    },
    actions:{
        Filter: function(){
            var eid = this.get("content").eid;
            $("#" + eid + " .panel-body .panel-filter").show();
        }
    }
});
App.PaymentsController = Ember.ObjectController.extend({
    isLoading: true,
    content:{
        eid: "Payments"
    },
    actions:{
        Filter: function(){
            var eid = this.get("content").eid;
            $("#" + eid + " .panel-body .panel-filter").show();
        }
    }
});
App.PaymentItemController = Ember.ObjectController.extend({
    content:{},
    cardNumberLast: function(){
        var cardNumber = this.get("model.accountNum");

        return cardNumber.substr(cardNumber.length -4, 4);
    }.property("model"),

    isPrimary: function(){
        return this.get("model.isDefault") == "true";
    }.property("model")
});

App.ShippingController = Ember.ObjectController.extend({
    isLoading: true,
    content:{
        eid: "ShippingInfo"
    },
    actions:{
        Filter: function(){
            var eid = this.get("content").eid;
            $("#" + eid + " .panel-body .panel-filter").show();
        }
    }
});
App.HistoriesController = Ember.ObjectController.extend({
    isLoading: true,
    content:{
        eid: "Histories"
    },
    actions:{
        Filter: function(){
            var eid = this.get("content").eid;
            $("#" + eid + " .panel-body .panel-filter").show();
        }
    }
});