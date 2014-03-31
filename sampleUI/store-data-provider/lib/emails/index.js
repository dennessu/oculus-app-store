
var RestClient = require('../rest_client');

var Emails = function(host, port){
  this.Host = host;
  this.Port = port;
};

Emails.prototype.Send = function(dataObj, cb){
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
    path: "/rest/emails",
    method: 'POST',
    headers:{
        'Content-Type': 'application/json'
    }
  };

  var client = new RestClient();
  client.Request(options, dataObj, cb);
};

module.exports = Emails;