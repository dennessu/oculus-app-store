
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

                if($("#BtnPaymentId").hasClass('load')) return;
                $("#BtnPaymentId").addClass('load');

                var _self = this;
                console.log("[PaymentIndexController:Continue] Select Payment Id: ", _self.get("paymentId"));
                if(_self.get("paymentId") == ""){
                    _self.set("errMessage", "Please select payment method");
                    $("#BtnPaymentId").removeClass('load');
                }else{
                    _self.set("errMessage", null);
                    Utils.Cookies.Set(AppConfig.CookiesName.PaymentId, _self.get("paymentId"));

                    var cartProvider = new CartProvider();
                    cartProvider.PutOrder(Utils.GenerateRequestModel(null), function(resultData){
                        if(resultData.data.status == 200){
                            _self.transitionToRouteAnimated("ordersummary", {main: "slideOverLeft"});
                        } else{
                            _self.set("errMessage", "Please try again later!");
                            $("#BtnPaymentId").removeClass('load');
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

                        // update order
                        var cartProvider = new CartProvider();
                        cartProvider.PutOrder(Utils.GenerateRequestModel(null), function(resultData){
                           if(resultData.data.status == 200){
                               _self.transitionToRouteAnimated("ordersummary", {main: "slideOverLeft"});
                           } else{
                               _self.set("errMessage", "Please try again later!");
                               $("#BtnPayment").removeClass('load');
                           }
                        });
                    }else{
                        _self.set("errMessage", "Please try again later!");
                        $("#BtnPayment").removeClass('load');
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