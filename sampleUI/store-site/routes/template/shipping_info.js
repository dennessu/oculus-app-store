exports.Layout = function(req, res){
    res.render('modules/shipping_info');
};
exports.Index = function(req, res){
    res.render('modules/shipping_info_index');
};
exports.Address = function(req, res){
    res.render('modules/shipping_info_address');
};
exports.Edit = function(req, res){
    res.render('modules/shipping_info_edit');
};