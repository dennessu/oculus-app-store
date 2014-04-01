
var BillingProvider = function(){
    this._Provider = new DataProvider(AppConfig.API.Billing.Config, AppConfig.Runtime.SocketAddress);
    this._Namespace = AppConfig.API.Billing.Config.Namespace;

    for(var p in AppConfig.API.Billing){
        if(p.toLowerCase() === "config") continue;

        (function(self, method){
            self[method] = function(data, cb){
                self._Call(AppConfig.API.Billing[method], data, cb);
            };
        })(this, p);
    }
};

BillingProvider.prototype.Discount = function(){
    this._Provider.Discount();
};
BillingProvider.prototype._Call = function(api, data, cb){
    var path = this._Namespace + api.Path;
    this._Provider.Emit(path, data, function(data){ CartProvider._Callback(data, cb); });
};

BillingProvider._Callback = function(responseModel, cb){
    Utils.SettingHandler(responseModel.settings);
    cb(responseModel);
};