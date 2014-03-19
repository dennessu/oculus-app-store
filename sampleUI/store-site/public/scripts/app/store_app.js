
var App = Ember.App = Ember.Application.create();

App.ProductAdapter = ProductAdapter;
App.store = DS.Store.create({
    adapter: 'ProductAdapter'
});
App.Product = Product;

App.Router.map(function(){

});

App.ApplicationRoute = Ember.Route.extend({
    model: function(){
        return this.store.findAll("Product");
    }
});


