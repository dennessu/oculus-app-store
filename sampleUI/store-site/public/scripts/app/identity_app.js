var App = Ember.App = Ember.Application.create();

App.Router.map(function(){
    this.route("login");
    this.route("register");
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
    },
    didInsertElement: function(){
        $("form").validate();
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
        $("form").validate();
    }
});

App.LoginController = Ember.ObjectController.extend({
    content: {
        username: "",
        password: ""
    },

    actions: {
        Submit: function(){
            console.log("[LoginController:Submit] Click Login");
            var provider = new IdentityProvider();

            var model = new IdentityModels.LoginModel();
            model.event = Utils.Cookies.Get(AppConfig.CookiesName.Event);
            model.cid = Utils.Cookies.Get(AppConfig.CookiesName.ConversationId);
            model.username = this.get("content.username");
            model.password = this.get("content.password");

            var data = {
                body: model,
                cookies: Utils.Cookies.GetAll()
            };

            provider.Login(data, function(data){
                console.log(data);
                //provider.Discount();
            });
        },
        Cancel: function(){
            console.log("[LoginController:Cancel] Click Cancel");
        }
    }


});

