var IdentityProvider = require('store-data-provider').Identity;
var DomainModels = require('../../models/domain');

module.exports = function(data, cookies, query, cb){

    var resultModel = new DomainModels.ResultModel();

    if(!data.remember){
        resultModel.status = DomainModels.ResultStatusEnum.Normal;
    }else{
        resultModel.status = DomainModels.ResultStatusEnum.Exception;
    }
    resultModel.data = "";

    var responseModel = new DomainModels.ResponseModel();
    responseModel.data = resultModel;

    cb(responseModel);
};