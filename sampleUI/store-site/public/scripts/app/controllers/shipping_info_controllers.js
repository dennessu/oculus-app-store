
var ShippingInfoControllers = {
    IndexController: Ember.ObjectController.extend({
        isValid: false,
        methods: (function(){
            var result = new Array();
            for(var i = 0; i < AppConfig.ShippingMethods.length; ++i) result.push({t: AppConfig.ShippingMethods[i].name, v: AppConfig.ShippingMethods[i].value});
            return result;
        }()),
        content:{
            shippingMethodId: ""
        },
        actions: {
            Continue: function(){

                if($("#BtnShippingMethod").hasClass('load')) return;
                $("#BtnShippingMethod").addClass('load');

                var _self = this;
                var selectedValue = $('#ShippingMethodId').val();
                if(selectedValue == undefined){
                    $("#BtnShippingMethod").removeClass('load');
                    this.set("isValid", true);
                }else{
                    this.set("isValid", false);
                    Utils.Cookies.Set(AppConfig.CookiesName.ShippingMethodId, selectedValue);

                    var provider = new BillingProvider();
                    provider.GetShippingInfos(Utils.GenerateRequestModel(null), function(result){
                        if(result.data.status == 200){
                            var lists = JSON.parse(result.data.data).results;
                            if(lists.length <= 0) {
                                _self.transitionToRouteAnimated("shipping.edit", {shipping: "flip"});
                            }else {
                                _self.transitionToRouteAnimated("shipping.address", {main: "slideOverLeft"});
                            }
                        }else{
                           _self.transitionToRouteAnimated("shipping.address", {main: "slideOverLeft"});
                        }
                    });
                }
            }
        }
    }),
    AddressController: Ember.ObjectController.extend({
        isValid: false,
        content:{
            results: []
        },
        actions: {
            CreateNew: function(){
                this.transitionToRouteAnimated("shipping.edit", {shipping: "flip"});
            },
            Continue: function(){

                if($("#BtnShippingId").hasClass('load')) return;
                $("#BtnShippingId").addClass('load');

                var _self = this;
                var selectedValue = $('input:radio:checked').val();
                if(selectedValue == undefined){
                    this.set("isValid", true);
                    $("#BtnShippingId").removeClass('load');
                }else{
                    this.set("isValid", false);
                    Utils.Cookies.Set(AppConfig.CookiesName.ShippingId, selectedValue);

                    _self.transitionToRouteAnimated("payment", {main: "slideOverLeft"});
                }
            }
        }
    }),
    EditController: Ember.ObjectController.extend({
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
                        _self.transitionToRouteAnimated("payment", {main: "slideOverLeft"});
                    }else{
                        _self.set("errMessage", "Please try again later!");
                        $("#BtnShippingAddress").removeClass('load');
                    }
                });
            },
            Cancel: function(){
                var _self = this;

                _self.transitionToRouteAnimated("shipping.address", {shipping: "flip"});
            }
        }
    })
};