var Identity = require('../logic/identity');
var Catalog = require('../logic/catalog');
var Cart = require('../logic/cart');

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

    });
};