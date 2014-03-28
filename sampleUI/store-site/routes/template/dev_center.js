
exports.Index = function(req, res){

    res.render('dev_center/index',
        {
            layout: false,
            title: "Store Demo",
            CatalogManageAPPsUrl: process.AppConfig.CatalogManageAPPsUrl,
            CatalogManageOffersUrl: process.AppConfig.CatalogManageOffersUrl
        });
};