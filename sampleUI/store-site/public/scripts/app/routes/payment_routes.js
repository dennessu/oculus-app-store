
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
        setupController: function(controller, model){

            var provider = new PaymentProvider();
            provider.PaymentInstruments(Utils.GenerateRequestModel(null), function(result){
                if(result.data.status == 200){
                    var payments = JSON.parse(result.data.data).results;
                    var paymentList = new Array();
                    paymentList.push({
                        t: "Please choose",
                        v: ""
                    });
                    for(var i = 0; i < payments.length; ++i){
                        var item = payments[i];
                        paymentList.push({
                            t: item.creditCardRequest.type + " " + item.accountNum.substr(item.accountNum.length - 4, 4),
                            v: item.self.id
                        });
                    }
                    controller.set("content.paymentList", paymentList);
                }else{
                    // Error
                }
            });
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