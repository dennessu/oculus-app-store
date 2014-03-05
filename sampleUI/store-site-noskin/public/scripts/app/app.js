require([
    'ember-load',
    'app-setting',
    'app/config/auth_manager',
    'app/adapters/product_adapter',
    'app/adapters/cart_item_loacal_adapter',
    'app/models/api_key',
    'app/models/user_model',
    'app/models/product_model',
    'app/models/cart_item_model',
    'app/views/shop_views',
    'app/routes/router',
    'app/routes/routes',
    'app/controllers/product_controller',
    'app/controllers/cart_controller',
    'app/controllers/cart_item_controller'
], function (Ember, setting, auth,
                productAdapter, cartLocalAdapter,
                apiKey, userModel, productModel, cartItemModel, views,
                router, routes,
                productCont, cartCont, cartItemCont) {

    Ember.read
    Ember.App = Ember.Application.create({
        rootElement: "#AppContainer",
        ProductAdapter: productAdapter,
        CartItemAdapter: cartLocalAdapter,

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