
var CartProvider = function(){
    this._Provider = new DataProvider(AppConfig.API.Cart.Config, AppConfig.SocketAddress);
    this._Namespace = AppConfig.API.Cart.Config.Namespace;

    for(var p in AppConfig.API.Cart){
        if(p.toLowerCase() === "config") continue;

        (function(self, method){
            self[method] = function(data, cb){
                self._Call(AppConfig.API.Cart[method], data, cb);
            };
        })(this, p);
    }
};

CartProvider.prototype.Discount = function(){
    this._Provider.Discount();
};
CartProvider.prototype._Call = function(api, data, cb){
    var path = this._Namespace + api.Path;
    this._Provider.Emit(path, data, function(data){ CartProvider._Callback(data, cb); });
};

CartProvider._Callback = function(responseModel, cb){
    Utils.SettingHandler(responseModel.settings);
    cb(responseModel);
};