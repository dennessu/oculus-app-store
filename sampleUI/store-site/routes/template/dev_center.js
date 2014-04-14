
exports.Index = function(req, res){

    res.render('dev_center/index',
        {
            layout: false,
            title: "Oculus VR Store",
            CatalogManageAPPsUrl: process.AppConfig.UrlConstants.CatalogManageAPPsUrl,
            CatalogManageOffersUrl: process.AppConfig.UrlConstants.CatalogManageOffersUrl
        });
};