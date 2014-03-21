
var ShippingInfoRoutes = {
    EntryRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.ShippingInfo.Entry);
        }
    }),
    EditRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.ShippingInfo.Edit);
        }
    })
};