
var StoreViews = {
    IndexView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.Store.Index.name]
    }),
    DetailView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.Store.Detail.name]
    }),
    CartView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.Store.Cart.name]
    }),
    OrderSummaryView: Ember.View.extend({
        template: Ember.TEMPLATES[AppConfig.Templates.Store.OrderSummary.name]
    })
};