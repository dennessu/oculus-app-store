
// Application Setting
define({
  AdapterHost: "/",
  AdapterNamespace: "api",
  ProductAdapterHost: "/api",
  ProductAdapterNamespace: "",
  IdentityAdapterHost: "/api",
  IdentityAdapterNamespace: "",
    CartAdapterHost: "/api",
    CartAdapterNamespace: "",

  GoogleCaptchaPublicKey: "6LeKhO4SAAAAAL53gitVTB5ddevC59wE-6usFCnT",

  LoginUrl: "/API/Account/Login",
  RegisterUrl: "/API/Account/Register",
  CaptchaUrl: "/API/Validators/Captcha",

  GetView: function(url, name){
        $.ajax({
            url: url,
            async: false,
            success: function (data) {
                Ember.TEMPLATES[name] = Ember.Handlebars.compile(data);
            }
        });
      return Ember.TEMPLATES[name];
    }
});