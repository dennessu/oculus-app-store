define([
  'ember-load',
  'app-setting'
], function(Ember, setting, userModel){

  return Ember.ObjectController.extend({
    title: "Enter the letters you see in the field below:",
    isValid: false,

    actions:{
      Submit:function(){
        var _this = this;
        var isFailed = false;

        var postData = {
          recaptcha_challenge_field: $("#recaptcha_challenge_field").val(),
          recaptcha_response_field: $("#recaptcha_response_field").val()
        };

        console.log("---------------------------------------------------");
        console.log("Captcha Request");

        $.ajax({
          url: setting.CaptchaUrl,
          type:"POST",
          async: false,
          dataType: "json",
          data: postData,
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
                console.log("Response Conditions Failed!");
                isFailed = true
              }
            }else{
              console.log("Response Conditions Failed!");
              isFailed = true
            }
          },
          error: function(XMLHttpRequest, textStatus, errorThrown){

            console.log("Response Error");
            console.log("Response textStatus: ", textStatus);
            console.log("Response errorThrown: ", errorThrown);

            if (XMLHttpRequest.status === 422) {
              console.log("Response status: 422");
              isFailed = true
            }
          }
        });
        console.log("---------------------------------------------------");

        if(isFailed){
          Recaptcha.reload();
        }

        this.set("isValid", isFailed);
      }
    }

  });
});