define([
  'ember-load',
  'app-setting'
], function(Ember, setting, g){

  return {
    IndexView: Ember.View.extend({
        templateName : "index",
        didInsertElement: function(){

        }
    }),

    CartView: Ember.View.extend({
      templateName: "cart",
        didInsertElement: function(){
        }
    }),

    ProductView: Ember.View.extend({
      templateName: "product",
        didInsertElement: function(){

        }
    })
  };
});