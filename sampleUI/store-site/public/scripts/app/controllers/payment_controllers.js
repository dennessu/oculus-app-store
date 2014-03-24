
var PaymentControllers = {
    IndexController: Ember.ArrayController.extend({
        actions: {
            CreateNew: function(){
                var _self = this;

                _self.transitionToRouteAnimated("payment.edit", {payment: "flip"});

            },
            Continue: function(){
                var _self = this;

                _self.transitionToRouteAnimated("payment.edit", {payment: "flip"});
            }
        }
    }),
    EditController: Ember.ObjectController.extend({
        actions: {
            Continue: function () {
                var _self = this;

                _self.transitionToRouteAnimated("ordersummary", {main: "slideOverLeft"});

            },
            Cancel: function () {
                var _self = this;

                _self.transitionToRouteAnimated("payment.index", {payment: "flip"});
            }
        }

    })
};