
var ShippingInfoModel = DS.Model.extend({
    //id: DS.attr("number"),
    street: DS.attr("string"), //"NO. 1000 Twin Dophin Dr",
    city: DS.attr("string"),//"Redwood City",
    state: DS.attr("string"), //"CA",
    postalCode: DS.attr("string"), //"96045",
    country: DS.attr("string"), //"US",
    firstName: DS.attr("string"), //"Steve",
    lastName: DS.attr("string"), //"Smith",
    phoneNumber: DS.attr("string") //"207-655-2345"
});
