/**
 * Created by Haiwei on 2014/4/16.
 */

var ShippingInfoModel = function(){
    this.userId = {
        "href": "http://api.wan-san.com/users/12345",
        "id": "12345"
    },
    this.street = ""; //"NO. 1000 Twin Dophin Dr",
    this.city = ""; //"Redwood City",
    this.state = ""; //"CA",
    this.postalCode = ""; //"96045",
    this.country = ""; //"US",
    this.firstName = ""; //"Steve",
    this.lastName = ""; //"Smith",
    this.phoneNumber = ""; //"207-655-2345"
};

if(typeof(window) != "undefined"){
    Module.Load(window, "Models.Billing.ShippingInfoModel", ShippingInfoModel);
}else{
    exports.ShippingInfoModel = ShippingInfoModel;
}
