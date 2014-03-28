
var RestClient = require('../rest_client');

var Payment = function(host, port){
  this.Host = host;
  this.Port = port;
};

Payment.prototype.GetPaymentInstruments = function(userId, cb){
    /*
     Method: GET
     URL: /users/{userId}/payment-instruments/search
     Data Type: QueryString
     Content-Type: none
     Request: null
     Response: 200
     */

  var options = {
    host: this.Host,
    port: this.Port,
    path: "/rest/users/" + userId + "/payment-instruments", //TODO: Product Edit
    method: 'GET',
    headers:{
        'Content-Type': 'application/json'
    }
  };

  var client = new RestClient();
  client.Request(options, null, cb);
};

Payment.prototype.GetPaymentInstrumentsById = function(paymentId, userId, cb){
    /*
     Method: GET
     URL: /users/{userId}/payment-instruments/{paymentInstrumentId}
     Data Type: QueryString
     Content-Type: none
     Request: null
     Response: 200
     */

    var options = {
        host: this.Host,
        port: this.Port,
        path: "/rest/users/" + userId + "/payment-instruments/" + paymentId,
        method: 'GET',
        headers:{
            'Content-Type': 'application/json'
        }
    };

    var client = new RestClient();
    client.Request(options, null, cb);
};

Payment.prototype.PutPaymentInstruments = function(paymentId, userId, dataObj, cb){
    /*
     Method: PUT
     URL: /users/{userId}/payment-instruments/{paymentInstrumentId}
     Data Type: QueryString
     Content-Type: 'application/json'
     Request:
     Response:
     */

    var options = {
        host: this.Host,
        port: this.Port,
        path: "/rest/users/" + userId + "/payment-instruments/" + paymentId,
        method: 'PUT',
        headers:{
            'Content-Type': 'application/json'
        }
    };

    var client = new RestClient();
    client.Request(options, dataObj, cb);
};

Payment.prototype.PostPaymentInstruments = function(userId, dataObj, cb){
    /*
     Method: POST
     URL: /users/{userId}/payment-instruments
     Data Type: JSON
     Content-Type: 'application/json'
     Request:
     Response:
     */

    var options = {
        host: this.Host,
        port: this.Port,
        path: "/rest/users/" + userId + "/payment-instruments",
        method: 'POST',
        headers:{
            'Content-Type': 'application/json'
        }
    };

    var client = new RestClient();
    client.Request(options, dataObj, cb);
};

Payment.prototype.DeltePaymentInstruments = function(paymentId, userId, cb){
    /*
     Method: DELETE
     URL: /users/{userId}/payment-instruments/{paymentInstrumentId}
     Data Type: QueryString
     Content-Type: none
     Request: null
     Response: 200
     */

    var options = {
        host: this.Host,
        port: this.Port,
        path: "/rest/users/" + userId + "/payment-instruments/" + paymentId,
        method: 'DELETE', //TODO: DELETE
        headers:{
            'Content-Type': 'application/json'
        }
    };

    var client = new RestClient();
    client.Request(options, null, cb);
};

module.exports = Payment;