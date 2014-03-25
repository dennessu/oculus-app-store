
var StoreRoutes = {
    ApplicationRoute: Ember.Route.extend({
        init: function(){
            this._super();
            var _self = this;
            console.log("[ApplicationRoute] Application.Init");

            App.AuthManager = AuthManager.create();

            if(App.AuthManager.isAuthenticated()){
                console.log("[ApplicationRoute] Authenticated");

                var provider = new CartProvider();
                provider.Merge(Utils.GenerateRequestModel(null), function(resultData){
                    var resultModel = resultData.data;
                    if (resultModel.status == 200) {
                        console.log("[ApplicationRoute:Init] Merge Car Success");

                        Utils.Cookies.Remove(AppConfig.CookiesName.AnonymousUserId);
                        Utils.Cookies.Remove(AppConfig.CookiesName.AnonymousCartId);
                    } else {
                        console.log("[ApplicationRoute:Init] Merge Car Failed!");
                    }
                });
            }else{
                if(Ember.isEmpty(App.AuthManager.getUserId())){
                    var provider = new IdentityProvider();

                    provider.GetAnonymousUser(Utils.GenerateRequestModel(null), function (data) {
                        var resultModel = data.data;
                        if (resultModel.status == 200) {
                            console.log("[ApplicationRoute:Init] Create anonymous user success!");
                        } else {
                            console.log("[ApplicationRoute:Init] Create anonymous user failed!");
                        }
                    });
                }
            }
        },
        beforeModel: function(){

        },
        afterModel: function(){
            if(App.AuthManager.isAuthenticated()){
                // to before route
                var beforeRoute = Utils.Cookies.Get(AppConfig.CookiesName.BeforeRoute);
                if(!Ember.isEmpty(beforeRoute)){
                    console.log("[ApplicationRoute] After Model: transitionTo ", beforeRoute);
                    Utils.Cookies.Remove(AppConfig.CookiesName.BeforeRoute);
                    this.transitionTo(beforeRoute);
                }
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
    }),

    ThanksRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.Store.Thanks);
        }
    })
};