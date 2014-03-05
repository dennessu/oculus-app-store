require([
    'jquery-plugins-load',
    'ember-load',
    'app-setting',
    'app/config/AuthManager',
    'app/adapters/ProductAdapter',
    'app/adapters/IdentityAdapter',
    'app/adapters/CartItemLoacalAdapter',
    'app/adapters/CartRestAdapter',
    'app/models/APIKey',
    'app/models/UserModel',
    'app/models/ProductModel',
    'app/models/CartItemModel',
    'app/views/ShopViews',
    'app/routes/Router',
    'app/routes/Routes',
    'app/controllers/ProductController',
    'app/controllers/CartController',
    'app/controllers/CartItemController'
], function (p, Ember, setting, auth,
                identityAdapter, productAdapter, cartLocalAdapter, cartRestAdapter,
                apiKey, userModel, productModel, cartItemModel, views,
                router, routes,
                productCont, cartCont, cartItemCont) {

    Ember.read
    Ember.App = Ember.Application.create({
        rootElement: "#AppContainer",
        ProductAdapter: productAdapter,
        CartItemAdapter: cartLocalAdapter,
        UserAdapter: identityAdapter,
        CartRestStore: cartRestAdapter,

        AuthManager: auth,

        Router: router,

        APIKey: apiKey,
        User: userModel,
        Product: productModel,
        CartItem: cartItemModel,

        IndexView: views.IndexView,
        ProductView: views.ProductView,
        CartView: views.CartView,

        ApplicationRoute: routes.ApplicationRoute,
        ProductRoute: routes.ProductRoute,
        CartRoute: routes.CartRoute,
        CartIndexRoute: routes.CartIndexRoute,

        ProductController: productCont,
        CartController: cartCont,
        CartItemController: cartItemCont
    });
});