
module.exports = function(){
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