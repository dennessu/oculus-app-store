var AccountControllers = {
    IndexController: Ember.ObjectController.extend({
        content:{
            firstName:"",
            lastName: ""
        }
    }),
    EditInfoController: Ember.ObjectController.extend({
        content:{
          firstName: "",
          lastName: "",
          isOptInPromotion: false,
          isOptInNewsWeekly: false
        },
        actions:{
            SaveChanges: function(){
                var _self = this;
                // save profile
                var profileData = {firstName: _self.get("content.firstName"), lastName: _self.get("content.lastName")};
                var provider = new IdentityProvider();
                provider.PutProfile(Utils.GenerateRequestModel(profileData), function(resultData){
                    if(resultData.data.status == 200){
                        if(_self.get("content.isOptInPromotion")){
                            provider.PostOptIns(Utils.GenerateRequestModel({optin: "promotion"}), function(resultData){

                            });
                        }
                        if(_self.get("content.isOptInNewsWeekly")){
                            provider.PostOptIns(Utils.GenerateRequestModel({optin: "newsweekly"}), function(resultData){

                            });
                        }
                        console.log("[EditInfoController: SaveChanges] success");
                    }else{
                        //TODO: Error
                        console.log("[EditInfoController: SaveChanges] Failed");
                    }
                });
            },
            Cancel: function(){
                this.transitionToRoute("account.index");
            }
        }
    }),
    EditPasswordController: Ember.ObjectController.extend({
        errMessage: null,
        content:{
            password: ""
        },
        actions:{
            SaveChanges: function(){
                var _self = this;
                var provider = new IdentityProvider();
                provider.PutUser(Utils.GenerateRequestModel({password: _self.get("content.password")}), function(resultData){
                    if(resultData.data.status == 200){
                        _self.set("errMessage", null);
                        console.log("[EditPasswordController:SaveChanges] success");
                        _self.transitionToRoute("account.index");
                    }else{
                        // TODO: Error
                        console.log("[EditPasswordController:SaveChanges] failed!");
                        _self.set("errMessage", "Please try again later!");
                    }
                });

            },
            Cancel: function(){
                this.transitionToRoute("account.index");
            }
        }
    }),
    ProfileController: Ember.ObjectController.extend({}),
    EditProfileController: Ember.ObjectController.extend({
        actions:{
            SaveChanges: function(){

            },
            Cancel: function(){
                this.transitionToRoute("account.profile");
            }
        }
    }),
    HistoryController: Ember.ObjectController.extend({
        content:{
            orders: new Array()
        }
    }),
    HistoryItemController: Ember.ObjectController.extend({
        content:{
            products: new Array()
        },
        subTotal: function(){
            var result = 0;
            var products = this.get("model").orderItems;
            for(var i = 0; i < products.length; ++i){
                result+= products[i].totalAmount;
            }
            return result
        }.property("content.products")
    }),
    HistoryItemProducts: Ember.ObjectController.extend({
        product: function(){
            return this.store.find('Product', this.get('model').offer.id);
        }.property('model')

    }),
    PaymentController: Ember.ObjectController.extend({})
};