
exports.Login = function(req, res){
    res.render('identity/login');
};

exports.Captcha = function(req, res){
    res.render('identity/captcha');
};

exports.TFA = function(req, res){
    res.render('identity/tfa');
};
exports.My = function(req, res){
    res.render('identity/my');
};

exports.Register = function(req, res){
    res.render('identity/register');
};
exports.PIN = function(req, res){
    res.render('identity/pin');
};