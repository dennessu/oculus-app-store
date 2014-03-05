define([
  'ember-load',
  'app-setting'
], function(Ember, setting){

  return  Ember.ObjectController.extend({
    title: 'Sign up',
    isShowCaptcha: false,
    isValid: false,
    errMessage: 'Please try again later!',
    months:(function(){
      var result = new Array();
      result.push({t: "Month", v: ""});
      for(var i = 1; i <= 12; ++i) result.push({t: i, v: i});
      return result;
    }()),
    days:(function(){
      var result = new Array();
      result.push({t: "Day", v: ""});
      for(var i = 1; i <= 31; ++i) result.push({t: i, v: i});
      return result;
    }()),
    years:(function(){
      var result = new Array();
      result.push({t: "Year", v: ""});
      for(var i = new Date().getFullYear(); i >= 1950; --i) result.push({t: i, v: i});
      return result;
    }()),
    content:{
      firstname:"",
      lastname:"",
      email:"",
      password:"",
      month:"",
      day:"",
      year:""
    },

    actions:{
      Submit: function(){
        var _this = this;
        var isFailed = false;

        console.log("---------------------------------------------------");
        console.log("Register Request");

        $.ajax({
          url: setting.RegisterUrl,
          type:"POST",
          async: false,
          dataType: "json",
          data: this.content,
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

            console.log("Register Error");
            console.log("Response textStatus: ", textStatus);
            console.log("Response errorThrown: ", errorThrown);

            if (XMLHttpRequest.status === 422) {
              console.log("Response status: 422");
                try{
                    var reObj = JSON.parse(XMLHttpRequest.responseText);
                    if(typeof(reObj['data']) != "undefined"){
                        var errObj = JSON.parse(reObj.data);
                        if(typeof(errObj['description']) != "undefined"){
                            _this.set("errMessage", errObj['description']);
                        }
                    }
                }catch(e){}

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