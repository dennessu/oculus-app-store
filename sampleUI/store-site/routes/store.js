
exports.Index =  function(req, res){
    res.render('index', {layout: 'layout', title: 'Store Demo'});
};

exports.ProductDetail =  function(req, res){
    res.render('product_detail', {layout: 'layout', title: 'Product Detail'});
};

exports.ShoppingCart =  function(req, res){
    res.render('shopping_cart', {layout: 'layout', title: 'Store Demo'});
};
