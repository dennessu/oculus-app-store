
var ShippingInfoRoutes = {
    LayoutRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.ShippingInfo.Layout);
        }
    }),
    IndexRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.ShippingInfo.Index);
        }
    }),
    AddressRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.ShippingInfo.Address);
        },
        setupController: function(controller, model){
            var provider = new BillingProvider();
            provider.GetShippingInfos(Utils.GenerateRequestModel(null), function(result){
                if(result.data.status == 200){
                    var lists = JSON.parse(result.data.data).results;
                    controller.set("content.results", lists);
                }else{

                }
            });
        }
    }),
    EditRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.ShippingInfo.Edit);
        }
    })
};