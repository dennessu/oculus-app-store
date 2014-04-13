/**
 * Created by Haiwei on 2014/4/11.
 */

var App = Ember.App = Ember.Application.create();

App.Router.map(function(){
    this.resource("login");
});

App.ApplicationRoute = Ember.Route.extend({
    init: function(){
        console.log("[ApplicationRoute:Init]");

        App.AuthManager = AuthManager.create();
    },
    beforeModel: function(){
        if(!App.AuthManager.isAuthenticated()){
            var code = Utils.Cookies.Get(AppConfig.CookiesName.Code);
            if(code == null) {
                this.transitionTo("login");
            }else{
                console.log("[Auth Code]", code);
            }
        }else{
            this.transitionTo("index");
        }
    }
});
App.IndexRoute = Ember.Route.extend({
    beforeModel: function(){
        console.log("[IndexRoute:beforeModel]");
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
        this.controllerFor('head').set('content', {"title": "OK"});
        this.controllerFor('profiles').set('isLoading', false);
    }
});
App.LoginRoute = Ember.Route.extend({

});

App.LoginView = Ember.View.extend({

});
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
App.HeadController = Ember.ObjectController.extend({
    context:{
        eid: "Head"
    },
    actions:{

    }

});
App.SidebarController = Ember.ObjectController.extend({
    context:{
        eid: "Sidebar"
    },
    actions:{
        Filter: function(){
            console.log(this.get('context').id);
        }
    }
});
App.ProfilesController = Ember.ObjectController.extend({
    isLoading: true,
    context:{
        eid: "Profiles"
    },
    actions:{
        Filter: function(){
            var eid = this.get("context").eid;
            $("#" + eid + " .panel-body .panel-filter").show();
        }
    }
});
App.EntitlementsController = Ember.ObjectController.extend({
    isLoading: true,
    context:{
        eid: "Entitlements"
    },
    actions:{
        Filter: function(){
            var eid = this.get("context").eid;
            $("#" + eid + " .panel-body .panel-filter").show();
        }
    }
});
App.PaymentsController = Ember.ObjectController.extend({
    isLoading: true,
    context:{
        eid: "Payments"
    },
    actions:{
        Filter: function(){
            var eid = this.get("context").eid;
            $("#" + eid + " .panel-body .panel-filter").show();
        }
    }
});
App.ShippingController = Ember.ObjectController.extend({
    isLoading: true,
    context:{
        eid: "ShippingInfo"
    },
    actions:{
        Filter: function(){
            var eid = this.get("context").eid;
            $("#" + eid + " .panel-body .panel-filter").show();
        }
    }
});
App.HistoriesController = Ember.ObjectController.extend({
    isLoading: true,
    context:{
        eid: "Histories"
    },
    actions:{
        Filter: function(){
            var eid = this.get("context").eid;
            $("#" + eid + " .panel-body .panel-filter").show();
        }
    }
});