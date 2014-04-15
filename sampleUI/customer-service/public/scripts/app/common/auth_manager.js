var AuthManager = Ember.Object.extend({
    init: function () {
        this._super();

        var accessToken = Utils.Cookies.Get(AppConfig.CookiesName.AccessToken);
        var authUserId = Utils.Cookies.Get(AppConfig.CookiesName.UserId);

        if (!Ember.isEmpty(accessToken) && !Ember.isEmpty(authUserId)) {
            this.authenticate(accessToken, authUserId);
        }
    },
    isAuthenticated: function () {
        return !Ember.isEmpty(this.get('AuthKey.access_token')) && !Ember.isEmpty(this.get('AuthKey.user_id'));
    },
    getUserId: function () {
        if(this.isAuthenticated()){
            return this.get('AuthKey.user_id');
        }else{
            return Utils.Cookies.Get(AppConfig.CookiesName.AnonymousUserId);
        }
    },
    getCartId: function(){
        if(this.isAuthenticated()){
            return Utils.Cookies.Get(AppConfig.CookiesName.CartId);
        }else{
            return Utils.Cookies.Get(AppConfig.CookiesName.AnonymousCartId);
        }
    },
    getUsername: function(){
        return Utils.Cookies.Get(AppConfig.CookiesName.Username);
    },
    authenticate: function (accessToken, userId) {
        /*
         $.ajaxSetup({
         headers:{'Authorization': accessToken}
         });
         var user = Ember.App.User.find(userId);
         */
        this.set('AuthKey', App.AuthKey.create({
            access_token: accessToken,
            user_id: userId
        }));
    },

    reset: function () {
        /*
         Ember.App.__container__.lookup("route:application").transitionToRoute('index');
         Ember.run.sync();
         Ember.run.next(this, function(){
         this.set('APIKey', null);


         $.ajaxSetup({
         headers: {'Authorization' : ''}
         });

         });
         */
        this.set('AuthKey', null);
    },

    apiKeyObserver: function () {
        if (Ember.isEmpty(this.get('AuthKey'))) {
            Utils.Cookies.Remove(AppConfig.CookiesName.AccessToken);
            Utils.Cookies.Remove(AppConfig.CookiesName.IdToken);
            Utils.Cookies.Remove(AppConfig.CookiesName.UserId);
            Utils.Cookies.Remove(AppConfig.CookiesName.Username);
            Utils.Cookies.Remove(AppConfig.CookiesName.CustomerId);
        } else {
            Utils.Cookies.Set(AppConfig.CookiesName.AccessToken, this.get('AuthKey.access_token'));
            Utils.Cookies.Set(AppConfig.CookiesName.UserId, this.get('AuthKey.user_id'));
        }
    }.observes('AuthKey')
});

DS.rejectionHandler = function (reason) {
    if (reason.status === 401) {
        Ember.App.AuthManager.reset();
    }
    throw reason;
}
