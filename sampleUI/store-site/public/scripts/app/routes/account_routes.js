var AccountRoutes = {
    LayoutRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.Account.Layout);
        }
    }),
    IndexRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.Account.Index);
        }
    }),
    EditInfoRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.Account.EditInfo);
        }
    }),
    EditPasswordRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.Account.EditPassword);
        }
    }),
    EditShippingRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.Account.EditShipping);
        }
    }),
    ProfileRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.Account.Profile);
        }
    }),
    EditProfileRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.Account.EditProfile);
        }
    }),
    HistoryRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.Account.History);
        }
    }),
    PaymentRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.Account.Payment);
        }
    })
};