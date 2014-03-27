var Identity = require('../logic/identity');
var Catalog = require('../logic/catalog');
var Cart = require('../logic/cart');
var Billing = require('../logic/billing');
var Payment = require('../logic/payment');

module.exports = function(io){
    io.set("log level", 0);

    io.sockets.on("connection", function (socket) {
        socket.emit('welcome', { hello: 'world' });
        socket.on('disconnect', function () {
            io.sockets.emit('user disconnected');
        });

        socket.on('/api/identity/login', function (data, fn) {
            Identity.Login(data, function(data){
                fn(data);
            });
        });

        socket.on('/api/identity/captcha', function (data, fn) {
            var address = socket.handshake.address;
            data.data.ip = address.address;

            Identity.Captcha(data, function(data){
                fn(data);
            });
        });

        socket.on('/api/identity/tfa', function (data, fn) {
            Identity.TFA(data, function(data){
                fn(data);
            });
        });

        socket.on('/api/identity/register', function (data, fn) {
            Identity.Register(data, function(data){
                fn(data);
            });
        });
        socket.on('/api/identity/get_anonymous_user', function (data, fn) {
            Identity.GetAnonymousUser(data, function(data){
                fn(data);
            });
        });
        socket.on('/api/identity/get_profile', function (data, fn) {
            Identity.GetProfile(data, function(data){
                fn(data);
            });
        });
        socket.on('/api/identity/put_profile', function (data, fn) {
            Identity.PutProfile(data, function(data){
                fn(data);
            });
        });
        socket.on('/api/identity/get_opt_ins', function (data, fn) {
            Identity.GetOptIns(data, function(data){
                fn(data);
            });
        });
        socket.on('/api/identity/post_opt_ins', function (data, fn) {
            Identity.PostOptIns(data, function(data){
                fn(data);
            });
        });
        socket.on('/api/identity/put_user', function (data, fn) {
            Identity.PutUser(data, function(data){
                fn(data);
            });
        });

        socket.on('/api/identity/pin', function (data, fn) {
            Identity.PIN(data, function(data){
                fn(data);
            });
        });

        socket.on('/api/catalog/products', function (data, fn) {
            Catalog.Products(data, function(data){
                fn(data);
            });
        });
        socket.on('/api/catalog/get_download_links', function (data, fn) {
            Catalog.GetDownloadLinksByOfferId(data, function(data){
                fn(data);
            });
        });

        /* Cart -------------------------------------------------------------- */
        socket.on('/api/cart/get', function (data, fn) {
            Cart.GetCart(data, function(data){
                fn(data);
            });
        });
        socket.on('/api/cart/add', function (data, fn) {
            Cart.AddCartItem(data, function(data){
                fn(data);
            });
        });
        socket.on('/api/cart/remove', function (data, fn) {
            Cart.RemoveCartItem(data, function(data){
                fn(data);
            });
        });
        socket.on('/api/cart/update', function (data, fn) {
            Cart.UPdateCartItem(data, function(data){
                fn(data);
            });
        });
        socket.on('/api/cart/merge', function (data, fn) {
            Cart.MergeCartItem(data, function(data){
                fn(data);
            });
        });
        socket.on('/api/cart/get_order_by_id', function (data, fn) {
            Cart.GetOrderById(data, function(data){
                fn(data);
            });
        });
        socket.on('/api/cart/get_order_by_user', function (data, fn) {
            Cart.GetOrders(data, function(data){
                fn(data);
            });
        });
        socket.on('/api/cart/post_order', function (data, fn) {
            Cart.PostOrder(data, function(data){
                fn(data);
            });
        });
        socket.on('/api/cart/put_order', function (data, fn) {
            Cart.PutOrder(data, function(data){
                fn(data);
            });
        });
        socket.on('/api/cart/purchase_order', function (data, fn) {
            Cart.PurchaseOrder(data, function(data){
                fn(data);
            });
        });

        /* Billing -------------------------------------------------------------- */
        socket.on('/api/billing/get_shipping_info', function (data, fn) {
            Billing.GetShippingInfo(data, function(data){
                fn(data);
            });
        });

        socket.on('/api/billing/get_shipping_info_by_id', function (data, fn) {
            Billing.GetShippingInfoById(data, function(data){
                fn(data);
            });
        });
        socket.on('/api/billing/add', function (data, fn) {
            Billing.PostShippingInfo(data, function(data){
                fn(data);
            });
        });

        /* Payment -------------------------------------------------------------- */
        socket.on('/api/payment/get_payment_instruments', function (data, fn) {
            Payment.GetPayment(data, function(data){
                fn(data);
            });
        });

        socket.on('/api/payment/get_payment_instruments_by_id', function (data, fn) {
            Payment.GetPaymentById(data, function(data){
                fn(data);
            });
        });
        socket.on('/api/payment/add', function (data, fn) {
            Payment.PostPayment(data, function(data){
                fn(data);
            });
        });


    });
};