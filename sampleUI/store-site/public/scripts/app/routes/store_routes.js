
var StoreRoutes = {
    ApplicationRoute: Ember.Route.extend({
        init: function(){
            this._super();
            console.log("[ApplicationRoute] Application.Init");

            App.AuthManager = AuthManager.create();

            if(App.AuthManager.isAuthenticated()){
                console.log("[ApplicationRoute] Authenticated");

                // TODO: Merge Cart
            }else{
                // TODO: Create Anonymous User
            }
        },
        actions: {
            logout: function() {
                App.AuthManager.reset();
            }
        }
    }),
    IndexRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.Store.Index);
        },
        model: function(){
            return this.store.findAll("Product");
        }
    }),
    DetailRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.Store.Detail);
        },
        model: function(params){
            return this.store.find("Product", params.productId);
        }
    }),

    CartRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.Store.Cart);
        },
        model: function(){
            return this.store.findAll("CartItem");
        }
    }),

    OrderSummaryRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.Store.OrderSummary);
        }
    })
};