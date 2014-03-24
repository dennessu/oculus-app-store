
var StoreControllers = {
    ApplicationController: Ember.ObjectController.extend({
        username: function() {
            return App.AuthManager.getUsername();
        }.property('App.AuthManager.AuthKey'),

        isAuthenticated: function() {
            return App.AuthManager.isAuthenticated();
        }.property('App.AuthManager.AuthKey')
    }),

    DetailController: Ember.ObjectController.extend({

        actions:{
            AddToCart: function(){
                var _self = this;
                console.log("[DetailController:AddToCart]");
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
                        selected: true,
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
                subtotal += item.get("subTotal");
            });
            return subtotal;

        }.property("@each.qty", "@each.subTotal"),

        totalCount: function(){
            return this.getEach("qty").reduce(function(previousValue, item, index, enumerable){
                return previousValue + item;
            });
        }.property("@each.qty"),

        actions: {
            Checkout: function(){
                var _self = this;
                if(App.AuthManager.isAuthenticated()){
                    console.log("[CartController:CheckOut]");
                    // TODO: Call PostOrder
                    _self.transitionToRouteAnimated("address", {main: "slideOverLeft"});
                }else{
                    Utils.Cookies.Set(AppConfig.CookiesName.BeforeRoute, "cart");

                    location.href = AppConfig.LoginUrl;
                }
            }
        }

    }),

    CartItemController: Ember.ObjectController.extend({
        product: function(){
            return this.store.find('Product', this.get('model').get('product_id'));
        }.property('model'),

        itemSubtotal: function(){
            var model = this.get("model");
            var count = model.get("qty");
            var subTotal = (count > 0 ? count : 1) * this.get("product.price");
            this.set("model.subTotal", subTotal);

            return subTotal;
        }.property('model.qty', 'product.price'),

        changeQty: function(){
            var qty = this.get("model.qty");
            if(!isNaN(qty) && qty > 0){

            }else{
                this.set("model.qty", 1);
            }
        }.observes('model.qty'),

        changeStatus: function(){
            var _self = this;

            var data = {"cart_items": [{
                product_id: _self.get("model.product_id"),
                selected: _self.get("model.selected"),
                qty: _self.get("model.qty")
            }]};

            var provider = new CartProvider();
            provider.Update(Utils.GenerateRequestModel(data), function(resultData){
                var resultModel = resultData.data;
                if (resultModel.status == 200) {
                    console.log("[CartItemController:Change Status] Success");
                    _self.set("errMessage", null);
                } else {
                    console.log("[CartItemController:Change Status] Failed!");
                    _self.set("errMessage", Utils.GetErrorMessage(resultModel));

                    //TODO: ?
                }
            });
        }.observes('model.selected'),

        actions: {
            Change: function(value){
                var _self = this;

                if(value != undefined && !isNaN(value) && value > 0){
                    var productId = _self.get("model.product_id");

                    var data = {"cart_items": [{
                        product_id: productId,
                        selected: _self.get("model.selected"),
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

            RemoveItem: function(){
                var _self = this;
                var item = this.get("model");
                var productId = item.get("product_id");

                var data = {"cart_items": [{
                    product_id: productId,
                    selected: _self.get("model.selected"),
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