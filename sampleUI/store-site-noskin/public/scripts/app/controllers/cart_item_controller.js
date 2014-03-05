define([
  'ember-load',
  'app-setting'
], function(Ember, setting){

  return  Ember.ObjectController.extend({
      product: function(){
          return this.store.find('Product', this.get('model').get('product_id'));
      }.property('model'),

      itemSubtotal: function(){
          var model = this.get("model");
          var count = model.get("count");

          return (count > 0 ? count : 1) * model.get("unit_price");
      }.property('model.count', 'model.unit_price'),

      changeQty: function(){
          var qty = this.get("model.count");
          if(!isNaN(qty) && qty > 0){

          }else{
              this.set("model.count", 1);
          }
      }.observes('model.count'),

      actions:{

          change: function(value){
              if(value != undefined && !isNaN(value) && value > 0){
                  if(Ember.App.AuthManager.isAuthenticated()){
                      var userId = Ember.App.AuthManager.getUserId();
                      $.post("/api/UPdateCartItem", { userId: userId, productId: this.get("model.product_id"), count:qty } );
                  }
              }else{
                  this.set("model.count", 1);
              }
          },

          removeItem: function(){
              var item = this.get("model");
              var productId = item.get("product_id");
              item.deleteRecord();
              item.save();

              if(Ember.App.AuthManager.isAuthenticated()){
                  var userId = Ember.App.AuthManager.getUserId();

                  $.post("/api/RemoveCartItem", { userId: userId, productId: productId} );
              }
          }
      }
  });
});