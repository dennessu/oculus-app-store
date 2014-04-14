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
                if($("#BtnSaveSetting").hasClass('load')) return;
                $("#BtnSaveSetting").addClass('load');

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

                        _self.transitionToRoute("account.index");
                    }else{
                        //TODO: Error
                        console.log("[EditInfoController: SaveChanges] Failed");
                        $("#BtnSaveSetting").removeClass('load');
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

                if($("#BtnSavePassword").hasClass('load')) return;
                $("#BtnSavePassword").addClass('load');

                var _self = this;
                var provider = new IdentityProvider();
                provider.RestPassword(Utils.GenerateRequestModel({password: _self.get("content.password")}), function(resultData){
                    if(resultData.data.status == 200){
                        _self.set("errMessage", null);
                        console.log("[EditPasswordController:SaveChanges] success");
                        _self.transitionToRoute("account.index");
                    }else{
                        // TODO: Error
                        console.log("[EditPasswordController:SaveChanges] failed!");
                        _self.set("errMessage", "Please try again later!");
                        $("#BtnSavePassword").removeClass('load');
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
        }.property("content.products"),

        total: function(){
            return parseFloat(this.get("model.totalAmount")) + parseFloat(this.get("model.totalTax"));
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
                provider.DeletePayment(Utils.GenerateRequestModel({paymentId: paymentId}), function(result){
                    if(result.data.status == 200) {
                        _self.set("errMessage", null);
                        $("#DelPaymentDialog").hide();
                        var provider = new PaymentProvider();
                        provider.GetPayments(Utils.GenerateRequestModel(null), function (result) {
                            if (result.data.status == 200) {
                                var payments = JSON.parse(result.data.data).results;
                                _self.set("content.payments", payments);
                            } else {
                                _self.set("content.payments", []);
                            }
                        });
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
            var type = this.get("model").creditCardRequest.type;
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
            for(var i = 0; i < AppConfig.PaymentTypes.length; ++i) result.push({t: AppConfig.PaymentTypes[i].name, v: AppConfig.PaymentTypes[i].value});
            return result;
        }()),
        cardTypes: (function(){
            var result = new Array();
            for(var i = 0; i < AppConfig.CardTypes.CreditCard.length; ++i)
                result.push({t: AppConfig.CardTypes.CreditCard[i].name, v: AppConfig.CardTypes.CreditCard[i].value});
            return result;
        }()),
        paymentHolderType: (function(){
            var result = new Array();
            for(var i = 0; i < AppConfig.PaymentHolderTypes.length; ++i) result.push({t: AppConfig.PaymentHolderTypes[i].name, v: AppConfig.PaymentHolderTypes[i].value});
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
            state: "",
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

                if($("#BtnPayment").hasClass('load')) return;
                $("#BtnPayment").addClass('load');

                var _self = this;
                var model = _self.get("content");

                model.expireDate = new Date(parseInt(_self.get("content.year")), parseInt(_self.get("content.month")) - 1);

                var provider = new PaymentProvider();
                provider.PostPayment(Utils.GenerateRequestModel(model), function(resultData){
                    if(resultData.data.status == 200){
                        _self.set("errMessage", null);
                        _self.transitionToRoute("account.payment");
                    }else{
                        _self.set("errMessage", "Please try again later!");
                        $("#BtnPayment").removeClass('load');
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
                provider.DeleteShippingInfo(Utils.GenerateRequestModel({shippingId: $("#SelectedId").val()}), function(result){
                    if(result.data.status == 200){
                        _self.set("errMessage", null);
                        $("#DelDialog").hide();
                        var provider = new BillingProvider();
                        provider.GetShippingInfos(Utils.GenerateRequestModel(null), function(result){
                            if(result.data.status == 200){
                                var shippings = JSON.parse(result.data.data).results;
                                _self.set("content.shippings", shippings);
                            }else{
                                console.log("Can't get the shippings");
                            }
                        });
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
            postalCode: "",
            phoneNumber: ""
        },

        actions: {
            Continue: function(){

                if($("#BtnShippingAddress").hasClass('load')) return;
                $("#BtnShippingAddress").addClass('load');

                var _self = this;

                var dataProvider = new BillingProvider();
                dataProvider.PostShippingInfo(Utils.GenerateRequestModel(this.get("content")), function(result){
                    if(result.data.status == 200){
                        _self.set("errMessage", null);
                        _self.transitionToRoute('account.shipping');
                    }else{
                        _self.set("errMessage", "Please try again later!");
                        $("#BtnShippingAddress").removeClass('load');
                    }
                });
            },
            Cancel: function(){
                this.transitionToRoute('account.shipping');
            }
        }
    }),
    EntitlementsController: Ember.ObjectController.extend({
        errMessage: null,
        content:{
            results: []
        }
    })
};