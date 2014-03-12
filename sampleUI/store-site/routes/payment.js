
exports.Entry = function(req, res){
    res.render('payment/entry', {layout: 'payment/layout', title: 'Store Demo'});
};

exports.Create = function(req, res){
    res.render('payment/create', {layout: 'payment/layout', title: 'Store Demo'});
};

exports.ShippingAddress = function(req, res){
    res.render('payment/address', {layout: 'payment/layout', title: 'Store Demo'});
};

exports.SelectePaymentMethod = function(req, res){
    res.render('payment/selecte_payment_method', {layout: 'payment/layout', title: 'Store Demo'});
};