
var PaymentProvider = function(){
    this._Provider = new DataProvider(AppConfig.APIs.Payment.Config, AppConfig.Runtime.SocketAddress);
    this._Namespace = AppConfig.APIs.Payment.Config.namespace;

    for(var p in AppConfig.APIs.Payment){
        if(p.toLowerCase() === "config") continue;

        (function(self, method){
            self[method] = function(data, cb){
                self._Call(AppConfig.APIs.Payment[method], data, cb);
            };
        })(this, p);
    }
};

PaymentProvider.prototype.Discount = function(){
    this._Provider.Discount();
};
PaymentProvider.prototype._Call = function(api, data, cb){
    var path = this._Namespace + api.path;
    this._Provider.Emit(path, data, function(data){ CartProvider._Callback(data, cb); });
};

PaymentProvider._Callback = function(responseModel, cb){
    Utils.SettingHandler(responseModel.settings);
    cb(responseModel);
};