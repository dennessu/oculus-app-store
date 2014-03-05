define([
  'ember-load',
  'app-setting'
], function(Ember, setting){

  return  Ember.ArrayController.extend({

      subTotal: function(){
          var _self = this;

          var subtotal = 0;
          this.forEach(function(item, index, enumerable){
              subtotal += item.get("count") * item.get("unit_price");
          });
          return subtotal;

      }.property("@each.count", "@each.unit_price"),

      totalCount: function(){
          return this.getEach("count").reduce(function(previousValue, item, index, enumerable){
              console.log("Previous Value:" + previousValue);
            return previousValue + item;
          });
      }.property("@each.count")
  });
});