
var DevCenterViews = {
    IndexView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.DevCenter.Index],
        didInsertElement: function(){
            $("form").validate({
                errorPlacement: function(error, element) {
                    error.appendTo(element.parent());
                }
            });

            var isDev = Utils.Cookies.Get(AppConfig.CookiesName.IsDev);
            console.log("Is Developer: ", isDev);
            if(isDev == "true"){
                $("#ConfirmDialog").hide();
            }else{
                $("#ConfirmDialog").show();
            }
        }
    })
};