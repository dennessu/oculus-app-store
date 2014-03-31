
var EntitlementProvider = function(){
    this._Provider = new DataProvider(AppConfig.API.Entitlement.Config, AppConfig.Runtime.SocketAddress);
    this._Namespace = AppConfig.API.Entitlement.Config.Namespace;

    for(var p in AppConfig.API.Entitlement){
        if(p.toLowerCase() === "config") continue;

        (function(self, method){
            self[method] = function(data, cb){
                self._Call(AppConfig.API.Entitlement[method], data, cb);
            };
        })(this, p);
    }
};

EntitlementProvider.prototype.Discount = function(){
    this._Provider.Discount();
};
EntitlementProvider.prototype._Call = function(api, data, cb){
    var path = this._Namespace + api.Path;
    this._Provider.Emit(path, data, function(data){ CartProvider._Callback(data, cb); });
};

EntitlementProvider._Callback = function(responseModel, cb){
    Utils.SettingHandler(responseModel.settings);
    cb(responseModel);
};