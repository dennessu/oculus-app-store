
App.Product = DS.Model.extend({
    //id: DS.attr("number"),
    name: DS.attr("string"),
    price: DS.attr("number"),
    picture: DS.attr("string"),
    description: DS.attr("string")
});