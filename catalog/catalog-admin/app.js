var express = require('express');
var offers = require('./api-proxy/offers');
var attributes = require('./api-proxy/attributes');
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

app.use("/", express.static(path.join(__dirname, 'public')));

// development only
if ('development' == app.get('env')) {
  app.use(express.errorHandler());
}

app.get('/api/offers', offers.getOffers);
app.post('/api/offers', offers.createOffer);
app.get('/api/offers/:id', offers.getOffer);
app.put('/api/offers/:id', offers.updateOffer);

app.get('/api/attributes', attributes.getAttributes);
app.post('/api/attributes', attributes.createAttribute);
app.get('/api/attributes/:id', attributes.getAttribute);

/*app.engine('.html', require('ejs').renderFile);
app.set('views', __dirname + '/public');
app.get('/admin', function(req, res){
    res.render('admin.html');
});
*/

http.createServer(app).listen(app.get('port'), function(){
  console.log('Express server listening on port ' + app.get('port'));
});
