define([
  'ember-load'
], function(Ember){

  var Router = Ember.Router.extend();

  Router.map(function(){
    this.resource('index', {path: "/"});
    this.resource('login');
    this.resource('register');
    this.resource('captcha');
  });

  return Router;
});