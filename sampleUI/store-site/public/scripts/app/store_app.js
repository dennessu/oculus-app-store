
var App = Ember.App = Ember.Application.create();

App.ApplicationAdapter = CustomAdapter;
App.store = DS.Store.create({
    adapter: App.ApplicationAdapter
});
App.AuthKey = AuthKeyModel;
App.Product = ProductModel;
App.CartItem = CartItemModel;
App.ShippingInfo = ShippingInfoModel;
App.CreditCard = CreditCartModel;
App.Profile = ProductModel;

App.Router.map(function(){
    this.resource("detail", { path: "/detail/:productId"});
    this.resource("cart", { path: "/cart"});
    this.resource("shipping", {path: "/shipping"}, function(){
        this.route("address");
        this.route("edit");
    });
    this.resource("payment", {path: "/payment"}, function(){
        this.route("edit");
    });
    this.resource("ordersummary", { path: "/ordersummary"});
    this.resource("thanks", {path: "/thanks"});

    this.resource("account", {path: "/account"}, function(){

        this.route("editinfo");
        this.route("editpassword");
        this.route("profile");
        this.route("editprofile");
        this.route("history");
        this.route("payment");

    });
});

App.ApplicationRoute = StoreRoutes.ApplicationRoute;
App.IndexRoute = StoreRoutes.IndexRoute;
App.DetailRoute = StoreRoutes.DetailRoute;
App.CartRoute = StoreRoutes.CartRoute;
App.OrdersummaryRoute = StoreRoutes.OrderSummaryRoute;
App.ThanksRoute = StoreRoutes.ThanksRoute;
App.ShippingRoute = ShippingInfoRoutes.LayoutRoute;
App.ShippingIndexRoute = ShippingInfoRoutes.IndexRoute;
App.ShippingAddressRoute = ShippingInfoRoutes.AddressRoute;
App.ShippingEditRoute = ShippingInfoRoutes.EditRoute;
App.PaymentRoute = PaymentRoutes.LayoutRoute;
App.PaymentIndexRoute = PaymentRoutes.IndexRoute;
App.PaymentEditRoute = PaymentRoutes.EditRoute;

App.AccountRoute = AccountRoutes.LayoutRoute;
App.AccountIndexRoute = AccountRoutes.IndexRoute;
App.AccountEditinfoRoute = AccountRoutes.EditInfoRoute;
App.AccountEditpasswordRoute = AccountRoutes.EditPasswordRoute;
App.AccountProfileRoute = AccountRoutes.ProfileRoute;
App.AccountEditprofileRoute = AccountRoutes.EditProfileRoute;
App.AccountHistoryRoute = AccountRoutes.HistoryRoute;
App.AccountPaymentRoute = AccountRoutes.PaymentRoute;

App.IndexView = StoreViews.IndexView;
App.DetailView = StoreViews.DetailView;
App.CartView = StoreViews.CartView;
App.OrdersummaryView = StoreViews.OrderSummaryView;
App.ThanksView = StoreViews.ThanksView;
App.ShippingView = ShippingInfoViews.LayoutView;
App.ShippingIndexView = ShippingInfoViews.IndexView;
App.ShippingAddressView = ShippingInfoViews.AddressView;
App.ShippingEditView = ShippingInfoViews.EditView;
App.PaymentView = PaymentViews.LayoutView;
App.PaymentIndexView = PaymentViews.IndexView;
App.PaymentEditView = PaymentViews.EditView;

App.AccountView = AccountViews.LayoutView;
App.AccountIndexView = AccountViews.IndexView;
App.AccountEditinfoView = AccountViews.EditInfoView;
App.AccountEditpasswordView = AccountViews.EditPasswordView;
App.AccountProfileView = AccountViews.ProfileView;
App.AccountEditprofileView = AccountViews.EditProfileView;
App.AccountHistoryView = AccountViews.HistoryView;
App.AccountPaymentView = AccountViews.PaymentView;

App.ApplicationController = StoreControllers.ApplicationController;
App.DetailController = StoreControllers.DetailController;
App.CartController = StoreControllers.CartController;
App.CartItemController = StoreControllers.CartItemController;
App.OrdersummaryController = StoreControllers.OrderSummaryController;
App.OrderSummaryItemController = StoreControllers.OrderSummaryItemController;
App.ThanksController = StoreControllers.ThanksController;
App.ShippingIndexController = ShippingInfoControllers.IndexController;
App.ShippingAddressController = ShippingInfoControllers.AddressController;
App.ShippingEditController = ShippingInfoControllers.EditController;
App.PaymentIndexController = PaymentControllers.IndexController;
App.PaymentEditController = PaymentControllers.EditController;

App.AccountIndexController = AccountControllers.IndexController;
App.AccountEditinfoController = AccountControllers.EditInfoController;
App.AccountEditpasswordController = AccountControllers.EditPasswordController;
App.AccountProfileController = AccountControllers.ProfileController;
App.AccountEditprofileController = AccountControllers.EditProfileController;
App.AccountHistoryController = AccountControllers.HistoryController;
App.AccountHistoryItemController = AccountControllers.HistoryItemController;
App.AccountHistoryItemProductsController = AccountControllers.HistoryItemProducts;
App.AccountPaymentController = AccountControllers.PaymentController;

