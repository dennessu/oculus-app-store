
var IdentityProvider = function(){
    this._Provider = new DataProvider(AppConfig.API.Identity.Config);
};

IdentityProvider.prototype.Discount = function(){
    this._Provider.Discount();
}

IdentityProvider.prototype.Login = function(data, cb){
    this._Provider.Emit(AppConfig.API.Identity.Login, data, cb);
};

IdentityProvider.prototype.Register = function(data, cb){
    this._Provider.Emit(AppConfig.API.Identity.Register, data, cb);
};

