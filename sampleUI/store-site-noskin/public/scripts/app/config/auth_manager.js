define([
    'ember-load',
   'ember-data-load',
    'app/config/cookies',
    'app/models/api_key',
    'app/models/user_model'
], function(Ember, DS, Cookies, APIKey, User){

    var AuthManager = Ember.Object.extend({
        init: function(){
            this._super();

            var accessToken = Cookies.Get('access_token');
            var authUserId = Cookies.Get('user_id');

            if(!Ember.isEmpty(accessToken) && !Ember.isEmpty(authUserId)){
                this.authenticate(accessToken, authUserId);
            }
        },

        isAuthenticated: function(){
            return !Ember.isEmpty(this.get('APIKey.access_token')) && !Ember.isEmpty(this.get('APIKey.user_id'));
        },

        getUserId: function(){
          return this.get('APIKey.user_id');
        },

        authenticate: function(accessToken, userId){
            /*
            $.ajaxSetup({
                headers:{'Authorization': accessToken}
            });
            var user = Ember.App.User.find(userId);
             */
            this.set('APIKey', Ember.App.APIKey.create({
                access_token: accessToken,
                user_id: userId
            }));
        },

        reset: function(){
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
        },

        apiKeyObserver: function(){
            if(Ember.isEmpty(this.get('APIKey'))){
                Cookies.Remove('access_token');
                Cookies.Remove('user_id');
            }else{
                Cookies.Set('access_token', this.get('APIKey.access_token'));
                Cookies('user_id', this.get('APIKey.user_id'));
            }
        }.observes('APIKey')
    });

    DS.rejectionHandler = function(reason){
        if(reason.status === 401){
            Ember.App.AuthManager.reset();
        }
        throw reason;
    }

    return AuthManager;
});