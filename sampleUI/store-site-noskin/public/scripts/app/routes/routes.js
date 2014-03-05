define([
  'ember-load',
  'app/config/auth_manager'

], function(Ember, auth){

  return {
    ApplicationRoute: Ember.Route.extend({
      init: function(){
          console.log("[ApplicationRoute]");

          this._super();
          Ember.App.AuthManager = auth.create();

          console.log("Is authenticated: " ,Ember.App.AuthManager.isAuthenticated());

          if(Ember.App.AuthManager.isAuthenticated()){
              console.log("Merge Cart");
              this.store.find("CartItem").then(function(records){
                  var localCartItems = new Array();

                  records.forEach(function(item){
                      localCartItems[localCartItems.length] = {
                          product_id: item.get("product_id"),
                          count: item.get("count")
                      };
                  });
                  records.invoke("deleteRecord");
                  records.invoke("save");

                  $.ajax({
                      type: "POST",
                      url: "/api/MergeCart",
                      async: false,
                      cache: false,
                      contentType: "application/json; charset=utf-8",
                      dataType: 'json',
                      data: JSON.stringify({"cartitems": localCartItems, user_id: Ember.App.AuthManager.getUserId()}),
                      success: function (data) {
                          return true;
                      }
                   });
            });
          }
      },
        actions: {
            logout: function() {

                this.transitionToRoute('index');
            }
        }
    }),

    ProductRoute: Ember.Route.extend({
        model: function(params) {
            var product = this.store.find('Product', params.product_id);
            console.log("[ProductRoute]Find Product. Id: ", params.product_id);
            return product;
        },
        renderTemplate: function(controller){
            this.render("product", {controller: controller});
        }
    }),
    CartRoute: Ember.Route.extend({
        beforeModel: function() {
            console.log("[CartRoute]before model");

            var _self = this;
            if(Ember.App.AuthManager.isAuthenticated()){
                var resData = "";
                $.ajax({
                    type: "GET",
                    url: "/api/cartitems?userId=" + Ember.App.AuthManager.getUserId(),
                    async: false,
                    cache: false,
                    success: function (data) {
                        resData = JSON.parse(data.data);
                        for (var i = 0; i < resData.CartItems.length; ++i) {
                            var item = resData.CartItems[i].CartItem;
                            _self.store.push("CartItem", {
                                id: item.id,
                                product_id: item.product_id,
                                unit_price: item.unit_price,
                                count: item.count,
                                status: true
                            });
                            console.log("[CartRoute]Push to Store, Product Id: " + item.product_id);
                        }
                    }});

                _self.store.findAll('CartItem').then(function(cartItemRecords){
                    cartItemRecords.forEach(function(ci){
                        _self.store.find("Product", ci.get("product_id")).then(function(productRecords){
                            ci.set("unit_price", productRecords.get('price'));
                        });
                    });
                });
            }
        },
        model: function(){
            console.log("[CartRoute][model]");

            return this.store.findAll('CartItem');
        }

    }),
    CartIndexRoute: Ember.Route.extend({
        model: function(){
            return this.modelFor('model');
        }
    })
  };

});