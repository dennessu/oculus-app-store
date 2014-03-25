
var IdentityProvider = function(){
    this._Provider = new DataProvider(AppConfig.API.Identity.Config, AppConfig.SocketAddress);
    this._Namespace = AppConfig.API.Identity.Config.Namespace;
};

IdentityProvider.prototype.Discount = function(){
    this._Provider.Discount();
};
IdentityProvider.prototype._Call = function(api, data, cb){
    var path = this._Namespace + api.Path;
    this._Provider.Emit(path, data, function(data){ IdentityProvider._Callback(data, cb); });
};

IdentityProvider._Callback = function(responseModel, cb){
    Utils.SettingHandler(responseModel.settings);
    cb(responseModel);
};

IdentityProvider.prototype.Login = function(data, cb){
    this._Call(AppConfig.API.Identity.Login, data, cb);
};

IdentityProvider.prototype.Captcha = function(data, cb){
    this._Call(AppConfig.API.Identity.Captcha, data, cb);
};

IdentityProvider.prototype.TFA = function(data, cb){
    this._Call(AppConfig.API.Identity.TFA, data, cb);
};

IdentityProvider.prototype.Register = function(data, cb){
    this._Call(AppConfig.API.Identity.Register, data, cb);
};

IdentityProvider.prototype.GetAnonymousUser = function(data, cb){
    this._Call(AppConfig.API.Identity.GetAnonymousUser, data, cb);
};

IdentityProvider.prototype.GetProfile = function(data, cb){
    this._Call(AppConfig.API.Identity.GetProfile, data, cb);
};

IdentityProvider.prototype.PIN = function(data, cb){
    this._Call(AppConfig.API.Identity.PIN, data, cb);
};


