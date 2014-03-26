
var StoreViews = {
    IndexView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.Store.Index]
    }),
    DetailView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.Store.Detail]
    }),
    CartView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.Store.Cart]
    }),
    OrderSummaryView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.Store.OrderSummary]
    }),
    ThanksView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.Store.Thanks]
    })
};