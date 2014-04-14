exports.Layout = function(req, res){
    res.render('account/layout', {layout: false, title: "Oculus VR Store"});
};
exports.Index = function(req, res){
    res.render('account/settings', {layout: false, title: "Oculus VR Store"});
};
exports.EditInfo = function(req, res){
    res.render('account/edit_settings', {layout: false, title: "Oculus VR Store"});
};
exports.EditPassword = function(req, res){
    res.render('account/edit_password', {layout: false, title: "Oculus VR Store"});
};
exports.EditShipping = function(req, res){
    res.render('account/edit_shipping', {layout: false, title: "Oculus VR Store"});
};
exports.Profile = function(req, res){
    res.render('account/profile', {layout: false, title: "Oculus VR Store"});
};
exports.EditProfile = function(req, res){
    res.render('account/edit_profile', {layout: false, title: "Oculus VR Store"});
};
exports.History = function(req, res){
    res.render('account/history', {layout: false, title: "Oculus VR Store"});
};
exports.Payment = function(req, res){
    res.render('account/payment', {layout: false, title: "Oculus VR Store"});
};
exports.AddPayment = function(req, res){
    res.render('modules/payment_edit', {layout: false, title: "Oculus VR Store"});
};
exports.Shipping = function(req, res){
    res.render('account/shipping', {layout: false, title: "Oculus VR Store"});
};
exports.AddShipping = function(req, res){
    res.render('modules/shipping_info_edit', {layout: false, title: "Oculus VR Store"});
};

exports.Entitlements = function(req, res){
    res.render('account/user_entitlements', {layout: false, title: "Oculus VR Store"});
};