
exports.Entry = function(req, res){
    res.render('payment/entry', {layout: 'payment/layout', title: 'Store Demo'});
};

exports.Create = function(req, res){
    res.render('payment/create', {layout: 'payment/layout', title: 'Store Demo'});
};