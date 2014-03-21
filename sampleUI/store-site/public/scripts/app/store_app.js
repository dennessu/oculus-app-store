
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
    this.resource("shipping", {path: "/shipping"});
    //this.resource("orderSummary", { path: "/orderSummary"});

});

App.ApplicationRoute = StoreRoutes.ApplicationRoute;
App.IndexRoute = StoreRoutes.IndexRoute;
App.DetailRoute = StoreRoutes.DetailRoute;
App.CartRoute = StoreRoutes.CartRoute;
App.OrderSummaryRoute = StoreRoutes.OrderSummaryRoute;

App.ShippingRoute = ShippingInfoRoutes.EntryRoute;
App.ShippingEditRoute = ShippingInfoRoutes.EditRoute;
App.PaymentIndexRoute = PaymentRoutes.EntryRoute;
App.PaymentEditRoute = PaymentRoutes.EditRoute;

App.IndexView = StoreViews.IndexView;
App.DetailView = StoreViews.DetailView;
App.CartView = StoreViews.CartView;
App.OrderSummaryRoute = StoreViews.OrderSummaryView;

App.ShippingView = ShippingInfoViews.EntryView;
App.ShippingEditView = ShippingInfoViews.EditView;
App.PaymentIndexView = PaymentViews.EntryView;
App.PaymentEditView = PaymentViews.EditView;

App.ApplicationController = StoreControllers.ApplicationController;
App.DetailController = StoreControllers.DetailController;
App.CartController = StoreControllers.CartController;
App.CartItemController = StoreControllers.CartItemController;

App.ShippingController = ShippingInfoControllers.EntryController;
App.ShippingEditController = ShippingInfoControllers.EditController;
App.PaymentIndexController = PaymentControllers.EntryController;
App.PaymentEditController = PaymentControllers.EditController;

