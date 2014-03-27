
var RestClient = require('../rest_client');

var Catalog = function(host, port){
  this.Host = host;
  this.Port = port;
};

Catalog.prototype.GetOffers = function(dataObj, cb){
  /*
   Method: GET
   URL: /rest/offers
   Data Type: QueryString
   Content-Type: 'application/json'
   Request: null
   Response: offer list
   */

  var options = {
    host: this.Host,
    port: this.Port,
    path: "/rest/offers",
    method: 'GET',
    headers:{
      'Content-Type': 'application/json'
    }
  };

  var client = new RestClient();
  client.Request(options, dataObj, cb);
};

Catalog.prototype.GetOfferById = function(offerId, dataObj, cb){
    /*
     Method: GET
     URL: /rest/offers/{offerId}
     Data Type: QueryString
     Content-Type: 'application/json'
     Request: null
     Response: offer list
     */

    var options = {
        host: this.Host,
        port: this.Port,
        path: "/rest/offers/" + offerId,
        method: 'GET',
        headers:{
            'Content-Type': 'application/json'
        }
    };

    var client = new RestClient();
    client.Request(options, dataObj, cb);
};

Catalog.prototype.GetItemById = function(itemId, cb){
    /*
     Method: GET
     URL: /rest/offers/{offerId}
     Data Type: QueryString
     Content-Type: 'application/json'
     Request: null
     Response: offer list
     */

    var options = {
        host: this.Host,
        port: this.Port,
        path: "/rest/items/" + itemId,
        method: 'GET',
        headers:{
            'Content-Type': 'application/json'
        }
    };

    var client = new RestClient();
    client.Request(options, null, cb);
};


module.exports = Catalog;