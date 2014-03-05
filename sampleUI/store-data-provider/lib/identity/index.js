
var RestClient = require('../rest_client');

var Identity = function(host, port){
  this.Host = host;
  this.Port = port;
};

Identity.prototype.PostAuthenticate = function(dataObj, cb){
  /*
   Method: POST
   URL: /rest/authenticate
   Data Type: QueryString
   Content-Type: 'application/x-www-form-urlencoded'
   Request: fs={fs}&username={username}&password={password}
   Response: [302] {redirect_url}
   */

  var options = {
    host: this.Host,
    port: this.Port,
    path: "/rest/authenticate",
    method: 'POST',
    headers:{
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  };

  var client = new RestClient();
  client.Request(options, dataObj, cb);
};

Identity.prototype.GetTokenInfo = function(accessToken, dataObj, cb){
    /*
     Method: GET
     URL: /rest/tokeninfo?access_token={access_token}
     Data Type: QueryString
     Content-Type: 'application/json'
     Request: null
     Response:
     {
     "user_id": "728917210477568936",
     "expire_in": 3252,
     "scopes": "identity openid",
     "client_id": "client"
     }
     */

    var options = {
        host: this.Host,
        port: this.Port,
        path: "/rest/tokeninfo?access_token=" + accessToken,
        method: 'GET',
        headers:{
            'Content-Type': 'application/json'
        }
    };

    var client = new RestClient();
    client.Request(options, dataObj, cb);
}

Identity.prototype.PostTokenInfoByCode = function(dataObj, cb){
    /*
     Method: POST
     URL: /rest/token
     Data Type: QueryString
     Content-Type: 'application/x-www-form-urlencoded'
     Request:
     {
     this.code = "";
     this.grant_type = "authorization_code";
     this.client_id = "client";
     this.client_secret = "secret";
     this.redirect_uri = "";
     }
     Response:
     {
     "access_token": "UGJCWmduajZLWjdMNTdXV0xVUk9qRDcxeHp3R1djY2w7QVQ",
     "token_type": "Bearer",
     "expires_in": 3599,
     "refresh_token": null,
     "id_token": "eyJhbGciOiJIUzI1NiIsImN0eSI6IkpXVCJ9.eyJpc3MiOiJodHRwOi8vMTAuMC4xLjEzMzo4MDgxL3Jlc3QvIiwic3ViIjoiNzI4OTE3MjEwNDc3NTY4OTM2IiwiYXVkIjoiY2xpZW50IiwiZXhwIjoxMzkzNTg3MjYzLCJpYXQiOjEzOTM1ODM2NjMsImF1dGhfdGltZSI6bnVsbCwibm9uY2UiOiIxMjMiLCJjX2hhc2giOm51bGwsImF0X2hhc2giOiI0X2JkOWN4MmtUVFFUeXFaSExmeFJRIn0.Sa0SMRTrhbs6HMtVTpBGjKe8ctEzNeP6o1eg2S_nhaQ"
     }
     */

    var options = {
        host: this.Host,
        port: this.Port,
        path: "/rest/token",
        method: 'POST',
        headers:{
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    };

    var client = new RestClient();
    client.Request(options, dataObj, cb);
}

Identity.prototype.PostCreateAccount = function(dataObj, cb){
  /*
   Method: POST
   URL: /rest/users
   Data Type: JSON
   Content-Type: 'application/json'
   Request:
   {
   "userName": "tom14@ea.com",
   "password": "password",
   "passwordStrength": "WEAK",
   "status": "ACTIVE"
   }
   Response:
   {
   "createdTime": "2014-02-28T07:41:01Z",
   "resourceAge": 0,
   "userName": "tom14@ea.com",
   "status": "ACTIVE",
   "self": {
   "href": "https://xxx.xxx.xxx",
   "id": "728917210477568936"
   }
   }
   */

  var options = {
    host: this.Host,
    port: this.Port,
    path: "/rest/users",
    method: 'POST',
    headers:{
      'Content-Type': 'application/json'
    }
  };

  var client = new RestClient();
  client.Request(options, dataObj, cb);
}

Identity.prototype.PostCreateProfiles = function(userId, dataObj, cb){
  /*
   Method: POST
   URL: /rest/users/{userId}/profiles
   Data Type: JSON
   Content-Type: 'application/json'
   Request:
   {
   "user": {
   "href": "https://xxx.xxx.xxx",
   "id": "728917210477568936"
   },
   "type":"PAYIN",
   "region":"en_US",
   "firstName":"tom",
   "middleName": "",
   "lastName": "tom",
   "dateOfBirth": "2014-02-24T09:45:27Z",
   "locale":"en_US"
   }
   Response:
   {
   "createdTime": "2014-02-28T07:44:32Z",
   "resourceAge": 0,
   "type": "PAYIN",
   "region": "en_US",
   "firstName": "tom14",
   "middleName": "",
   "lastName": "tom14",
   "dateOfBirth": "2010-02-24T09:45:27Z",
   "locale": "en_US",
   "self": {
   "href": "https://xxx.xxx.xxx",
   "id": "728917210477570936"
   },
   "user": {
   "href": "https://xxx.xxx.xxx",
   "id": "728917210477568936"
   }
   }
   */

  var options = {
    host: this.Host,
    port: this.Port,
    path: "/rest/users/"+ userId +"/profiles",
    method: 'POST',
    headers:{
      'Content-Type': 'application/json'
    }
  };

  var client = new RestClient();
  client.Request(options, dataObj, cb);
}

Identity.prototype.GetPayinProfilesByUserId = function(userId, dataObj, cb){
    /*
     Method: GET
     URL: /rest/users/{userId}/profiles?type=PAYIN
     Data Type: JSON
     Content-Type: 'application/json'
     Request: null
     Response:
     {
     "createdTime": "2014-02-28T07:44:32Z",
     "resourceAge": 0,
     "type": "PAYIN",
     "region": "en_US",
     "firstName": "tom14",
     "middleName": "",
     "lastName": "tom14",
     "dateOfBirth": "2010-02-24T09:45:27Z",
     "locale": "en_US",
     "self": {
     "href": "https://xxx.xxx.xxx",
     "id": "728917210477570936"
     },
     "user": {
     "href": "https://xxx.xxx.xxx",
     "id": "728917210477568936"
     }
     }
     */

    var options = {
        host: this.Host,
        port: this.Port,
        path: "/rest/users/"+ userId +"/profiles?type=PAYIN",
        method: 'GET',
        headers:{
            'Content-Type': 'application/json'
        }
    };

    var client = new RestClient();
    client.Request(options, dataObj, cb);
}

Identity.prototype.GetAccount = function(userId, cb){
  /*
   Method: GET
   URL: /identity/users/{userid}
   Data Type: JSON
   Content-Type: 'application/json'
   */

  var options = {
    host: this.Host,
    port: this.Port,
    path: "/identity/users/{userid}",
    method: 'GET',
    headers:{
      'Content-Type': 'application/json'
    }
  };

  var client = new RestClient();
  client.Request(options, null, cb);
}

module.exports = Identity;