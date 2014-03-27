exports.Layout = function(req, res){
    res.render('account/layout', {layout: false, title: "Store Demo"});
};
exports.Index = function(req, res){
    res.render('account/settings', {layout: false, title: "Store Demo"});
};
exports.EditInfo = function(req, res){
    res.render('account/edit_settings', {layout: false, title: "Store Demo"});
};
exports.EditPassword = function(req, res){
    res.render('account/edit_password', {layout: false, title: "Store Demo"});
};
exports.EditShipping = function(req, res){
    res.render('account/edit_shipping', {layout: false, title: "Store Demo"});
};
exports.Profile = function(req, res){
    res.render('account/profile', {layout: false, title: "Store Demo"});
};
exports.EditProfile = function(req, res){
    res.render('account/edit_profile', {layout: false, title: "Store Demo"});
};
exports.History = function(req, res){
    res.render('account/history', {layout: false, title: "Store Demo"});
};
exports.Payment = function(req, res){
    res.render('account/payment', {layout: false, title: "Store Demo"});
};
exports.AddPayment = function(req, res){
    res.render('modules/payment_edit', {layout: false, title: "Store Demo"});
};
exports.Shipping = function(req, res){
    res.render('account/shipping', {layout: false, title: "Store Demo"});
};
exports.AddShipping = function(req, res){
    res.render('modules/shipping_info_edit', {layout: false, title: "Store Demo"});
};