Ember.TEMPLATES["login"] = "";
var App = Ember.App = Ember.Application.create();

App.Router.map(function(){
    this.resource("application", {path:"/"}, function(){

    });
    this.resource("login", {path: "/login"});
    this.resource("register", {path: "/register"});
});
App.ApplicationRoute = Ember.Route.extend({});
App.ApplicationIndexRoute = Ember.Route.extend({
    renderTemplate: function(){
        console.log("[ApplicationRoute] Render LoginView");
        this.render("login");
    }
});
App.LoginRoute = Ember.Route.extend({});
App.RegisterRoute = Ember.Route.extend({});

App.LoginView = Ember.View.extend({
    //template: Utils.GetViews("login"),
    template: "",
    willInsertElement: function(){

        console.log("[LoginView] Will Insert");
        Utils.GetViews("login");
        this.set("template", Ember.TEMPLATES["login"]);

    },
    willClearRender: function(){
        console.log("[LoginView] Will Clear");
    },
    render: function(){
        console.log("[LoginView] Render");
        Utils.GetViews("login");
        this.set("template", Ember.TEMPLATES["login"]);
    }
});
App.RegisterView = Ember.View.extend({
    //template: Utils.GetViews("register")
    template: Ember.TEMPLATES["register"],
    willInsertElement: function(){
        Utils.GetViews("register");
        this.template = Ember.TEMPLATES["register"];
    },
    willClearRender: function(){

    },
    render: function(){
        console.log("[LoginView] Render");
        Utils.GetViews("register");
        this.set("template", Ember.TEMPLATES["register"]);
    }
});

App.LoginController = Ember.ObjectController.extend({
    content: {
        username: "",
        password: ""
    },

    actions: {
        Submit: function(){
            console.log("[Login Controller] Click Login");
        }

    }


});

