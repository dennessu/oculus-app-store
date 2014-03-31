var Async = require('async');
var Emitter = require('events').EventEmitter;
var CatalogDataProvider = require('store-data-provider').Catalog;
var DomainModels = require('../../models/domain');
var Utils = require('../../utils/utils');

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

exports.GetDownloadLinksByOfferId = function(data, callback){
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;
    var offerId = body["productId"];
    var itemsCount = 0;
    var itemsIndex = 0;

    // get offer
    // get offer items
    // get

    var callBackResult = new Array(); //{name: "", link: ""}
    var emitter = new Emitter();
    var dataProvider = new CatalogDataProvider(process.AppConfig.Catalog_API_Host, process.AppConfig.Catalog_API_Port);

      dataProvider.GetOfferById(offerId, null, function(result){
          if(result.StatusCode == 200){
              var offer = JSON.parse(result.Data);
              if(offer["results"] != undefined && offer["results"].length > 0){
                  itemsCount = offer.results.length;
                  offer.results.forEach(function(item){
                      dataProvider.GetItemById(item.itemId.id, function(itemResult){
                          if(itemResult.StatusCode == 200){
                              var itemObj = JSON.parse(itemResult.Data);
                              emitter.emit("AppendLink", itemObj.name, itemObj.properties["downloadLink"]);
                          }else{
                              var resultModel = new DomainModels.ResultModel;
                              resultModel.status = DomainModels.ResultStatusEnum.Normal;
                              resultModel.data = callBackResult;

                              callback(Utils.GenerateResponseModel(resultModel));
                          }
                      });
                  });
              }else{
                  var resultModel = new DomainModels.ResultModel;
                  resultModel.status = DomainModels.ResultStatusEnum.Normal;
                  resultModel.data = callBackResult;

                  callback(Utils.GenerateResponseModel(resultModel));
              }
          }else{
              var resultModel = new DomainModels.ResultModel;
              resultModel.status = DomainModels.ResultStatusEnum.APIError;
              resultModel.data = result.Data;

              callback(Utils.GenerateResponseModel(resultModel));
          }
      });


    emitter.on("AppendLink", function(name, link){
        if(link != undefined){
            callBackResult.push({name: name, link: link});
        }
        itemsIndex++;

        if(itemsIndex >= itemsCount){
            var resultModel = new DomainModels.ResultModel;
            resultModel.status = DomainModels.ResultStatusEnum.Normal;
            resultModel.data = callBackResult;

            callback(Utils.GenerateResponseModel(resultModel));
        }
    });
};
