
var ShippingInfoViews = {
    LayoutView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.ShippingInfo.Layout]
    }),
    IndexView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.ShippingInfo.Index]
    }),
    EditView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.ShippingInfo.Edit]
    })
};