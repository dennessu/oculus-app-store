
var PaymentViews = {
    LayoutView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.Payment.Layout]
    }),
    IndexView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.Payment.Index]
    }),
    EditView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.Payment.Edit]
    })
};