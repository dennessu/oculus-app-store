define([
  'ember-load',
  'app-setting'
], function(Ember, setting){

  return Ember.ObjectController.extend({

    actions:{
      AddToCart: function(){
          console.log('[ProductController][AddToCart]');

          var _self = this;
          var currentId = _self.get('model').get('id');
          var defaultCount = 1;

          _self.store.find('CartItem', {product_id: currentId}).then(function(records){

              var isExists = false;
              records.forEach(function(item) {
                  console.log("Find Product Id:", item.get('product_id'));

                  if(item != undefined){
                      if(item.get('product_id') === currentId){
                          isExists = true;
                          defaultCount = parseInt(item.get('count')) + 1;
                          item.set('count', defaultCount);
                          item.save();

                          console.log("Current cart item count: ", item.get('count'));
                          return;
                      }
                  }
              });

              if(!isExists && !Ember.App.AuthManager.isAuthenticated()){
                  var record = _self.store.createRecord('CartItem', {
                      product_id: currentId,
                      count: defaultCount,
                      unit_price: _self.get("model.price"),
                      status: true
                  });
                  record.save();

                  console.log("Add a new cart item. Product Id: ", _self.get('model').get('id'));
              }

              if(Ember.App.AuthManager.isAuthenticated()){
                  var userId = Ember.App.AuthManager.getUserId();

                  $.ajax({
                      type: "POST",
                      url: "/api/addcartitem",
                      async: false,
                      cache: false,
                      data: { userId: userId, productId: currentId, count: defaultCount},
                      success: function (data) {
                          _self.transitionToRoute('cart');
                          return true;
                      },
                      error: function(){
                          _self.transitionToRoute('cart');
                          return true;
                      }
                  });
              }else{
                  _self.transitionToRoute('cart');
                  return true;
              }
          });
      }
    }
  });

});