
var DevCenterViews = {
    IndexView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.DevCenter.Index],
        didInsertElement: function(){
            $("form").validate({
                errorPlacement: function(error, element) {
                    error.appendTo(element.parent());
                }
            });
        }
    })
};