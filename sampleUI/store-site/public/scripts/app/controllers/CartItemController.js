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
          return model.get("count") * model.get("unit_price");
      }.property('model.count', 'model.unit_price'),

      actions:{
        changeCount: function(value){
            if(value != undefined){
                if(value <= 1) this.set("model.count", 1);
                console.log("Current Item count: ", this.get("model.count"));
                this.get("model").save();

                if(Ember.App.AuthManager.isAuthenticated()){
                    var userId = Ember.App.AuthManager.getUserId();

                    $.post("/api/AddCartItem", { userId: userId, productId: this.get("model.product_id"), count:this.get("model.count") } );
                }
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