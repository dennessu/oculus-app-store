define([
  'ember-load',
  'app-setting'
], function(Ember, setting, g){

  return {
    IndexView: Ember.View.extend({
        templateName : "index",
        didInsertElement: function(){
            $("body").attr("class", "body-bg-1");
        }
    }),

    CartView: Ember.View.extend({
      template: setting.GetView("/Cart", "cart"),
        didInsertElement: function(){
            $("body").attr("class", "body-bg-5");
        }
    }),

    ProductView: Ember.View.extend({
      template: setting.GetView("/Product", "product"),
        didInsertElement: function(){
            $("body").attr("class", "body-bg-2");
        }
    })
  };
});