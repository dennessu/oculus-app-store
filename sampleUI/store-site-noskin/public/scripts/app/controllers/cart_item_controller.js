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
                      var productId = this.get("model.product_id");

                      $.ajax({
                          type: "POST",
                          url: "/api/UPdateCartItem",
                          async: true,
                          cache: false,
                          contentType: "application/json; charset=utf-8",
                          dataType: 'json',
                          data: JSON.stringify({"cartitems": [{ product_id: productId, count: value}], user_id: userId}),
                          success: function (data) {
                              return true;
                          }
                      });
                  }else{
                      this.get('model').save();
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

                  $.ajax({
                      type: "POST",
                      url: "/api/RemoveCartItem",
                      async: true,
                      cache: false,
                      contentType: "application/json; charset=utf-8",
                      dataType: 'json',
                      data: JSON.stringify({"cartitems": [{ product_id: productId, count: 1}], user_id: Ember.App.AuthManager.getUserId()}),
                      success: function (data) {
                          return true;
                      }
                  });
              }
          }
      }
  });
});