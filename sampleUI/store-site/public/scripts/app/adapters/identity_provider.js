
var IdentityProvider = function(){
    this._Provider = new DataProvider(AppConfig.APIs.Identity.Config, AppConfig.Runtime.SocketAddress);
    this._Namespace = AppConfig.APIs.Identity.Config.namespace;

    for(var p in AppConfig.APIs.Identity){
        if(p.toLowerCase() === "config") continue;

        (function(self, method){
            self[method] = function(data, cb){
                self._Call(AppConfig.APIs.Identity[method], data, cb);
            };
        })(this, p);
    }
};

IdentityProvider.prototype.Discount = function(){
    this._Provider.Discount();
};
IdentityProvider.prototype._Call = function(api, data, cb){
    var path = this._Namespace + api.path;
    this._Provider.Emit(path, data, function(data){ IdentityProvider._Callback(data, cb); });
};

IdentityProvider._Callback = function(responseModel, cb){
    Utils.SettingHandler(responseModel.settings);
    cb(responseModel);
};