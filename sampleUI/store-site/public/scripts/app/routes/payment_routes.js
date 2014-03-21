
var PaymentRoutes = {
    EntryRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.Payment.Entry);
        },
        model: function(){
            return this.store.findAll("Product");
        }
    }),
    EditRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.Payment.Edit);
        },
        model: function(){
            return this.store.findAll("Product");
        }
    })
};