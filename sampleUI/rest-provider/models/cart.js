/**
 * Created by Haiwei on 2014/4/16.
 */

var OfferItemModel = function(){
    this.offer = {
        id : ""
    };
    this.quantity = "";
    this.selected = true;
    this.createdTime = new Date();
    this.updatedTime = new Date();
};
var CartModel = function(){
    this.self = {
        href: "",
        id: ""
    };
    this.user= {
        href: "",
        id: ""
    };
    this.resourceAge = 0,
        this.createdTime = new Date(),
        this.updatedTime = new Date(),
        this.offers = new Array(),
        this.coupons = new Array()
};

exports.OfferItemModel = OfferItemModel;
exports.CartModel = CartModel;