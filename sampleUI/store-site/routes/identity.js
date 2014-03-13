
exports.Index = function(req, res){

    res.render('identity/login', {layout: 'identity/layout', title: 'Store Demo'});
};


exports.Login = function(req, res){

    res.render('identity/login', {layout: 'identity/layout', title: 'Store Demo'});
};

exports.LoginSuccess = function(req, res){

    res.render('identity/login_tfa', {layout: 'identity/layout', title: 'Store Demo'});
};

exports.RegisterNotification = function(req, res){
    res.render('identity/notification', {layout: 'identity/layout', title: 'Store Demo'});
};
exports.RegisterVerificationNotification = function(req, res){
    res.render('identity/verification_notification', {layout: 'identity/layout', title: 'Store Demo'});
};