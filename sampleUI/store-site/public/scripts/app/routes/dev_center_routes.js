var DevCenterRoutes = {
    Index: Ember.Route.extend({
        beforeModel: function(){
            if(!Ember.App.AuthManager.isAuthenticated()){
                Utils.Cookies.Set(AppConfig.CookiesName.BeforeRoute, "devcenter");
                location.href = AppConfig.LoginUrl;
                return;
            }

            Utils.GetViews(AppConfig.Templates.DevCenter.Index);
        },
        setupController: function(controller, model){
            var provider = new EntitlementProvider();
            provider.Get(Utils.GenerateRequestModel(null), function(resultData){
                if(resultData.data.status == 200){
                    var items = JSON.parse(resultData.data.data).results;

                    console.log("[DevCenter: setupController] dev items length: ", items.length);

                    if(items.length <= 0){
                        $("#ConfirmDialog").show();
                        controller.set("content.isDeveloper", false);
                    }else{
                        $("#ConfirmDialog").hide();
                        controller.set("content.isDeveloper", true);
                    }
                }else{
                    // TODO: Error
                }
            });
        }
    })
};