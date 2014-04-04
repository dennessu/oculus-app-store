
exports.Index = function(req, res){
    res.render("store/index", {title: "Oculus VR Store"});
};
exports.Detail = function(req, res){
    res.render("store/detail", {title: "Oculus VR Store"});
}

exports.Cart = function(req, res){
    res.render("store/cart", {title: "Oculus VR Store"});
}

exports.OrderSummary = function(req, res){
    res.render("store/order_summary", {title: "Oculus VR Store"});
}

exports.Thanks = function(req, res){
    res.render("store/thanks_for_purchase", {title: "Oculus VR Store"});
}