/**
 * Created by Haiwei on 2014/4/16.
 */
var http           = require('http');
var express        = require('express');
var morgan         = require('morgan');
var bodyParser     = require('body-parser');
var methodOverride = require('method-override');
var expressSession = require('express-session');
var cookieParser   = require('cookie-parser');
var AppConfig      = require('./configs');
var Routes         = require('./routes');

var app            = express();

// init
AppConfig.Init(process.argv);

app.set('port', process.env.PORT);
app.set('view engine', 'ejs');
app.set('views', __dirname + '/views');
app.use(express.static(__dirname + '/public'));
app.use(morgan('dev'));
app.use(cookieParser());
app.use(expressSession({secret: 'auth'}));
app.use(bodyParser());
app.use(methodOverride());

Routes(app);

http.createServer(app).listen(app.get('port'), function(){
    console.log('Express server listening on port ' + app.get('port'));
});