var AccountRoutes = {
    LayoutRoute: Ember.Route.extend({
        beforeModel: function(){
            if(!Ember.App.AuthManager.isAuthenticated()){
                Utils.Cookies.Set(AppConfig.CookiesName.BeforeRoute, "account");
                location.href = AppConfig.LoginUrl;
                return;
            }

            Utils.GetViews(AppConfig.Templates.Account.Layout);
        }
    }),
    IndexRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.Account.Index);
        },
        setupController: function(controller, model){
            // get profile
            var provider = new IdentityProvider();
            provider.GetProfile(Utils.GenerateRequestModel(null), function(resultData){
                if(resultData.data.status == 200){
                    var profile = JSON.parse(resultData.data.data);
                    controller.set("content.firstName", profile.firstName);
                    controller.set("content.lastName", profile.lastName);
                }else{
                    // TODO: Error
                }
            });
        }
    }),
    EditInfoRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.Account.EditInfo);
        },
        setupController: function(controller, model){
            // get profile
            var provider = new IdentityProvider();
            provider.GetProfile(Utils.GenerateRequestModel(null), function(resultData){
               if(resultData.data.status == 200){
                   var profile = JSON.parse(resultData.data.data);
                   controller.set("content.firstName", profile.firstName);
                   controller.set("content.lastName", profile.lastName);
               }else{
                   // TODO: Error
               }
            });

            // get opt-in
            provider.GetOptIns(Utils.GenerateRequestModel(null), function(resultData){
                if(resultData.data.status == 200){
                    var optIns = JSON.parse(resultData.data.data).items;
                    for(var i = 0; i < optIns.length; ++i){
                        if(optIns[i].type.toLowerCase() == "promotion"){
                            controller.set("content.isOptInPromotion", true);
                        }
                        if(optIns[i].type.toLowerCase() == "newsweekly"){
                            controller.set("content.isOptInNewsWeekly", true);
                        }
                    }
                }else{
                    // TODO: Error
                }
            });
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
        },
        setupController: function(controller, model){
            var provider = new CartProvider();
            provider.GetOrders(Utils.GenerateRequestModel(null), function(resultData){
                if(resultData.data.status == 200){
                    var orders = JSON.parse(resultData.data.data).items;
                    controller.set("content.orders", orders);
                }else{
                    // TODO: Error
                }
            });
        }
    }),
    PaymentRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.Account.Payment);
        },
        setupController: function(controoler, model){
            var provider = new PaymentProvider();
            provider.PaymentInstruments(Utils.GenerateRequestModel(null), function(result){
                if(result.data.status == 200){
                    var payments = JSON.parse(result.data.data).items;
                    controoler.set("content.payments", payments);
                }else{
                    console.log("Can't get the payment instruments");
                }
            });
        }
    }),
    AddPaymentRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.Account.AddPayment);
        },
        setupController:function(controller, model){
            var identityProvider = new IdentityProvider();
            identityProvider.GetProfile(Utils.GenerateRequestModel(null), function(resultData){
                if(resultData.data.status == 200){
                    var birthday = new Date(JSON.parse(resultData.data.data).dateOfBirth);
                    var nowYear = new Date().getFullYear();
                    var age = nowYear - birthday.getFullYear();
                    console.log("birthday Years:", birthday.getFullYear(), " Now Years:", nowYear);
                    if(age <= 17){
                        console.log("Set isHolder is True");
                        controller.set("isHolder", true);
                    }else{
                        console.log("Set isHolder is False");
                        controller.set("isHolder", false);
                    }
                }else{
                    console.log("Set isHolder is False");
                    controller.set("isHolder", false);
                }
            });
        }
    }),
    ShippingRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.Account.Shipping);
        },
        setupController: function(controoler, model){
            var provider = new BillingProvider();
            provider.ShippingInfo(Utils.GenerateRequestModel(null), function(result){
                if(result.data.status == 200){
                    var shippings = JSON.parse(result.data.data);
                    controoler.set("content.shippings", shippings);
                }else{
                    console.log("Can't get the shippings");
                }
            });
        }
    }),
    AddShippingRoute: Ember.Route.extend({
        beforeModel: function(){
            Utils.GetViews(AppConfig.Templates.Account.AddShipping);
        }
    })
};