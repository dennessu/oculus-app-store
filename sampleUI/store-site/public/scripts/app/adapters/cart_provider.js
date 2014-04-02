
var CartProvider = function(){
    this._Provider = new DataProvider(AppConfig.APIs.Cart.Config, AppConfig.Runtime.SocketAddress);
    this._Namespace = AppConfig.APIs.Cart.Config.namespace;

    for(var p in AppConfig.APIs.Cart){
        if(p.toLowerCase() === "config") continue;

        (function(self, method){
            self[method] = function(data, cb){
                self._Call(AppConfig.APIs.Cart[method], data, cb);
            };
        })(this, p);
    }
};

CartProvider.prototype.Discount = function(){
    this._Provider.Discount();
};
CartProvider.prototype._Call = function(api, data, cb){
    var path = this._Namespace + api.path;
    this._Provider.Emit(path, data, function(data){ CartProvider._Callback(data, cb); });
};

CartProvider._Callback = function(responseModel, cb){
    Utils.SettingHandler(responseModel.settings);
    cb(responseModel);
};