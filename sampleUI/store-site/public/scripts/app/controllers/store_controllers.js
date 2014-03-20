
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

                if(_self.AuthManager.isAuthenticated()){

                }else{

                }
            }
        }
    })

};