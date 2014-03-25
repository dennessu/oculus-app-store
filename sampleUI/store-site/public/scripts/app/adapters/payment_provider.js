
var PaymentProvider = function(){
    this._Provider = new DataProvider(AppConfig.API.Payment.Config, AppConfig.SocketAddress);
    this._Namespace = AppConfig.API.Payment.Config.Namespace;

    for(var p in AppConfig.API.Payment){
        if(p.toLowerCase() === "config") continue;

        (function(self, method){
            self[method] = function(data, cb){
                self._Call(AppConfig.API.Payment[method], data, cb);
            };
        })(this, p);
    }
};

PaymentProvider.prototype.Discount = function(){
    this._Provider.Discount();
};
PaymentProvider.prototype._Call = function(api, data, cb){
    var path = this._Namespace + api.Path;
    this._Provider.Emit(path, data, function(data){ CartProvider._Callback(data, cb); });
};

PaymentProvider._Callback = function(responseModel, cb){
    Utils.SettingHandler(responseModel.settings);
    cb(responseModel);
};