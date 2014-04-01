
var CatalogProvider = function(){
    this._Provider = new DataProvider(AppConfig.API.Catalog.Config, AppConfig.Runtime.SocketAddress);
    this._Namespace = AppConfig.API.Catalog.Config.Namespace;

    for(var p in AppConfig.API.Catalog){
        if(p.toLowerCase() === "config") continue;

        (function(self, method){
            self[method] = function(data, cb){
                self._Call(AppConfig.API.Catalog[method], data, cb);
            };
        })(this, p);
    }
};

CatalogProvider.prototype.Discount = function(){
    this._Provider.Discount();
};
CatalogProvider.prototype._Call = function(api, data, cb){
    var path = this._Namespace + api.Path;
    this._Provider.Emit(path, data, function(data){ CatalogProvider._Callback(data, cb); });
};

CatalogProvider._Callback = function(responseModel, cb){
    Utils.SettingHandler(responseModel.settings);
    cb(responseModel);
};