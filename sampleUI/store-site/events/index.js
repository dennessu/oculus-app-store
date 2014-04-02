var Identity = require('../logic/identity');
var Catalog = require('../logic/catalog');
var Cart = require('../logic/cart');
var Billing = require('../logic/billing');
var Payment = require('../logic/payment');
var Entitlement = require('../logic/entitlement');

module.exports = function(io){
    io.set("log level", 0);

    io.sockets.on("connection", function (socket) {
        socket.emit('welcome', { hello: 'world' });
        socket.on('disconnect', function () {
            io.sockets.emit('user disconnected');
        });

        var Events = {
            Identity: Identity,
            Catalog: Catalog,
            Cart: Cart,
            Billing: Billing,
            Payment: Payment,
            Entitlement: Entitlement
        };

        var APIs = process.AppConfig.APIs;
        for(var s in APIs){
            var server = APIs[s];

            for(var e in APIs[s]){
                if(e.toLowerCase() == "config") continue;

                var eventName = server.Config.namespace + server[e].path;

                console.log("Register Event: ", eventName);

                socket.on(eventName, function (data, fn) {
                    var address = socket.handshake.address;
                    data.query.ip = address.address;

                    Events[s][e](data, function(data){
                        fn(data);
                    });
                });
            }
        }
    });
};