
var BillingProvider = function(){
    this._Provider = new DataProvider(AppConfig.APIs.Billing.Config, AppConfig.Runtime.SocketAddress);
    this._Namespace = AppConfig.APIs.Billing.Config.namespace;

    for(var p in AppConfig.APIs.Billing){
        if(p.toLowerCase() === "config") continue;

        (function(self, method){
            self[method] = function(data, cb){
                self._Call(AppConfig.APIs.Billing[method], data, cb);
            };
        })(this, p);
    }
};

BillingProvider.prototype.Discount = function(){
    this._Provider.Discount();
};
BillingProvider.prototype._Call = function(api, data, cb){
    var path = this._Namespace + api.path;
    this._Provider.Emit(path, data, function(data){ CartProvider._Callback(data, cb); });
};

BillingProvider._Callback = function(responseModel, cb){
    Utils.SettingHandler(responseModel.settings);
    cb(responseModel);
};