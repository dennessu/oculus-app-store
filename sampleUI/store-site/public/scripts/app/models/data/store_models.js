
var ProductModel = DS.Model.extend({
    //id: DS.attr("number"),
    name: DS.attr("string"),
    price: DS.attr("number"),
    picture: DS.attr("string"),
    description: DS.attr("string")
});


var CartItemModel = DS.Model.extend({
    // id
    product_id: DS.attr("number"),
    qty: DS.attr("number")
});