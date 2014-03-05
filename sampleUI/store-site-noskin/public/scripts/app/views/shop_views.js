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
      template: setting.GetView("/Cart", "cart"),
        didInsertElement: function(){
        }
    }),

    ProductView: Ember.View.extend({
      template: setting.GetView("/Product", "product"),
        didInsertElement: function(){

        }
    })
  };
});