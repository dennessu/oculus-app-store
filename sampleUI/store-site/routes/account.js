
exports.Settings = function(req, res){

    res.render('account/settings', {layout: 'layout', title: 'Account'});
};

exports.EditSettings = function(req, res){

    res.render('account/edit_settings', {layout: 'layout', title: 'Account'});
};

exports.EditPassword = function(req, res){

    res.render('account/edit_password', {layout: 'layout', title: 'Account'});
};

exports.Profile = function(req, res){

    res.render('account/profile', {layout: 'layout', title: 'Account'});
};

exports.EditProfile = function(req, res){

    res.render('account/edit_profile', {layout: 'layout', title: 'Account'});
};


exports.Payment = function(req, res){

    res.render('account/payment', {layout: 'layout', title: 'Account'});
};