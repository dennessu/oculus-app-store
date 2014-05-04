var IdentityProvider = require('store-data-provider').APIProvider.Identity;
var DomainModels = require('../../models/domain');

module.exports = function(data, cb){
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var resultModel = new DomainModels.ResultModel();
    resultModel.status = DomainModels.ResultStatusEnum.Normal;
    var responseModel = new DomainModels.ResponseModel();
    responseModel.data = resultModel;

    cb(responseModel);
};