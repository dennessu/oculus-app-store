
var RestClient = require('../rest_client');

var Order = function(host, port){
  this.Host = host;
  this.Port = port;
};

Order.prototype.PostOrder = function(dataObj, cb){
    /*
     Method: POST
     URL: /rest/orders"
     Data Type: JSON
     Content-Type: none
     Request: null
     Response: null
     */

  var options = {
    host: this.Host,
    port: this.Port,
    path: "/rest/orders",
    method: 'POST',
    headers:{
        'Content-Type': 'application/json'
    }
  };

  var client = new RestClient();
  client.Request(options, dataObj, cb);
};

Order.prototype.GetOrderById = function(orderId, cb){
    /*
     Method: GET
     URL: /rest/orders/{orderId}"
     Data Type: JSON
     Content-Type: none
     Request: null
     Response: null
     */

    var options = {
        host: this.Host,
        port: this.Port,
        path: "/rest/orders/" + orderId,
        method: 'GET',
        headers:{
            'Content-Type': 'application/json'
        }
    };

    var client = new RestClient();
    client.Request(options, null, cb);
};

Order.prototype.PutOrder = function(orderId, dataObj, cb){
    /*
     Method: GET
     URL: /rest/orders/{orderId}"
     Data Type: JSON
     Content-Type: none
     Request: null
     Response: null
     */

    var options = {
        host: this.Host,
        port: this.Port,
        path: "/rest/orders/" + orderId,
        method: 'PUT',
        headers:{
            'Content-Type': 'application/json'
        }
    };

    var client = new RestClient();
    client.Request(options, dataObj, cb);
};

module.exports = Order;