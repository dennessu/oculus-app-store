
exports.Index =  function(req, res){
    res.render('store/index', {layout: 'layout', title: 'Store Demo'});
};

exports.ProductDetail =  function(req, res){
    res.render('store/detail', {layout: 'layout', title: 'Product Detail'});
};

exports.ShoppingCart =  function(req, res){
    res.render('cart/cart', {layout: 'layout', title: 'Shopping Cart'});
};

exports.OrderSummary =  function(req, res){
    res.render('cart/order_summary', {layout: 'layout', title: 'Order Summary'});
};

exports.PurchaseNotification =  function(req, res){
    res.render('cart/notification', {layout: 'identity/layout', title: 'Order Summary'});
};
