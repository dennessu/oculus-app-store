exports.Layout = function(req, res){
    res.render('account/layout');
};
exports.Index = function(req, res){
    res.render('account/settings');
};
exports.EditInfo = function(req, res){
    res.render('account/edit_setting');
};
exports.EditPassword = function(req, res){
    res.render('account/edit_password');
};
exports.EditShipping = function(req, res){
    res.render('account/edit_shipping');
};
exports.Profile = function(req, res){
    res.render('account/profile');
};
exports.EditProfile = function(req, res){
    res.render('account/edit_profile');
};
exports.History = function(req, res){
    res.render('account/history');
};
exports.Payment = function(req, res){
    res.render('account/payment');
};