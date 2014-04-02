
var EntitlementProvider = function(){
    this._Provider = new DataProvider(AppConfig.APIs.Entitlement.Config, AppConfig.Runtime.SocketAddress);
    this._Namespace = AppConfig.APIs.Entitlement.Config.namespace;

    for(var p in AppConfig.APIs.Entitlement){
        if(p.toLowerCase() === "config") continue;

        (function(self, method){
            self[method] = function(data, cb){
                self._Call(AppConfig.APIs.Entitlement[method], data, cb);
            };
        })(this, p);
    }
};

EntitlementProvider.prototype.Discount = function(){
    this._Provider.Discount();
};
EntitlementProvider.prototype._Call = function(api, data, cb){
    var path = this._Namespace + api.path;
    this._Provider.Emit(path, data, function(data){ CartProvider._Callback(data, cb); });
};

EntitlementProvider._Callback = function(responseModel, cb){
    Utils.SettingHandler(responseModel.settings);
    cb(responseModel);
};