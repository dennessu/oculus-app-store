
var CreditCartModel = DS.Model.extend({
    accountName: DS.attr("string"),
    accountNum: DS.attr("string"),
    isValidated: DS.attr("boolean"),
    isDefault: DS.attr("boolean"),
    expireDate: DS.attr("string"),
    encryptedCvmCode: DS.attr("string"),
    addressLine1: DS.attr("string"),
    city: DS.attr("string"),
    state: DS.attr("string"),
    country: DS.attr("string"),
    postalCode: DS.attr("string"),
    phoneType: DS.attr("string"),
    phoneNumber: DS.attr("string")
});