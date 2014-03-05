define([
  'ember-load'
], function(Ember){

  return {
    IndexRoute: Ember.Route.extend({
      renderTemplate: function () {
        this.render("login");
      }
    }),
    LoginRoute: Ember.Route.extend({}),
    RegisterRoute: Ember.Route.extend({}),
    CaptchaRoute: Ember.Route.extend({})
  };
});