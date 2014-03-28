
/**
 * Module dependencies.
 */
var http = require('http');
var path = require('path');
var express = require('express');
var partials = require('express-partials');
var routes = require('./routes');
var filter = require('./routes/filter/global_filter');

var app = express();

// all environments
app.set('port', process.env.PORT || 80);
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');
app.use(partials());
app.use(express.favicon());
app.use(express.logger('dev'));
app.use(express.json());
app.use(express.urlencoded());
app.use(express.methodOverride());
app.use(express.cookieParser('auth'));
app.use(express.cookieSession({ secret: 'auth', cookie: { httpOnly:true }}));

app.use(filter); // Filter
app.use(app.router);
app.use(express.static(path.join(__dirname, 'public')));

// development only
if ('development' == app.get('env')) {
  app.use(express.errorHandler());
}

routes(app);

var server = http.createServer(app);
function run(){
    server.listen(app.get('port'), function(){
        console.log('Server '+ process.pid +' listening on port ' + app.get('port'));
    });
}
if(!module.parent){
    run();
}else{
    exports.run = run;
}