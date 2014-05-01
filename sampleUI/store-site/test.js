
var Async = require('async');
var appConfig = require('./configs');
var CatalogDataProvider = require('store-data-provider').Catalog;

appConfig.Init(["prod","prod","prod"]);

var GetOffers = function(){

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

                for(var i = 0; i < resultList.length; ++i){
                    var revisionObj = JSON.parse(resultList[i].Data);
                    var locales = revisionObj.locales["en_US"];

                    var offerId = revisionObj.offer.id;
                    var name = locales.name;
                    var longDescription = locales.longDescription;
                    var shortDescription = locales.shortDescription;
                    var thumb = locales.images != null && locales.images.thumb != null ? locales.images.thumb.href : "";
                    var mainThumb = locales.images != null && locales.images.main != null ? locales.images.main.href : "";
                    var price = 0;

                    if(revisionObj.price != null){
                        if(revisionObj.price.priceType == "CUSTOM") {
                            if(revisionObj.price.prices != null && typeof(revisionObj.price.prices.USD) != "undefined"){
                                price = revisionObj.price.prices.USD;
                            }
                        }else{

                            (function(offerId, priceTierId){
                                priceTiersFunList.push(function(cb){
                                    dataProvider.GetPriceTierById(priceTierId, function(tierResult){
                                        if(tierResult.StatusCode.toString()[0] == 2){
                                            cb(null, {offerId: offerId, result: tierResult});
                                        }else{
                                            cb("Can't get price tiers! tierId: " + priceTierId, {offerId: offerId, result: tierResult});
                                        }
                                    });
                                });
                            })(revisionObj.offer.id, revisionObj.price.priceTier.id);
                        }
                    }

                    fillProductFromRevision(offerId, name, price, longDescription, shortDescription, thumb, mainThumb);

                    // get price tiers
                    if(priceTiersFunList.length > 0){
                        Async.parallel(priceTiersFunList, function(err, tierResult){

                            if(err){
                                console.log("API call failed!");
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
                    }
                }

                //cb(null, resultList);
            });
        }
    ], function(err, result){
        if(err){
            console.log("API call failed!");
        }

        console.log("Result List: ", resultList);
    });
};


GetOffers();