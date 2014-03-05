
var http = require('http');
var path = require('path');
var express = require('express');
var partials = require('express-partials');
var lessMiddleware = require('less-middleware');

var routes = require('./routes');
var filter = require('./routes/filter/GlobalFilter');

var app = express();

// all environments
app.set('port', process.env.PORT || 3100);
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');
app.use(partials());
app.use(express.favicon());
app.use(express.logger('dev'));
app.use(express.json());
app.use(express.urlencoded());
app.use(express.methodOverride());
app.use(express.cookieParser('store'));
app.use(express.cookieSession({ key:'store_id', secret: 'store', cookie: { maxAge: 60 * 60 * 1000, httpOnly: true }}));

app.use(filter); // Filter
app.use(app.router);

app.use(lessMiddleware({
  dest: __dirname + '/public',
  src: __dirname + '/public',
  compress: false,
  debug:true
}));
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