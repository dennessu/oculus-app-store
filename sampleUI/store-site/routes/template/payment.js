exports.Layout = function(req, res){
    res.render('modules/payment');
};
exports.Index = function(req, res){
    res.render('modules/payment_index');
};
exports.Edit = function(req, res){
    res.render('modules/payment_edit');
};