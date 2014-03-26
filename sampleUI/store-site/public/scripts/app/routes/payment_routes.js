
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
    })
};