
var App = Ember.App = Ember.Application.create();

App.Router.map(function(){

});

App.IndexRoute = Ember.Route.extend({
    model: function(){
        this.store.find("Product");
    }
});


