define([
    'ember-load'
], function(DS){

    var APIKey = Ember.Object.extend({
        access_token: '',
        user_id: ''
    });

    return APIKey;
});