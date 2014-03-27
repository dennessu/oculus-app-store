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
    HistoryItemProductsController: Ember.ObjectController.extend({
        product: function(){
            return this.store.find('Product', this.get('model').offer.id);
        }.property('model'),
        downloadLinksObserver: function(){
            var _self = this;

            if(this.get("model").type == "DIGITAL"){
                var provider = new CatalogProvider();
                provider.GetDownloadLinks(Utils.GenerateRequestModel({productId: this.get('model').offer.id}), function(resultData){
                    if(resultData.data.status == 200){
                        _self.set("downloadLinks", resultData.data.data);
                        //return resultData.data.data;
                    }else{
                        console.log("[HistoryItemProducts] Get download link error, ProductId: ", this.get('model').offer.id);
                    }
                });
            }
        }.property("model")
    }),
    PaymentController: Ember.ObjectController.extend({
        errMessage: null,
        content:{
            payments: []
        },
        actions:{
            AddPayment: function(){
                this.transitionToRoute("account.addpayment");
            },

            DelDialogYes: function(){
                var _self = this;
                var provider = new PaymentProvider();
                var paymentId = $("#SelectedPaymentId").val();
                provider.Del(Utils.GenerateRequestModel({paymentId: paymentId}), function(result){
                    if(result.data.status == 200){
                        _self.set("errMessage", null);
                        $("#DelPaymentDialog").hide();
                    }else{
                        _self.set("errMessage", "Please try again later!");
                    }
                });
            },

            DelDialogNo: function(){
                $("#DelPaymentDialog").hide();
            }
        }
    }),
    PaymentItemController: Ember.ObjectController.extend({
        cardDisplay: function(){
            var type = this.get("model").creditCardRequest.creditCardType;
            var number = this.get("model").accountNum;

            return type+" ****"+ number.substr(number.length - 4, 4);
        }.property('model'),

        expDisplay: function(){
            var expDate = new Date(this.get("model").creditCardRequest.expireDate);
            return (expDate.getMonth() + 1) + "/" + expDate.getFullYear();
        }.property('model'),

        actions:{
            DisplayDelDialog: function(){
                $("#SelectedPaymentId").val(this.get("model").self.id);
                $("#DelPaymentDialog").show();
            }
        }
    }),
    AddPaymentIndexController: Ember.ObjectController.extend({
        errMessage: null,
        isHolder: false,
        paymentTypes: (function(){
            var result = new Array();
            for(var i = 0; i < AppConfig.PaymentType.length; ++i) result.push({t: AppConfig.PaymentType[i].name, v: AppConfig.PaymentType[i].value});
            return result;
        }()),
        cardTypes: (function(){
            var result = new Array();
            for(var i = 0; i < AppConfig.CardType.CreditCard.length; ++i)
                result.push({t: AppConfig.CardType.CreditCard[i].name, v: AppConfig.CardType.CreditCard[i].value});
            return result;
        }()),
        paymentHolderType: (function(){
            var result = new Array();
            for(var i = 0; i < AppConfig.PaymentHolderType.length; ++i) result.push({t: AppConfig.PaymentHolderType[i].name, v: AppConfig.PaymentHolderType[i].value});
            return result;
        }()),
        countries: (function(){
            var result = new Array();
            for(var i = 0; i < AppConfig.Countries.length; ++i) result.push({t: AppConfig.Countries[i].name, v: AppConfig.Countries[i].value});
            return result;
        }()),
        months:(function(){
            var result = new Array();
            result.push({t: "Month", v: ""});
            for(var i = 1; i <= 12; ++i) result.push({t: i, v: i});
            return result;
        }()),
        years:(function(){
            var result = new Array();
            result.push({t: "Year", v: ""});
            for(var i = new Date().getFullYear(); i <= new Date().getFullYear() + 50; ++i) result.push({t: i, v: i});
            return result;
        }()),
        content: {
            paymentType: "",
            cardType: "",
            accountName: "",
            accountNum: "",
            isValidated: false,
            isDefault: false,
            expireDateYear: "",
            expireDateMonth: "",
            expireDate: "",
            encryptedCvmCode: "",
            addressLine1: "",
            city: "",
            state: "CA",
            country: "",
            postalCode: "",
            phoneType: "home",
            phoneNumber: "",

            relationship: "",
            holderPhoneNumber: "",
            email: ""
        },
        actions: {
            Continue: function () {
                var _self = this;
                var model = _self.get("content");

                model.expireDate = new Date(parseInt(_self.get("content.year")), parseInt(_self.get("content.month")) - 1);

                var provider = new PaymentProvider();
                provider.Add(Utils.GenerateRequestModel(model), function(resultData){
                    if(resultData.data.status == 200){
                        _self.set("errMessage", null);
                        _self.transitionToRoute("account.payment");
                    }else{
                        _self.set("errMessage", "Please try again later!");
                    }
                });
            },
            Cancel: function () {
                this.transitionToRoute("account.payment");
            }
        }
    }),
    ShippingController: Ember.ObjectController.extend({
        errMessage: null,
        content:{
            shippings: []
        },
        actions:{
            AddShipping: function(){
                this.transitionToRoute("account.addshipping");
            },

            DelDialogYes: function(){
                var _self = this;
                var provider = new BillingProvider();
                provider.Del(Utils.GenerateRequestModel({shippingId: $("#SelectedId").val()}), function(result){
                    if(result.data.status == 200){
                        _self.set("errMessage", null);
                        $("#DelDialog").hide();
                    }else{
                        _self.set("errMessage", "Please try again later!");
                    }
                });
            },

            DelDialogNo: function(){
                $("#DelDialog").hide();
            }
        }
    }),
    ShippingItemController: Ember.ObjectController.extend({
        actions:{
            DisplayDelDialog: function(){
                $("#SelectedId").val(this.get("model").self.id);

                $("#DelDialog").show();
            }
        }
    }),
    AddShippingIndexController: Ember.ObjectController.extend({
        errMessage: null,
        countries: (function(){
            var result = new Array();
            for(var i = 0; i < AppConfig.Countries.length; ++i) result.push({t: AppConfig.Countries[i].name, v: AppConfig.Countries[i].value});
            return result;
        }()),
        content:{
            country: "",
            firstName:"",
            lastName: "",
            street: "",
            city: "",
            state: "",
            postCode: "",
            phoneNumber: ""
        },

        actions: {
            Continue: function(){
                var _self = this;

                var dataProvider = new BillingProvider();
                dataProvider.Add(Utils.GenerateRequestModel(this.get("content")), function(result){
                    if(result.data.status == 200){
                        _self.set("errMessage", null);
                        _self.transitionToRoute('account.shipping');
                    }else{
                        _self.set("errMessage", "Please try again later!");
                    }
                });
            },
            Cancel: function(){
                this.transitionToRoute('account.shipping');
            }
        }
    })
};