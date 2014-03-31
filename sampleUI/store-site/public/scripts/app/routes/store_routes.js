
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
                var logoutUrl = Utils.Format(AppConfig.UrlConstants.LogoutUrl, AppConfig.Runtime.SocketAddress, Utils.Cookies.Get(AppConfig.CookiesName.IdToken));
                //$.get(logoutUrl);
                location.href = logoutUrl;
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
        /*
        setupController: function(controller, model){
            var provider = new CartProvider();
            provider.Get(Utils.GenerateRequestModel(null), function(result){
                if(result.data.status == 200){
                    var cartObj = JSON.parse(result.data.data);
                    var cartItems = new Array();

                    for(var i = 0; i < cartObj.orderItems.length; ++i){
                        var item = cartObj.orderItems[i];
                        cartItems.push({
                            id: i,
                            product_id: item.offer.id,
                            qty: item.quantity,
                            selected: item.selected
                        });
                    }

                    controller.set("content.cartItems", cartItems);
                }else{

                }
            });
        }
        */
    }),

    OrderSummaryRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.Store.OrderSummary);
        },
        setupController: function(controller, model){
            var provider = new CartProvider();
            provider.GetOrder(Utils.GenerateRequestModel(null), function(resultData){
                if(resultData.data.status == 200){
                    var order = JSON.parse(resultData.data.data);
                    // set products
                    var products = new Array();
                    for(var i =0; i < order.orderItems.length; ++i){
                        var item = order.orderItems[i];
                        products.push({
                            id: item.offer.id, price: item.unitPrice, qty: item.quantity, subTotal: item.totalAmount
                        });
                    }
                    controller.set("content.products", products);

                    // set shipping method name
                    var shippingMethod = "None";
                    for(var i = 0; i < AppConfig.ShippingMethods.length; ++i){
                        var item = AppConfig.ShippingMethods[i];
                        if(item.value == order.shippingMethodId.id){
                            shippingMethod = item.name;
                            break;
                        }
                    }
                    controller.set("content.shippingMethodName", shippingMethod);

                    // set shipping info
                    var billingProvider = new BillingProvider();
                    billingProvider.Get(Utils.GenerateRequestModel({shippingId: order.shippingAddressId.id}), function(resultData){
                        if(resultData.data.status == 200){
                            controller.set("content.shippingAddress", JSON.parse(resultData.data.data));
                        }else{

                        }
                    });

                    // set payment method
                    var paymentProvider = new PaymentProvider();
                    paymentProvider.Get(Utils.GenerateRequestModel({paymentId: order.paymentInstruments[0].id}), function(resultData){
                        if(resultData.data.status == 200){
                            var payment = JSON.parse(resultData.data.data);
                            controller.set("content.paymentMethodName", payment.creditCardRequest.type + " " + payment.accountNum.substr(payment.accountNum.length - 4, 4));
                        }else{

                        }
                    });

                    // amount
                    controller.set("content.discount", order.totalDiscount);
                    controller.set("content.shippingFee", order.totalShippingFee);
                    controller.set("content.shippingFeeDiscount", order.totalShippingFeeDiscount);
                    controller.set("content.tax", order.totalTax);
                    controller.set("content.totalAmount", order.totalAmount);

                }else{
                    throw "Can't get Order!";
                }
            });
        }
    }),

    ThanksRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.Store.Thanks);
        }
    })
};