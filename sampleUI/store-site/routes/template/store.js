
exports.Index = function(req, res){
    res.render("store/index", {title: "Store Demo"});
};
exports.Detail = function(req, res){
    res.render("store/detail", {title: "Store Demo"});
}

exports.Cart = function(req, res){
    res.render("store/cart", {title: "Store Demo"});
}

exports.OrderSummary = function(req, res){
    res.render("store/order_summary", {title: "Store Demo"});
}