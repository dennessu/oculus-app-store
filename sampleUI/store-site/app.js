var http = require('http');
var path = require('path');
var socketIO = require('socket.io');
var express = require('express');
var partials = require('express-partials');
var appConfig = require('./configs');
var routes = require('./routes');
var events = require('./events');
var filter = require("./routes/filters/global_filter");

var app = express();

// init
appConfig.Init(process.argv);

// all environments
app.set('port', process.env.PORT || 3000);
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');
app.use(express.logger('dev'));
app.use(express.json());
app.use(express.urlencoded());
app.use(express.methodOverride());
app.use(partials());
app.use(filter); // Filter
app.use(app.router);
app.use(express.static(path.join(__dirname, 'public')));

// development only
if ('development' == app.get('env')) {
  app.use(express.errorHandler());
}

routes(app);

var server = http.createServer(app);
var io = socketIO.listen(server);
events(io);

function Run(){
    server.listen(app.get('port'), function(){
        console.log('Express server listening on port ' + app.get('port'));
    });
}

if(!module.parent){
    Run();
}else{
    exports.Run = Run;
}
