var C = require('../config/configuration');

var SessionStore = function(req, res){
    this.Request = req;
    this.Response = res;
};

SessionStore.prototype.Set = function(key, value){
    this.Response.cookie(key, value, { maxAge: C.CookiesTimeout, domain: '', path: '/', secure: false });
};

SessionStore.prototype.Get = function(key){
    return this.Request.cookies[key];
};

SessionStore.prototype.Remove = function(key){
    this.Response.clearCookie(key);
};

module.exports = SessionStore;