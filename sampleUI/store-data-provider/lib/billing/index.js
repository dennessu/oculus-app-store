
var RestClient = require('../rest_client');

var Billing = function(host, port){
  this.Host = host;
  this.Port = port;
};

Billing.prototype.GetShippingInfo = function(userId, cb){
    /*
     Method: GET
     URL: /rest/users/{userId}/ship-to-info"
     Data Type: QueryString
     Content-Type: none
     Request: null
     Response: 200
     */

  var options = {
    host: this.Host,
    port: this.Port,
    path: "/rest/users/" + userId + "/ship-to-info",
    method: 'GET',
    headers:{
        'Content-Type': 'application/json'
    }
  };

  var client = new RestClient();
  client.Request(options, null, cb);
};

Billing.prototype.GetShippingInfoById = function(shippingId, userId, cb){
    /*
     Method: GET
     URL: /rest/users/{userId}/ship-to-info/{shippingId}"
     Data Type: QueryString
     Content-Type: none
     Request: null
     Response: 200
     */

    var options = {
        host: this.Host,
        port: this.Port,
        path: "/rest/users/" + userId + "/ship-to-info/" + shippingId,
        method: 'GET',
        headers:{
            'Content-Type': 'application/json'
        }
    };

    var client = new RestClient();
    client.Request(options, null, cb);
};

Billing.prototype.PutShippingInfo = function(shippingId, userId, dataObj, cb){
    /*
     Method: PUT
     URL: /rest/users/{userId}/ship-to-info/{shippingId}"
     Data Type: QueryString
     Content-Type: 'application/json'
     Request:
     Response:
     */

    var options = {
        host: this.Host,
        port: this.Port,
        path: "/rest/users/" + userId + "/ship-to-info/" + shippingId,
        method: 'PUT',
        headers:{
            'Content-Type': 'application/json'
        }
    };

    var client = new RestClient();
    client.Request(options, dataObj, cb);
};

Billing.prototype.PostShippingInfo = function(userId, dataObj, cb){
    /*
     Method: POST
     URL: /rest/users/{userId}/ship-to-info"
     Data Type: JSON
     Content-Type: 'application/json'
     Request:
     Response:
     */

    var options = {
        host: this.Host,
        port: this.Port,
        path: "/rest/users/" + userId + "/ship-to-info",
        method: 'POST',
        headers:{
            'Content-Type': 'application/json'
        }
    };

    var client = new RestClient();
    client.Request(options, dataObj, cb);
};

module.exports = Billing;