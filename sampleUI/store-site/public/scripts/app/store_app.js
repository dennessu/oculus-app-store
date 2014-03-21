
var App = Ember.App = Ember.Application.create();

App.ApplicationAdapter = CustomAdapter;
App.store = DS.Store.create({
    adapter: App.ApplicationAdapter
});
App.AuthKey = AuthKeyModel;
App.Product = ProductModel;
App.CartItem = CartItemModel;

App.Router.map(function(){
    this.resource("detail", { path: "/detail/:productId"});
    this.resource("cart", { path: "/cart"});
    this.resource("orderSummary", { path: "/orderSummary"});
});

App.ApplicationRoute = StoreRoutes.ApplicationRoute;
App.IndexRoute = StoreRoutes.IndexRoute;
App.DetailRoute = StoreRoutes.DetailRoute;
App.CartRoute = StoreRoutes.CartRoute;
App.OrderSummaryRoute = StoreRoutes.OrderSummaryRoute;

App.IndexView = StoreViews.IndexView;
App.DetailView = StoreViews.DetailView;
App.CartView = StoreViews.CartView;
App.OrderSummaryRoute = StoreViews.OrderSummaryView;

App.ApplicationController = StoreControllers.ApplicationController;
App.DetailController = StoreControllers.DetailController;
App.CartController = StoreControllers.CartController;
App.CartItemController = StoreControllers.CartItemController;


