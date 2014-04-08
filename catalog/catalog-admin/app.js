var express = require('express');
var offers = require('./api-proxy/offers');
var items = require('./api-proxy/items');
var attributes = require('./api-proxy/attributes');
var priceTiers = require('./api-proxy/price-tiers');
var auth = require('./api-proxy/auth');
var http = require('http');
var path = require('path');

var app = express();

// all environments
app.set('port', process.env.PORT || 3000);
app.use(express.favicon());
app.use(express.logger('dev'));
app.use(express.json());
app.use(express.urlencoded());
app.use(express.methodOverride());
app.use(express.bodyParser());
app.use(app.router);
app.use(express.cookieParser());

app.use("/", express.static(path.join(__dirname, 'public')));

// development only
if ('development' == app.get('env')) {
  app.use(express.errorHandler());
}

app.get('/auth', auth.Callback);
app.get('/login', auth.Login);

app.get('/api/offers', offers.getOffers);
app.post('/api/offers', offers.createOffer);
app.get('/api/offers/:id', offers.getOffer);
app.put('/api/offers/:id', offers.updateOffer);

app.get('/api/items', items.getItems);
app.post('/api/items', items.createItem);
app.get('/api/items/:id', items.getItem);
app.put('/api/items/:id', items.updateItem);

app.get('/api/attributes', attributes.getAttributes);
app.post('/api/attributes', attributes.createAttribute);
app.get('/api/attributes/:id', attributes.getAttribute);

app.get('/api/price-tiers', priceTiers.getPriceTiers);
app.post('/api/price-tiers', priceTiers.createPriceTier);
app.get('/api/price-tiers/:id', priceTiers.getPriceTier);

/*app.engine('.html', require('ejs').renderFile);
app.set('views', __dirname + '/public');
app.get('/admin', function(req, res){
    res.render('admin.html');
});
*/

http.createServer(app).listen(app.get('port'), function(){
  console.log('Express server listening on port ' + app.get('port'));
});
