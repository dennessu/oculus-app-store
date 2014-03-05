define([
  'ember-load',
  'app-setting',
  'app/models/user_model'
], function(Ember, setting, userModel){

  return Ember.ObjectController.extend({
    content: userModel,

    title: 'Sign in',
    isValid: false,

    actions:{
      Submit: function(){
        var _this = this;
        var isFailed = false;

        console.log("---------------------------------------------------");
        console.log("Login Request");

        $.ajax({
          url: setting.LoginUrl,
          type:"POST",
          async: false,
          dataType: "json",
          data: {email: this.content.email, password: this.content.password},
          success: function(data, textStatus, jqXHR){

            console.log("Response Succeed");
            console.log("Response textStatus: ", textStatus);
            console.log("Response data: ", data);

            if(textStatus == "success" && typeof(data) != "undefined" && data != null){

              if(data["action"] == 302){
                var reModel = JSON.parse(data["data"]);

                if(reModel["target"] == "_blank"){
                  window.open(reModel.url);
                }else{
                  window.location.href = reModel.url;
                  return;
                }
              }else{
                _this.transitionToRoute("captcha");
                return;
              }
            }else{
              console.log("Response Conditions Failed!");
              isFailed = true
            }
          },
          error: function(XMLHttpRequest, textStatus, errorThrown){

            console.log("Login Error");
            console.log("Response textStatus: ", textStatus);
            console.log("Response errorThrown: ", errorThrown);

            if (XMLHttpRequest.status === 422) {
              console.log("Response status: 422");
              isFailed = true
            }
          }
        });
        console.log("---------------------------------------------------");

        this.set("isValid", isFailed);
      }
    }
  });

});