require([
  'jquery-plugins-load',
  'google-captcha',
  'ember-load',
  'app-setting',
  'app/adapters/auth_adapter',
  'app/views/auth_views',
  'app/routes/router',
  'app/routes/routes',
  'app/controllers/login_controller',
  'app/controllers/register_controller',
  'app/controllers/captcha_controller',

], function(p, g, Ember, setting, appAdapter, authViews, router, routes, loginCont, registerCont, captchaCont){

  var App = Ember.Application.create({
    Adapter: appAdapter,
    Router: router,

    LoginView: authViews.LoginView,
    RegisterView: authViews.RegisterView,
    CaptchaView: authViews.CaptchaView,

    IndexRoute: routes.IndexRoute,
    LoginRoute: routes.LoginRoute,
    RegisterRoute: routes.RegisterRoute,
    CaptchaRoute: routes.CaptchaRoute,

    LoginController: loginCont,
    RegisterController: registerCont,
    CaptchaController: captchaCont
  });
});