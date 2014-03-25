
var ProfileModel = DS.Model.extend({
    type: DS.attr("string"),
    region: DS.attr("string"),
    firstName: DS.attr("string"),
    middleName: DS.attr("string"),
    lastName: DS.attr("string"),
    dateOfBirth: DS.attr("string"),
    locale: DS.attr("string")
});
