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
            for(var e in APIs[s]){
                if(e.toLowerCase() == "config") continue;
                var eventName = APIs[s].Config.namespace + APIs[s][e].path;

                (function(sock, eventStr, func){

                    sock.on(eventStr, function (data, fn) {
                        console.log("Event Path: ", eventStr);

                        var address = sock.handshake.address;
                        data.query.ip = address.address;

                        func(data, function(data){
                            fn(data);
                        });
                    });
                })(socket, eventName, Events[s][e]);

            }
        }
    });
};