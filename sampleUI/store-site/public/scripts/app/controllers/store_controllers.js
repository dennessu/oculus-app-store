
var StoreControllers = {
    ApplicationController: Ember.ObjectController.extend({
        username: function() {
            return App.AuthManager.getUsername();
        }.property('App.AuthManager.AuthKey'),

        isAuthenticated: function() {
            return App.AuthManager.isAuthenticated();
        }.property('App.AuthManager.AuthKey')
    }),

    IndexController: Ember.ObjectController.extend({
       content:{
           products: []
       }
    }),

    DetailController: Ember.ObjectController.extend({

        actions:{
            AddToCart: function(){

                if($("#BtnAddToCart").hasClass('load')) return;
                $("#BtnAddToCart").addClass('load');

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

                    provider.AddCartItem(Utils.GenerateRequestModel(data), function(resultData){
                        var resultModel = resultData.data;
                        if (resultModel.status == 200) {
                            console.log("[DetailController:AddToCartHandler] Success");
                            _self.set("errMessage", null);

                            _self.transitionToRouteAnimated("cart", {main: "slideOverLeft"});

                        } else {
                            console.log("[DetailController:AddToCartHandler] Failed!");
                            _self.set("errMessage", Utils.GetErrorMessage(resultModel));
                            $("#BtnAddToCart").removeClass('load');

                            //TODO: ?
                        }
                    });
                });
            },

            Buy: function(){
                if($("#BtnBuy").hasClass('load')) return;
                $("#BtnBuy").addClass('load');

                console.log("[DetailController:Buy]");
            }
        }
    }),

    CartController: Ember.ArrayController.extend({
        subTotal: function(){
            var _self = this;

            var subtotal = 0;
            this.forEach(function(item, index, enumerable){
                if(item.get("selected") == true) {
                    subtotal += item.get("subTotal");
                }
            });
            return Utils.FormatNumber(subtotal, 2, ",", 3);

        }.property("@each.qty", "@each.subTotal", "@each.selected"),

        totalCount: function(){
            return this.getEach("qty").reduce(function(previousValue, item, index, enumerable){
                return previousValue + item;
            });
        }.property("@each.qty"),

        statusChange: function(){
            var _self = this;
            var cartItmes = new Array();

            this.forEach(function(item, index, enumerable){
                cartItmes.push({
                    product_id: item.get("product_id"),
                    selected: item.get("selected"),
                    qty: item.get("qty")
                });
            });

            var data = {"cart_items": cartItmes};

            var provider = new CartProvider();
            provider.UpdateCartItem(Utils.GenerateRequestModel(data), function(resultData){
                var resultModel = resultData.data;
                if (resultModel.status == 200) {
                    console.log("[CartItemController:Change Status] Success");
                    _self.set("errMessage", null);
                } else {
                    console.log("[CartItemController:Change Status] Failed!");
                    _self.set("errMessage", Utils.GetErrorMessage(resultModel));
                }
            });
        }.property("@each.selected"),

        actions: {
            Checkout: function(){

                if($("#BtnCheckout").hasClass('load')) return;
                $("#BtnCheckout").addClass('load');

                var _self = this;
                var hasSelected = false;
                _self.get("model").forEach(function(item){
                    if(item.get("selected") == true){
                        hasSelected = true;
                    }
                });
                if(!hasSelected){
                    alert("Please choose an items!");
                    $("#BtnCheckout").removeClass('load');
                    return;
                }

                if(App.AuthManager.isAuthenticated()){
                    console.log("[CartController:CheckOut]");

                    var dataProvider = new CartProvider();
                    dataProvider.PostOrder(Utils.GenerateRequestModel(null), function(result){
                        var resultModel = result.data;
                        if(resultModel.status == 200){
                            console.log("[CartController:Checkout] Post order success");
                            var order = JSON.parse(resultModel.data);

                            // set order id to cookie
                            Utils.Cookies.Set(AppConfig.CookiesName.OrderId, order.self.id);
                            // clear shipping method and shipping address cookies
                            Utils.Cookies.Remove(AppConfig.CookiesName.ShippingMethodId);
                            Utils.Cookies.Remove(AppConfig.CookiesName.ShippingId);


                            _self.get("model").forEach(function(item){
                                item.deleteRecord();
                                item.save();
                            });


                            var allDigital = true;
                            for(var i = 0; i < order.orderItems.length; ++i){
                                if(order.orderItems[i].type == "PHYSICAL"){
                                    allDigital = false;
                                }
                            }
                            if(allDigital){
                                _self.transitionToRouteAnimated("payment", {main: "slideOverLeft"});
                            }else{
                                _self.transitionToRouteAnimated("shipping", {main: "slideOverLeft"});
                            }

                        }else{
                            console.log("[CartController:Checkout] Post order failed!");
                            $("#BtnCheckout").removeClass('load');
                            // TODO: do something
                        }
                    });
                }else{
                    Utils.Cookies.Set(AppConfig.CookiesName.BeforeRoute, "cart");
                    location.href = AppConfig.Runtime.LoginUrl;
                    return;
                }
            }
        }
    }),

    CartItemController: Ember.ObjectController.extend({
        product: function(){
            console.log("Cart Item Product Id: ", this.get('model').get('product_id'));
            return this.store.find('Product', this.get('model').get('product_id'));
        }.property('model'),

        itemSubtotal: function(){
            var model = this.get("model");
            var count = model.get("qty");
            var subTotal = (count > 0 ? count : 1) * this.get("product.price");
            this.set("model.subTotal", subTotal);

            return Utils.FormatNumber(subTotal, 2, ",", 3);
        }.property('model.qty', 'product.price'),

        changeQty: function(){
            var qty = this.get("model.qty");
            if(!isNaN(qty) && qty > 0){

            }else{
                this.set("model.qty", 1);
            }
        }.observes('model.qty'),

        actions: {
            ChangeCount: function(value){
                var _self = this;

                if(value != undefined && !isNaN(value) && value > 0){
                    var productId = _self.get("model.product_id");

                    var data = {"cart_items": [{
                        product_id: productId,
                        selected: _self.get("model.selected"),
                        qty: value
                    }]};

                    var provider = new CartProvider();
                    provider.UpdateCartItem(Utils.GenerateRequestModel(data), function(resultData){
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
                provider.RemoveCartItem(Utils.GenerateRequestModel(data), function(resultData){
                    var resultModel = resultData.data;
                    if (resultModel.status == 200) {
                        console.log("[CartItemController:removeItem] Success");

                        item.deleteRecord();
                        item.save();
                    } else {
                        console.log("[CartItemController:removeItem] Failed!");
                        //TODO: ?
                    }
                });
            }
        }
    }),

    OrderSummaryController: Ember.ObjectController.extend({
        content: {
            products: new Array(),
            paymentMethodName: "",
            shippingMethodName: "",
            shippingAddress: null
        },

        subTotal: function(){
            var result = 0;
            this.get("content.products").forEach(function(item){
                result+= item.subTotal;
            });
            return Utils.FormatNumber(result, 2, ",", 3);
        }.property("content.products"),

        total: function(){
            //console.log("Sum Total", this.get("content.totalAmount"), " ", this.get("content.totalTax"));
            return parseFloat(this.get("content.totalAmount")) + parseFloat(this.get("content.tax"));
        }.property("content.products"),

        actions:{
            Purchase: function(){

                if($("#BtnPurchase").hasClass('load')) return;
                $("#BtnPurchase").addClass('load');

                var _self = this;

                var cartProvider = new CartProvider();
                cartProvider.PurchaseOrder(Utils.GenerateRequestModel(null), function(resultData){
                    if(resultData.data.status == 200){
                        try {
                            _self.get('store').unloadAll(App.CartItem);
                        }catch(e){}

                        _self.transitionToRouteAnimated("thanks", {main: "slideOverLeft"});
                    } else{
                        $("#BtnPurchase").removeClass('load');
                        // TODO: Show Error
                    }
                });
            }
        }
    }),

    OrderSummaryItemController: Ember.ObjectController.extend({
        product: function(){
            return this.store.find('Product', this.get('model').id);
        }.property('model')
    }),

    ThanksController: Ember.ObjectController.extend({
        actions:{
            Continue: function(){
                this.transitionToRouteAnimated("index", {main: "slideOverLeft"});
            }
        }
    })
};