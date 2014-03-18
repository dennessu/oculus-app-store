
exports.Login = function(req, res){
    res.render('identity/login');
};
exports.Register = function(req, res){
    res.render('identity/register');
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