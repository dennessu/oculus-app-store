
var PaymentRoutes = {
    LayoutRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.Payment.Layout);
        }
    }),
    IndexRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.Payment.Index);
        }
    }),
    EditRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.Payment.Edit);
        }
    })
};