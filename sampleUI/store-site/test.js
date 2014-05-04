
var Async = require('async');
var appConfig = require('./configs');
var Catalog = require('store-data-provider').DataProvider.Catalog;

appConfig.Init(["prod","prod","prod"]);
//appConfig.Init(["dev","dev","dev"]);

var provider = new Catalog(process.AppConfig.Catalog_API_Host, process.AppConfig.Catalog_API_Port);
provider.GetOffers(function(err, result){
    console.log(result);

    console.log("2 --------------------------");
    provider.GetOffers(function(err, result){
        console.log(result);
    });
});

/*
provider.GetOfferById('6BD4FF347CDF', function(err, result){
    console.log(result);

    console.log("2 --------------------------");
    provider.GetOfferById('6BD4FF347CDF', function(err, result){
        console.log(result);
    });
});
    */