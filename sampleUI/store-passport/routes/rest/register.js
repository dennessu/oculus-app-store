var DataProvider = require('rest-provider').DataProvider;
var Models = require('rest-provider').Models;

module.exports = function(req, res){
    var model = req.body["model"];
    var redirectUrl = req.cookies[global.AppConfig.CookiesName.RedirectUrl];

    var provider = new DataProvider.Identity(global.AppConfig.Identity_API_Host, global.AppConfig.Identity_API_Port);

    provider.PostAuthorize({}, model, function (result) {
        var resultModel = new Models.SiteDomain.ResultModel();
        var settingArray = new Array();

        if (result.status == 200) {
            resultModel.status = global.AppConfig.ResultStatusEnum.Normal;

        } else if (result.status == 302) { // need redirect, only user login

            var redirectModel = new Models.SiteDomain.RedirectModel();
            redirectModel.target = "_self";
            redirectModel.url = result.headers.location;

            resultModel.status = global.AppConfig.ResultStatusEnum.Redirect;
            resultModel.data = redirectModel;

        } else { // Error
            resultModel.status = global.AppConfig.ResultStatusEnum.APIError;
            resultModel.data = result.data;
        }

        var responseModel = new Models.SiteDomain.ResponseModel();
        responseModel.data = resultModel;
        responseModel.settings = settingArray;

        res.json(200, responseModel);
    });
};