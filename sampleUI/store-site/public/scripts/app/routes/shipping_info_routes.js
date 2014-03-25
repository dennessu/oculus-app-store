
var ShippingInfoRoutes = {
    LayoutRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.ShippingInfo.Layout);
        }
    }),
    IndexRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.ShippingInfo.Index);
        },
        model: function(){
            return this.store.findAll("ShippingInfo");
        },
        afterModel: function(){
        }
    }),
    EditRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.ShippingInfo.Edit);
        }
    })
};