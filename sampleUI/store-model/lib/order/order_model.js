module.exports = function () {
    this.user = {
        "href": "",
        "id": ""
    };
    this.trackingUuid = "";
    this.type = "PAY_IN";
    this.country = "US";
    this.currency = "USD";
    this.tentative = true;
    this.shippingMethodId = {
        "href": "",
        "id": ""
    };
    this.shippingAddressId = {
        "href": "",
        "id": ""
    };
    this.paymentInstruments = new Array(); // [{"href" : "https://xxx.xxx.xxx", "id" : "00000025C000"}]
    this.orderItems = new Array(); //[ {"offer" : { "href" : "https://xxx.xxx.xxx", "id" : "000002080040", "quantity" : 2 },
};