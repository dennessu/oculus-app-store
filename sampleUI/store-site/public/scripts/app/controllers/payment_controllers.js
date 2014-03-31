
var PaymentControllers = {
    IndexController: Ember.ObjectController.extend({
        errMessage: null,
        content:{
            paymentList: []
        },
        paymentId: "",
        actions: {
            CreateNew: function(){
                var _self = this;
                _self.transitionToRouteAnimated("payment.edit", {payment: "flip"});

            },
            Continue: function(){
                var _self = this;
                console.log("[PaymentIndexController:Continue] Select Payment Id: ", _self.get("paymentId"));
                if(_self.get("paymentId") == ""){
                    _self.set("errMessage", "Please select payment method");
                }else{
                    _self.set("errMessage", null);
                    Utils.Cookies.Set(AppConfig.CookiesName.PaymentId, _self.get("paymentId"));

                    var cartProvider = new CartProvider();
                    cartProvider.PutOrder(Utils.GenerateRequestModel(null), function(resultData){
                        if(resultData.data.status == 200){
                            _self.transitionToRouteAnimated("ordersummary", {main: "slideOverLeft"});
                        } else{
                            _self.set("errMessage", "Please try again later!");
                        }
                    });
                }
            }
        }
    }),
    EditController: Ember.ObjectController.extend({
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

                        // update order
                        var cartProvider = new CartProvider();
                        cartProvider.PutOrder(Utils.GenerateRequestModel(null), function(resultData){
                           if(resultData.data.status == 200){
                               _self.transitionToRouteAnimated("ordersummary", {main: "slideOverLeft"});
                           } else{
                               _self.set("errMessage", "Please try again later!");
                           }
                        });
                    }else{
                        _self.set("errMessage", "Please try again later!");
                    }
                });
            },
            Cancel: function () {
                var _self = this;
                _self.transitionToRouteAnimated("payment.index", {payment: "flip"});
            }
        }
    })
};