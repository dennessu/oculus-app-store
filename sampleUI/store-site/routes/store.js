
exports.Index =  function(req, res){
    res.render('index', {layout: 'layout', title: 'Store Demo'});
};

exports.ProductDetail =  function(req, res){
    res.render('detail', {layout: 'layout', title: 'Product Detail'});
};

exports.ShoppingCart =  function(req, res){
    res.render('cart', {layout: 'layout', title: 'Shopping Cart'});
};
