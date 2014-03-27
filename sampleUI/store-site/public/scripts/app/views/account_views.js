
var AccountViews = {
    LayoutView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.Account.Layout]
    }),
    IndexView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.Account.Index]
    }),
    EditInfoView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.Account.EditInfo],
        didInsertElement: function(){
            $("form").validate();
        }
    }),
    EditPasswordView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.Account.EditPassword],
        didInsertElement: function(){
            $("form").validate();
        }
    }),
    ProfileView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.Account.Profile]
    }),
    EditProfileView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.Account.EditProfile]
    }),
    HistoryView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.Account.History]
    }),
    PaymentView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.Account.Payment]
    }),
    AddPaymentView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.Account.AddPayment],
        didInsertElement: function(){
            $("form").validate();
        }
    }),
    ShippingView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.Account.Shipping]
    }),
    AddShippingView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.Account.AddShipping],
        didInsertElement: function(){
            $("form").validate();
        }
    })

};