
var RestClient = require('../rest_client');

var Cart = function(host, port){
  this.Host = host;
  this.Port = port;
};

Cart.prototype.GetPrimaryCart = function(userId, dataObj, cb){
    /*
     Method: GET
     URL: /rest/users/{userId}/carts/primary"
     Data Type: QueryString
     Content-Type: none
     Request: null
     Response: 302
     location: http://54.186.12.35:8082/rest/users/728975415905280681/carts/728983570241536681
     */

  var options = {
    host: this.Host,
    port: this.Port,
    path: "/rest/users/" + userId + "/carts/primary",
    method: 'GET',
    headers:{
        'Content-Type': 'application/json'
    }
  };

  var client = new RestClient();
  client.Request(options, dataObj, cb);
};

Cart.prototype.GetCartByUrl = function(url, dataObj, cb){
    /*
     Method: GET
     URL: http://54.186.12.35:8082/rest/users/728975415905280681/carts/728983570241536681"
     Data Type: QueryString
     Content-Type: 'application/json'
     Request: null
     Response:
     {
     "user": {
     "href": "https://xxx.xxx.xxx",
     "id": "728975415905280681"
     },
     "cartName": "__primary",
     "resourceAge": 1,
     "createdTime": "2014-02-28T12:04:43Z",
     "updatedTime": "2014-02-28T12:05:22Z",
     "offers": [
     {
     "createdTime": "2014-02-28T12:05:22Z",
     "updatedTime": "2014-02-28T12:05:22Z",
     "offer": {
     "id": 30022
     },
     "quantity": 2,
     "selected": true,
     "self": {
     "href": "https://xxx.xxx.xxx",
     "id": "728983570241538681"
     }
     },
     {
     "createdTime": "2014-02-28T12:05:22Z",
     "updatedTime": "2014-02-28T12:05:22Z",
     "offer": {
     "id": 4000
     },
     "quantity": 2,
     "selected": true,
     "self": {
     "href": "https://xxx.xxx.xxx",
     "id": "728983570241537681"
     }
     }
     ],
     "coupons": [],
     "self": {
     "href": "https://xxx.xxx.xxx",
     "id": "728983570241536681"
     }
     }
     */

    var options = {
        host: this.Host,
        port: this.Port,
        path: url,
        method: 'GET',
        headers:{
            'Content-Type': 'application/json'
        }
    };

    var client = new RestClient();
    client.Request(options, dataObj, cb);
};

Cart.prototype.PutCartUpdate = function(userId, cartId, dataObj, cb){
    /*
     Method: PUT
     URL: /rest/users/{userId}/carts/{cartId}"
     Data Type: QueryString
     Content-Type: 'application/json'
     Request:
     {
     "user": {
     "href": "https://xxx.xxx.xxx",
     "id": "728975415905280681"
     },
     "cartName": "__primary",
     "resourceAge": 1,
     "createdTime": "2014-02-28T12:04:43Z",
     "updatedTime": "2014-02-28T12:05:22Z",
     "offers": [
     {
     "createdTime": "2014-02-28T12:05:22Z",
     "updatedTime": "2014-02-28T12:05:22Z",
     "offer": {
     "id": 30022
     },
     "quantity": 2,
     "selected": true,
     "self": {
     "href": "https://xxx.xxx.xxx",
     "id": "728983570241538681"
     }
     },
     {
     "createdTime": "2014-02-28T12:05:22Z",
     "updatedTime": "2014-02-28T12:05:22Z",
     "offer": {
     "id": 4000
     },
     "quantity": 2,
     "selected": true,
     "self": {
     "href": "https://xxx.xxx.xxx",
     "id": "728983570241537681"
     }
     }
     ],
     "coupons": [],
     "self": {
     "href": "https://xxx.xxx.xxx",
     "id": "728983570241536681"
     }
     }

     Response:
     {
     "user": {
     "href": "https://xxx.xxx.xxx",
     "id": "728975415905280681"
     },
     "cartName": "__primary",
     "resourceAge": 1,
     "createdTime": "2014-02-28T12:04:43Z",
     "updatedTime": "2014-02-28T12:05:22Z",
     "offers": [
     {
     "createdTime": "2014-02-28T12:05:22Z",
     "updatedTime": "2014-02-28T12:05:22Z",
     "offer": {
     "id": 30022
     },
     "quantity": 2,
     "selected": true,
     "self": {
     "href": "https://xxx.xxx.xxx",
     "id": "728983570241538681"
     }
     },
     {
     "createdTime": "2014-02-28T12:05:22Z",
     "updatedTime": "2014-02-28T12:05:22Z",
     "offer": {
     "id": 4000
     },
     "quantity": 2,
     "selected": true,
     "self": {
     "href": "https://xxx.xxx.xxx",
     "id": "728983570241537681"
     }
     }
     ],
     "coupons": [],
     "self": {
     "href": "https://xxx.xxx.xxx",
     "id": "728983570241536681"
     }
     }
     */

    var options = {
        host: this.Host,
        port: this.Port,
        path: "/rest/users/" + userId + "/carts/" + cartId,
        method: 'PUT',
        headers:{
            'Content-Type': 'application/json'
        }
    };

    var client = new RestClient();
    client.Request(options, dataObj, cb);
};

module.exports = Cart;