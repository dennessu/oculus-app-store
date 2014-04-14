var DevCenterRoutes = {
    Index: Ember.Route.extend({
        beforeModel: function(){
            if(!Ember.App.AuthManager.isAuthenticated()){
                Utils.Cookies.Set(AppConfig.CookiesName.BeforeRoute, "devcenter");
                location.href = AppConfig.Runtime.LoginUrl;
                return;
            }

            Utils.GetViews(AppConfig.Templates.DevCenter.Index);
        }
    })
};