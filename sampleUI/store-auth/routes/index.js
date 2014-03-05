
var HomePage = require('./home');
var AuthPage = require('./auth');
var MyPage = require('./my');

var AccountAPI = require('./api/account');
var RecaptchaAPI = require('./api/recaptcha_server');

module.exports = function(app){

  app.get("/", HomePage);
  app.get("/My", MyPage);
  app.get("/SiteAuth", AuthPage.SiteAuth);
  app.get("/logout", AuthPage.Logout);

  app.post('/API/Identity/Login', AccountAPI.Login);
  app.post('/API/Identity/Register', AccountAPI.Register);
  app.post('/API/Validators/Captcha', RecaptchaAPI);

}