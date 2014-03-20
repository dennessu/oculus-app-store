
var StoreControllers = {

    DetailController: Ember.ObjectController.extend({

        actions:{
            AddToCart: function(){
                var _self = this;

                /*
                    authentication
                        add to user cart
                    no authentication
                        create au anonymous user
                        create an anonymous cart
                        add to anonymous cart

                    transition to shopping cart
                 */

                if(Ember.App.AuthManager.isAuthenticated()){
                    _self.send("AddToCartHandler");
                }else{
                    if(Ember.App.AuthManager.getUserId() == null || Ember.App.AuthManager.getUserId() == ""){
                        //create au anonymous user
                        var provider = new IdentityProvider();

                        provider.GetAnonymousUser(Utils.GenerateRequestModel(null), function (data) {
                            var resultModel = data.data;
                            if (resultModel.status == 200) {
                                console.log("[DetailController:AddToCart] Create anonymous user success!");
                                _self.set("errMessage", null);
                                _self.send("AddToCartHandler");

                            } else {
                                console.log("[DetailController:AddToCart] Create anonymous user Failed!");
                                _self.set("errMessage", Utils.GetErrorMessage(resultModel));
                            }
                        });
                    }else{
                        // add to cart
                        _self.send("AddToCartHandler");
                    }
                }
            },

            AddToCartHandler: function(){
                console.log("[DetailController:AddToCartHandler]");

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
                                defaultCount = parseInt(item.get('qty')) + 1;
                                item.set('qty', defaultCount);
                                item.save();

                                console.log("Current cart item count: ", item.get('qty'));
                                return;
                            }
                        }
                    });

                    var provider = new CartProvider();

                    var data = {"cart_items": [{
                        product_id: currentId,
                        qty: defaultCount
                    }]};

                    provider.Add(Utils.GenerateRequestModel(data), function(resultData){
                        var resultModel = resultData.data;
                        if (resultModel.status == 200) {
                            console.log("[DetailController:AddToCartHandler] Success");
                            _self.set("errMessage", null);

                            _self.transitionToRouteAnimated("cart", {main: "slideOverLeft"});

                        } else {
                            console.log("[DetailController:AddToCartHandler] Failed!");
                            _self.set("errMessage", Utils.GetErrorMessage(resultModel));

                            //TODO: ?
                        }
                    });
                });
            }
        }
    }),

    CartController: Ember.ArrayController.extend({
        subTotal: function(){
            var _self = this;

            var subtotal = 0;
            this.forEach(function(item, index, enumerable){
                //var p = _self.store.find("Product", item.get("product_id"));

                subtotal += item.get("qty") * item.get("price");
            });
            return subtotal;

        }.property("@each.qty", "@each.price"),

        totalCount: function(){
            return this.getEach("qty").reduce(function(previousValue, item, index, enumerable){
                return previousValue + item;
            });
        }.property("@each.qty")

    }),

    CartItemController: Ember.ObjectController.extend({
        product: function(){
            return this.store.find('Product', this.get('model').get('product_id'));
        }.property('model'),

        itemSubtotal: function(){
            var model = this.get("model");
            var count = model.get("qty");
            var price = (count > 0 ? count : 1) * this.get("product.price");
            this.set("model.price", price);

            return price;
        }.property('model.qty', 'product.price'),

        changeQty: function(){
            var qty = this.get("model.qty");
            if(!isNaN(qty) && qty > 0){

            }else{
                this.set("model.qty", 1);
            }
        }.observes('model.qty'),

        actions: {
            change: function(value){
                var _self = this;

                if(value != undefined && !isNaN(value) && value > 0){
                    var productId = this.get("model.product_id");

                    var data = {"cart_items": [{
                        product_id: productId,
                        qty: value
                    }]};

                    var provider = new CartProvider();
                    provider.Update(Utils.GenerateRequestModel(data), function(resultData){
                        var resultModel = resultData.data;
                        if (resultModel.status == 200) {
                            console.log("[CartItemController:change] Success");
                            _self.set("errMessage", null);
                        } else {
                            console.log("[CartItemController:change] Failed!");
                            _self.set("errMessage", Utils.GetErrorMessage(resultModel));

                            //TODO: ?
                        }
                    });
                }
            },

            removeItem: function(){
                var _self = this;
                var item = this.get("model");
                var productId = item.get("product_id");

                var data = {"cart_items": [{
                    product_id: productId,
                    qty: 1
                }]};

                var provider = new CartProvider();
                provider.Remove(Utils.GenerateRequestModel(data), function(resultData){
                    var resultModel = resultData.data;
                    if (resultModel.status == 200) {
                        console.log("[CartItemController:removeItem] Success");
                        _self.set("errMessage", null);
                    } else {
                        console.log("[CartItemController:removeItem] Failed!");
                        _self.set("errMessage", Utils.GetErrorMessage(resultModel));

                        //TODO: ?
                    }
                });
            }
        }
    })


};