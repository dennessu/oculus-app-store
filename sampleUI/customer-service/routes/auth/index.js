
exports.Login = function (req, res) {

    res.cookie(global.Config.CookiesName.Code, req.query[global.Config.QueryStrings.Code]);

    res.render('auth/callback', {title: 'redirecting..', action: "login"});
};

exports.Logout = function(req, res){
    res.redirect('/');
    res.end();
};

exports.Register = function(req, res){
    res.redirect('/');
    res.end();
};
