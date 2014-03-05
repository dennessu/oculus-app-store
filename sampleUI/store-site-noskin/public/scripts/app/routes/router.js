define([
  'ember-load'
], function(Ember){

  var Router = Ember.Router.extend();

  Router.map(function(){
    this.resource('index', {path: '/'});
    this.resource('cart', {path: "/cart"});
    this.resource('product',{path: "/product/:product_id"});
  });

  return Router;
});