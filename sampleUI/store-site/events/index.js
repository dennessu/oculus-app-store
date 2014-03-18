var Identity = require('../logic/identity');
var Catalog = require('../logic/catalog');

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

        socket.on('/api/catalog/product', function (data, fn) {
            Catalog.Product(data, function(data){
                fn(data);
            });
        });

    });
};