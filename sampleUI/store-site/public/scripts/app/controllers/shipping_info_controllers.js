
var ShippingInfoControllers = {
    IndexController: Ember.ObjectController.extend({

        actions: {
            CreateNew: function(){
                var _self = this;

                _self.transitionToRouteAnimated("shipping.edit", {shipping: "flip"});

            },
            Continue: function(){
                var _self = this;

                _self.transitionToRouteAnimated("shipping.edit", {shipping: "flip"});
            }
        }
    }),
    EditController: Ember.ObjectController.extend({
        actions: {
            Continue: function(){
                var _self = this;

                _self.transitionToRouteAnimated("payment", {main: "slideOverLeft"});

            },
            Cancel: function(){
                var _self = this;

                _self.transitionToRouteAnimated("shipping.index", {shipping: "flip"});
            }
        }
    })
};