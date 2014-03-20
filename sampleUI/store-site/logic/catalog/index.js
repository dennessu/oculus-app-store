var CatalogDataProvider = require('store-data-provider').Catalog;
var DomainModels = require('../../models/domain');

exports.Products = function (data, cb) {
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var dataProvider = new CatalogDataProvider(process.AppConfig.Catalog_API_Host, process.AppConfig.Catalog_API_Port);

    if (body !=undefined && body != null && body["id"] != undefined && body["id"] != null) {
        dataProvider.GetOfferById(body.id, null, function (result) {
            var resultModel = new DomainModels.ResultModel();
            var responseModel = new DomainModels.ResponseModel();

            if (result.StatusCode == 200) {
                resultModel.status = DomainModels.ResultStatusEnum.Normal;
            } else {
                // error
                resultModel.status = DomainModels.ResultStatusEnum.APIError;
            }
            resultModel.data = result.Data;
            responseModel.data = resultModel;

            cb(responseModel);
        });
    } else {
        dataProvider.GetOffers(null, function (result) {
            var resultModel = new DomainModels.ResultModel();
            var responseModel = new DomainModels.ResponseModel();

            if (result.StatusCode == 200) {
                resultModel.status = DomainModels.ResultStatusEnum.Normal;
                resultModel.data = result.Data;
            } else {
                // error
                resultModel.status = DomainModels.ResultStatusEnum.APIError;
                resultModel.data = result.Data;
            }
            responseModel.data = resultModel;
            cb(responseModel);
        });
    }
};