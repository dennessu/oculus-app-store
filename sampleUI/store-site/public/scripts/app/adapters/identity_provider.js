
var IdentityProvider = function(){
    this._Provider = new DataProvider(AppConfig.API.Identity.Config);
};

IdentityProvider.prototype.Discount = function(){
    this._Provider.Discount();
};

IdentityProvider._Callback = function(responseModel, cb){
    Utils.SettingHandler(responseModel.settings);

    cb(responseModel);
};

IdentityProvider.prototype.Login = function(data, cb){
    this._Provider.Emit(AppConfig.API.Identity.Login, data, function(data){ IdentityProvider._Callback(data, cb); });
};

IdentityProvider.prototype.Register = function(data, cb){
    this._Provider.Emit(AppConfig.API.Identity.Register, data, function(data){ IdentityProvider._Callback(data, cb); });
};

IdentityProvider.prototype.Captcha = function(data, cb){
    this._Provider.Emit(AppConfig.API.Identity.Captcha, data, function(data){ IdentityProvider._Callback(data, cb); });
};

IdentityProvider.prototype.TFA = function(data, cb){
    this._Provider.Emit(AppConfig.API.Identity.TFA, data, function(data){ IdentityProvider._Callback(data, cb); });
};

