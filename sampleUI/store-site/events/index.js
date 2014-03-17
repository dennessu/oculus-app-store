var Identity = require('../logic/identity');

module.exports = function(io){
    io.set("log level", 0);

    io.sockets.on("connection", function (socket) {
        socket.emit('welcome', { hello: 'world' });
        socket.on('disconnect', function () {
            io.sockets.emit('user disconnected');
        });

        socket.on('/api/identity/login', function (data, fn) {
            Identity.Login(data.body, data.cookies, null, function(data){
                fn(data);
            });
        });


    });
};