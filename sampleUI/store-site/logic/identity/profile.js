var IdentityProvider = require('store-data-provider').Identity;
var DomainModels = require('../../models/domain');
var Utils = require('../../utils/utils');

module.exports = function(data, cb){
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var userId = cookies[process.AppConfig.CookiesName.UserId];
    var dataProvider = new IdentityProvider(process.AppConfig.Identity_API_Host, process.AppConfig.Identity_API_Port);

    dataProvider.GetPayinProfilesByUserId(userId, function(resultData){
        var resultModel = new DomainModels.ResultModel();
        if(resultData.StatusCode == 200){
            resultModel.status = DomainModels.ResultStatusEnum.Normal;
        }else{
            resultModel.status = DomainModels.ResultStatusEnum.APIError;
        }
        resultModel.data = resultData.Data;
        cb(Utils.GenerateResponseModel(resultModel));
    });
};