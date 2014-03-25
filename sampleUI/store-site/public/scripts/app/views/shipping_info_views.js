
var ShippingInfoViews = {
    LayoutView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.ShippingInfo.Layout]
    }),
    IndexView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.ShippingInfo.Index],
        didInsertElement: function(){
            $("form").validate();
        }
    }),
    AddressView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.ShippingInfo.Address]
    }),
    EditView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.ShippingInfo.Edit],
        didInsertElement: function(){
            $("form").validate();
        }
    })
};