var Async = require('async');
var Emitter = require('events').EventEmitter;
var CatalogDataProvider = require('store-data-provider').Catalog;
var DomainModels = require('../../models/domain');
var Utils = require('../../utils/utils');


exports.GetOffers = function(data, cb){
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var dataProvider = new CatalogDataProvider(process.AppConfig.Catalog_API_Host, process.AppConfig.Catalog_API_Port);

    Async.waterfall([
        // get offers
        function(cb){
            dataProvider.GetOffers(function(result){
                if(result.StatusCode.toString()[0] == 2){
                    var offers = JSON.parse(result.Data)[process.AppConfig.FieldNames.Results];
                    cb(null, offers);
                }else{
                    cb("Can't get offers", result);
                }
            });
        },
        // get offer revisions
        function(offers, cb){




        }
    ], function(err, result){

    });




};

exports.GetProducts = function (data, cb) {
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var dataProvider = new CatalogDataProvider(process.AppConfig.Catalog_API_Host, process.AppConfig.Catalog_API_Port);

    if (body !=undefined && body != null && body["id"] != undefined && body["id"] != null) {
        dataProvider.GetOfferById(body.id, function (result) {
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
        dataProvider.GetOffers(function (result) {
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

exports.GetDownloadLinks = function(data, callback){
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;
    var offerId = body["productId"];
    var itemsCount = 0;
    var itemsIndex = 0;

    var callBackResult = new Array(); //{name: "", link: ""}
    var emitter = new Emitter();
    var dataProvider = new CatalogDataProvider(process.AppConfig.Catalog_API_Host, process.AppConfig.Catalog_API_Port);

      dataProvider.GetOfferById(offerId, function(result){
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
