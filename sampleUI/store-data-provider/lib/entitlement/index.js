
var RestClient = require('../rest_client');

var Entitlement = function(host, port){
  this.Host = host;
  this.Port = port;
};

Entitlement.prototype.GetEntitlements = function(userId, devId, type, cb){
    /*
     Method: GET
     URL: /rest/users/{userId}/entitlements?developerId={devId}&type={type} //DEVELOPER
     Data Type: QueryString
     Content-Type: none
     Request: null
     Response: 200
     */

  var options = {
    host: this.Host,
    port: this.Port,
    path: "/rest/users/"+ userId +"/entitlements?developerId="+ devId +"&type=" + type,
    method: 'GET',
    headers:{
        'Content-Type': 'application/json'
    }
  };

  var client = new RestClient();
  client.Request(options, null, cb);
};

Entitlement.prototype.GetEntitlementsByUserId = function(userId, cb){
    /*
     Method: GET
     URL: /rest/users/{userId}/entitlements
     Data Type: QueryString
     Content-Type: none
     Request: null
     Response: 200
     */

    var options = {
        host: this.Host,
        port: this.Port,
        path: "/rest/users/"+ userId +"/entitlements",
        method: 'GET',
        headers:{
            'Content-Type': 'application/json'
        }
    };

    var client = new RestClient();
    client.Request(options, null, cb);
};

Entitlement.prototype.PostEntitlement = function(dataObj, cb){
    /*
     Method: POST
     URL: /rest/users/{userId}/entitlements?developerId={devId}
     Data Type: JSON
     Content-Type: 'application/json'
     Request:
     Response:
     */

    var options = {
        host: this.Host,
        port: this.Port,
        path: "/rest/entitlements",
        method: 'POST',
        headers:{
            'Content-Type': 'application/json'
        }
    };

    var client = new RestClient();
    client.Request(options, dataObj, cb);
};

module.exports = Entitlement;