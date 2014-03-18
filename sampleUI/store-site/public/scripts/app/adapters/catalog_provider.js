
var CatalogProvider = function(){
    this._Provider = new DataProvider(AppConfig.API.Catalog.Config, AppConfig.SocketAddress);
    this._Namespace = AppConfig.API.Catalog.Config.Namespace;
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

CatalogProvider.prototype.GetProducts = function(data, cb){
    this._Call(AppConfig.API.Catalog.GetProducts, data, cb);
};

CatalogProvider.prototype.GetProductById = function(data, cb){
    this._Call(AppConfig.API.Catalog.GetProductById, data, cb);
};