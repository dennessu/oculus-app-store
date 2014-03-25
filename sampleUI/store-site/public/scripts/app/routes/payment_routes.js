
var PaymentRoutes = {
    LayoutRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.Payment.Layout);
        }
    }),
    IndexRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.Payment.Index);
        },
        model: function(){
            return this.store.findAll("CreditCard");
        }
    }),
    EditRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.Payment.Edit);
        }
    })
};