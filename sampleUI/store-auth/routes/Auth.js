var C = require('../config/configuration');
var Utils = require('../helper/utils');

exports.SiteAuth= function(req, res){

  res.render("auth/redirect",
    {
      layout: false,
      title: "Processing",
      Post_Back_Url: req.cookies[C.QueryStringConstants.RedirectUrl],
      FormList: {
        access_token: req.cookies[C.CookiesKeyConstants.AccessToken],
        id_token: req.cookies[C.CookiesKeyConstants.IdToken]
      }
    });
}

exports.Logout= function(req, res){

  Utils.ClearQueryStrings(res, C.SaveQueryStringArray);
  res.clearCookie(C.CookiesKeyConstants.AccessToken, {path: "/"});

  res.redirect("/");
}