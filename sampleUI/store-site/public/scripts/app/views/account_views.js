
var AccountViews = {
    LayoutView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.Account.Layout]
    }),
    IndexView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.Account.Index]
    }),
    EditInfoView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.Account.EditInfo]
    }),
    EditPasswordView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.Account.EditPassword]
    }),
    EditShippingView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.Account.EditShipping]
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
    })
};