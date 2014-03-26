
var PaymentViews = {
    LayoutView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.Payment.Layout]
    }),
    IndexView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.Payment.Index],
        didInsertElement: function(){
            $("form").validate();
        }
    }),
    EditView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.Payment.Edit],
        didInsertElement: function(){
            $("form").validate();
        }
    })
};