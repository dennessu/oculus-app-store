var Async = require('async');
var Emitter = require('events').EventEmitter;
var CatalogDataProvider = require('store-data-provider').APIProvider.Catalog;
var DomainModels = require('../../models/domain');
var Utils = require('../../utils/utils');

exports.GetOffers = function (data, callback) {
    var body = data.data;
    var cookies = data.cookies;
    var query = data.query;

    var dataProvider = new CatalogDataProvider(process.AppConfig.Catalog_API_Host, process.AppConfig.Catalog_API_Port);

    var resultList = new Array();

    var fillProductFromRevision = function(offerId, name, price, longDescription, shortDescription, thumb, mainThumb){
        for(var i = 0; i < resultList.length; ++i){
            var item = resultList[i];
            if(item.id == offerId){
                item["name"] = name;
                item["price"] = price;
                item["longDescription"] = longDescription;
                item["shortDescription"] = shortDescription;
                item["thumb"] = thumb;
                item["mainThumb"] = mainThumb;
                return;
            }
        }
    };
    var fillProductPrice = function(offerId, price, name){
        for(var i = 0; i < resultList.length; ++i){
            var item = resultList[i];
            if(item.id == offerId){
                item["price"] = price;
                return;
            }
        }
    };

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
            var revisionFunList = [];

            for(var i = 0; i < offers.length; ++i){
                var item = offers[i];

                if(item.currentRevision == null){
                    console.log("[ERROR] No current revision! offerId: ", item.self.id);
                    continue;
                }

                resultList.push({
                    id: item.self.id,
                    revisionId: item.currentRevision.id
                });

                (function(offerId, revisionId){
                    revisionFunList.push(function(cb){
                        dataProvider.GetOfferRevisionById(revisionId, function(result){
                            if(result.StatusCode.toString()[0] == 2){
                                cb(null, result);
                            }else{
                                cb("Can't get offer revision! offerId: " + offerId, result);
                            }
                        });
                    });
                })(item.self.id, item.currentRevision.id);
            }

            Async.parallel(revisionFunList, function(err, resultList){
                if(err){
                    cb("Get revision failed!", resultList);
                }

                var priceTiersFunList = new Array();

                for(var i = 0; i < resultList.length; ++i) {
                    var revisionObj = JSON.parse(resultList[i].Data);
                    var locales = revisionObj.locales["en_US"];

                    var offerId = revisionObj.offer.id;
                    var name = locales.name;
                    var longDescription = locales.longDescription;
                    var shortDescription = locales.shortDescription;
                    var thumb = locales.images != null && locales.images.thumb != null ? locales.images.thumb.href : "";
                    var mainThumb = locales.images != null && locales.images.main != null ? locales.images.main.href : "";
                    var price = 0;

                    if (revisionObj.price != null) {
                        if (revisionObj.price.priceType == "CUSTOM") {
                            if (revisionObj.price.prices != null && typeof(revisionObj.price.prices.USD) != "undefined") {
                                price = revisionObj.price.prices.USD;
                            }
                        } else {
                            (function (tierOfferId, priceTierId) {
                                priceTiersFunList.push(function (cb) {
                                    dataProvider.GetPriceTierById(priceTierId, function (tierResult) {
                                        if (tierResult.StatusCode.toString()[0] == 2) {
                                            cb(null, {offerId: tierOfferId, result: tierResult});
                                        } else {
                                            cb("Can't get price tiers! tierId: " + priceTierId, {offerId: tierOfferId, result: tierResult});
                                        }
                                    });
                                });
                            })(offerId, revisionObj.price.priceTier.id);
                        }
                    }

                    fillProductFromRevision(offerId, name, price, longDescription, shortDescription, thumb, mainThumb);
                }

                // get price tiers
                if(priceTiersFunList.length > 0){
                    Async.parallel(priceTiersFunList, function(err, tierResult){

                        if(err){
                            cb("Can't get price tiers", tierResult);
                        }

                        for(var i = 0; i < tierResult.length; ++i){
                            var tierItem = tierResult[i];

                            var tierObj = JSON.parse(tierItem.result.Data);
                            var price = 0;
                            var name = "";
                            if(tierObj.prices != null && typeof(tierObj.prices.USD) != "undefined"){
                                price = tierObj.prices.USD;
                            }
                            if(tierObj.locales != null && typeof(tierObj.locales.en_US) != "undefined"){
                                name = tierObj.locales.en_US.name;
                            }
                            fillProductPrice(tierItem.offerId, price, name);
                        }

                        cb(null, tierResult);
                    });
                }else{
                    cb(null, resultList);
                }
            });
        }
    ],
        function(err, result){
        var resultModel = new DomainModels.ResultModel();
        var responseModel = new DomainModels.ResponseModel();

        if(err){
            resultModel.status = DomainModels.ResultStatusEnum.APIError;
        }else{
            resultModel.status = DomainModels.ResultStatusEnum.Normal;
            resultModel.data = resultList;
        }
        responseModel.data = resultModel;
        callback(responseModel);
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