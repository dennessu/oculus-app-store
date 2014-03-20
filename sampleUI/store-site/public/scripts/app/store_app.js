
var App = Ember.App = Ember.Application.create();

App.ApplicationAdapter = CustomAdapter;
App.store = DS.Store.create({
    adapter: App.ApplicationAdapter
});

App.AuthKey = AuthKey;
App.Product = ProductModel;

App.Router.map(function(){

    this.resource("detail", { path: "/detail/:productId"});
});

//App.ApplicationRoute = StoreRoutes.ApplicationRoute;
App.IndexRoute = StoreRoutes.IndexRoute;
App.DetailRoute = StoreRoutes.DetailRoute;

App.IndexView = StoreViews.IndexView;
App.DetailView = StoreViews.DetailView;



