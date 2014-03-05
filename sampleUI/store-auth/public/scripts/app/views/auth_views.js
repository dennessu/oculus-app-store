define([
  'ember-load',
  'app-setting',
  'google-captcha'
], function(Ember, setting, g){

  return {
    LoginView: Ember.View.extend({
      templateName:"login",
      didInsertElement: function(){
        console.log("Render Login View");

        $("form").validate();
      }
    }),

    RegisterView: Ember.View.extend({
      templateName:"register",
      didInsertElement: function(){
        console.log("Render Register View");

        jQuery.validator.addMethod("Birthday", function(value, element) {
          return !isNaN($("#RMonth").val()) && !isNaN($("#RDay").val()) && !isNaN($("#RYear").val());
        }, "Please select.");

        $("form").validate();
      }
    }),

    CaptchaView: Ember.View.extend({
      templateName:"captcha",
      didInsertElement: function(){
        console.log("Render Recaptcha View");

        Recaptcha.create(setting.GoogleCaptchaPublicKey, 'captchadiv',
          {
            tabindex: 1,
            theme: "clean",
            callback: Recaptcha.focus_response_field
          });
      }
    })
  };
});