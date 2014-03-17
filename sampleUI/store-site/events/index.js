var Identity = require('../logic/identity');

module.exports = function(io){
    io.set("log level", 0);

    io.sockets.on("connection", function (socket) {
        socket.emit('welcome', { hello: 'world' });
        socket.on('disconnect', function () {
            io.sockets.emit('user disconnected');
        });

        socket.on('/api/identity/login', function (data, fn) {
            Identity.Login(data.data, data.cookies, null, function(data){
                fn(data);
            });
        });

        socket.on('/api/identity/captcha', function (data, fn) {
            var address = socket.handshake.address;
            data.data.ip = address.address;

            Identity.Captcha(data.data, data.cookies, null, function(data){
                fn(data);
            });
        });

        socket.on('/api/identity/tfa', function (data, fn) {
            Identity.TFA(data.data, data.cookies, null, function(data){
                fn(data);
            });
        });


    });
};