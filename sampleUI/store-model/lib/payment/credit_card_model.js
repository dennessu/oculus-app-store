
module.exports = function(){
    this.accountName = ""; //"David",
    this.accountNum = ""; //"4111111111111111",
    this.isValidated = ""; //"false",
    this.isDefault = "";  //"true",
    this.type = {
        "href": "http://api.wan-san.com/v1/payment-instrument-types/CREDITCARD",
        "id": "CREDITCARD"
    };
    this.creditCardRequest = {
        "expireDate": "", //"1999-11-27"
        "encryptedCvmCode": "111"
    };
    this.address = {
        "addressLine1": "", //"ThirdStreetFerriday",
        "city": "LA",
        "state": "CA",
        "country": "US",
        "postalCode": "12345"
    };
    this.phone = {
        "type": "Home",
        "number": "12345678"
    };
    this.relationship = "",
    this.email = "",
    this.trackingUuid = ""; //"f48d6fae-adec-43d0-a865-00a0c91e6bf7"
}