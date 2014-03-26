'use strict';

/* Controllers */
var app = angular.module('catalog.controllers', ['ui.bootstrap', 'checklist-model']);

// Clear browser cache (in development mode)
//
// http://stackoverflow.com/questions/14718826/angularjs-disable-partial-caching-on-dev-machine
app.run(function ($rootScope, $templateCache) {
  $rootScope.$on('$viewContentLoaded', function () {
    $templateCache.removeAll();
  });
});

app.controller('OfferListCtrl', ['$scope', 'OffersFactory', '$routeParams',
  function($scope, OffersFactory, $routeParams) {
  	  $scope.offers = OffersFactory.query($routeParams);
  }]);

app.controller('OfferCreationCtrl', ['$scope', 'OffersFactory', '$location',
    function($scope, OffersFactory, $location) {
        $scope.createOffer = function () {
            $scope.submitted = true;
            OffersFactory.create($scope.offer, function(offer){
                $location.path('/offers/' + offer.self.id);
            });
        };

        $scope.cancel = function () {
            $location.path('/offers');
        };

        var init = function() {
            $scope.offer = {};
            $scope.offer.items = [];
            $scope.offer.categories = [];
            $scope.offer.properties = {};
            $scope.offer.eligibleCountries = [];
            $scope.selectedItems = {};
        };

        init();
    }]);

app.controller('OfferEditCtrl',
    ['$scope', 'OffersFactory', 'ItemsFactory', 'MetaFactory', 'AttributesFactory', 'PriceTiersFactory', 'PriceTierFactory','$routeParams',
        function($scope, OffersFactory, ItemsFactory, MetaFactory, AttributesFactory, PriceTiersFactory, PriceTierFactory, $routeParams) {
            $scope.addItem = function(item) {
                if (typeof $scope.selectedItems[item.self.id] == "undefined") {
                    $scope.selectedItems[item.self.id] = item;
                    $scope.offer.items.push({itemId:item.self});
                }
            };
            $scope.removeItem = function(item) {
                delete $scope.selectedItems[item.self.id];

                for (var i=0; i<$scope.offer.items.length; i++) {
                    if ($scope.offer.items[i].itemId.id == item.self.id) {
                        break;
                    }
                }
                $scope.offer.items.splice(i, 1);
            };

            $scope.saveItems = function() {
                $scope.offer.items = [];
                Object.keys( $scope.selectedItems ).forEach(function( key ) {
                    console.log(key);
                    $scope.offer.items.push($scope.selectedItems[key].self);
                });
            };
            $scope.removePrice = function(countryCode) {
                delete $scope.offer.prices[countryCode];
            };
            $scope.totalItems = function() {
                return Object.keys( $scope.selectedItems).length;
            };
            $scope.updateDeveloper = function() {
                $scope.offer.developer.href="http://localhost:8083/rest/api/users/" + $scope.offer.developer.id;
            };
            $scope.updateCategories = function() {
                $scope.offer.categories = [$scope.selectedCategory];
            };
            $scope.updateGenres = function() {
                $scope.offer.genres = [$scope.selectedGenre];
            };
            $scope.updatePriceTier = function(priceTier) {
                $scope.offer.priceTier = priceTier.self;
                $scope.selectedTier = priceTier;
            };
            $scope.addPrice = function(country) {
                if (angular.isUndefined($scope.offer)) {
                    $scope.offer = {};
                }
                if (angular.isUndefined($scope.offer.prices)) {
                    $scope.offer.prices = {};
                }

                $scope.offer.prices[country.code] = {"currency": country.currency};
            };
            $scope.selectAllCountries = function() {
                $scope.offer.eligibleCountries = [];
                $scope.countries.forEach(function(country) {
                    if (country.code != "DEFAULT") {
                        $scope.offer.eligibleCountries.push(country.code);
                    }
                });
            };
            $scope.deselectAllCountries = function() {
                $scope.offer.eligibleCountries = [];
            };

            $scope.updateSelectedPriceTier = function(priceTier) {
                console.log(priceTier);
                $scope.selectedTier = priceTier;
            };

            $scope.updatePriceTier = function() {
                $scope.offer.priceTier = $scope.priceTiers[$scope.selectedTierIndex].self;
                $scope.selectedTier = $scope.priceTiers[$scope.selectedTierIndex];
            };

            $scope.updatePriceType = function(priceType) {
                if (priceType=="TierPricing") {
                    $scope.offer.prices = {};
                } else {
                    $scope.offer.priceTier = {};
                    $scope.selectedTier = {};
                }
            };

            $scope.isCollapsed = true;
            $scope.items = ItemsFactory.query({status: "Released"});
            $scope.priceTiers = PriceTiersFactory.query();
            $scope.categoryAttributes = AttributesFactory.query({type: "Category"});
            $scope.genreAttributes = AttributesFactory.query({type: "Genre"});
            $scope.offers = OffersFactory.query($routeParams);
            $scope.countries = MetaFactory.countries;
        }]);

app.controller('OfferAdminListCtrl', ['$scope', 'OffersFactory',
    function($scope, OffersFactory) {
        $scope.updateList = function () {
            if ($scope.showStatus=="PendingReview") {
                $scope.offers = OffersFactory.query({status: 'PendingReview'});
            } else {
                $scope.offers = OffersFactory.query({status: 'Released'});
            }
        };

        $scope.offers = OffersFactory.query({status: 'PendingReview'});
    }]);

app.controller('OfferResponseCtrl', ['$scope', '$routeParams', 'OfferResponse',
    function($scope, $routeParams, OfferResponse) {
        $scope.response = OfferResponse.data;
        $scope.offerId = $routeParams.id;
    }]);

app.controller('OfferDetailCtrl', ['$scope', 'OfferFactory', 'ItemFactory', 'MetaFactory', 'AttributesFactory', 'PriceTierFactory', '$routeParams', '$location','OfferResponse',
    function($scope, OfferFactory, ItemFactory, MetaFactory, AttributesFactory, PriceTierFactory, $routeParams, $location, OfferResponse) {
        console.log("OfferDetailCtrl");
        console.log($routeParams);

        $scope.updateOffer = function () {
            $scope.offer.status="Design";
            OfferFactory.update({id: $routeParams.id}, $scope.offer, function(){
                OfferResponse.data = "Offer updated successfully!";
                $location.path('/offers/' + $routeParams.id + '/response');
            });
        };

        $scope.releaseOffer = function () {
            $scope.offer.status="Released";
            OfferFactory.update({id: $routeParams.id}, $scope.offer, function(){
                OfferResponse.data = "Offer released successfully!";
                $location.path('/offers/' + $routeParams.id + '/response');
            });
        };

        $scope.reviewOffer = function () {
            $scope.offer.status="PendingReview";

            OfferFactory.update({id: $routeParams.id}, $scope.offer, function(){
                OfferResponse.data = "Offer submitted for review!";
                $location.path('/offers/' + $routeParams.id + '/response');
            });
        };

        $scope.rejectOffer = function () {
            $scope.offer.status="Rejected";
            OfferFactory.update({id: $routeParams.id}, $scope.offer, function(){
                OfferResponse.data = "Offer rejected successfully!";
                $location.path('/offers/' + $routeParams.id + '/response');
            });
        };

        $scope.cancel = function () {
            $location.path('/offers/' + $routeParams.id);
        };

        $scope.offer = OfferFactory.query($routeParams, function(offer){
           $scope.selectedTier = PriceTierFactory.query({id:offer.priceTier.id});
           $scope.selectedItems = {};
           for (var i=0; i<offer.items.length; i++) {
               $scope.selectedItems[offer.items[i].itemId.id] = ItemFactory.query({id:offer.items[i].itemId.id});
           }
        });

    }]);

app.controller('ItemListCtrl', ['$scope', 'ItemsFactory', '$routeParams', '$location',
    function($scope, ItemsFactory, $routeParams, $location) {
        $scope.createItem = function () {
            ItemsFactory.create($scope.item, function(item){
                $location.path('/items/' + item.self.id);
            });
        };
        $scope.cancel = function () {
            $location.path('/items');
        };
        $scope.items = ItemsFactory.query($routeParams);
    }]);

app.controller('ItemCreationCtrl', ['$scope', 'MetaFactory', 'ItemsFactory', '$location',
    function($scope, MetaFactory, ItemsFactory, $location) {
        $scope.createItem = function () {
            ItemsFactory.create($scope.item, function(item){
                $location.path('/items/' + item.self.id);
            });
        };

        $scope.cancel = function () {
            $location.path('/items');
        };

        $scope.metaDefinitions = MetaFactory.itemMeta;

        var init = function() {
            $scope.item = {};
            $scope.item.properties = {};
            Object.keys($scope.metaDefinitions).forEach(function(key) {
                $scope.item.properties[key] = "";
                if ($scope.metaDefinitions[key].controlType == "MULTI_SELECT") {
                    $scope.item.properties[key] = [];
                }
            });
        };

        init();
    }]);

app.controller('ItemEditCtrl', ['$scope', 'ItemsFactory', 'MetaFactory', '$routeParams', '$location',
    function($scope, ItemsFactory, MetaFactory, $routeParams, $location) {
        $scope.updateDeveloper = function() {
            $scope.item.developer.href="http://localhost:8083/rest/api/users/" + $scope.item.developer.id;
        };

        $scope.metaDefinitions = MetaFactory.itemMeta;
        $scope.items = ItemsFactory.query($routeParams);
        $scope.itemTypes = MetaFactory.itemTypes;
    }]);

app.controller('ItemDetailCtrl', ['$scope', 'ItemFactory', 'MetaFactory', '$routeParams', '$location','ItemResponse',
    function($scope, ItemFactory, MetaFactory, $routeParams, $location, ItemResponse) {
        console.log("ItemDetailCtrl");
        console.log($routeParams);

        $scope.updateItem = function () {
            $scope.item.status="Design";
            ItemFactory.update({id: $routeParams.id}, $scope.item, function(){
                ItemResponse.data = "Item updated successfully!";
                $location.path('/items/' + $routeParams.id + '/response');
            });
        };

        $scope.releaseItem = function () {
            $scope.item.status="Released";
            ItemFactory.update({id: $routeParams.id}, $scope.item, function(){
                ItemResponse.data = "Item released successfully!";
                $location.path('/items/' + $routeParams.id + '/response');
            });
        };

        $scope.reviewItem = function () {
            $scope.item.status="PendingReview";

            ItemFactory.update({id: $routeParams.id}, $scope.item, function(){
                ItemResponse.data = "Item submitted for review!";
                $location.path('/items/' + $routeParams.id + '/response');
            });
        };

        $scope.rejectItem = function () {
            $scope.item.status="Rejected";
            ItemFactory.update({id: $routeParams.id}, $scope.item, function(){
                ItemResponse.data = "Item rejected successfully!";
                $location.path('/items/' + $routeParams.id + '/response');
            });
        };

        $scope.metaDefinitions = MetaFactory.itemMeta;
        $scope.item = ItemFactory.query($routeParams);
    }]);

app.controller('ItemAdminListCtrl', ['$scope', 'ItemsFactory',
    function($scope, ItemsFactory) {
        $scope.updateList = function () {
            if ($scope.showStatus=="PendingReview") {
                $scope.items = ItemsFactory.query({status: 'PendingReview'});
            } else {
                $scope.items = ItemsFactory.query({status: 'Released'});
            }
        };
        $scope.items = ItemsFactory.query({status: 'PendingReview'});
    }]);

app.controller('ItemResponseCtrl', ['$scope', '$routeParams', 'ItemResponse',
    function($scope, $routeParams, ItemResponse) {
        $scope.response = ItemResponse.data;
        $scope.itemId = $routeParams.id;
    }]);

app.controller('AttributeListCtrl', ['$scope', 'AttributesFactory', '$location',
    function($scope, AttributesFactory, $location) {
        $scope.createAttribute = function () {
            AttributesFactory.create($scope.attribute, function(attribute){
                $location.path('/attributes/' + attribute.self.id);
            });
        };
        $scope.cancel = function () {
            $location.path('/attributes');
        };
        $scope.attributes = AttributesFactory.query();
    }]);

app.controller('AttributeDetailCtrl', ['$scope', 'AttributeFactory', '$routeParams', '$location',
    function($scope, AttributeFactory, $routeParams, $location) {
        console.log("AttributeDetailCtrl");
        console.log($routeParams);

        $scope.cancel = function () {
            $location.path('/attributes/' + $routeParams.id);
        };

        $scope.attribute = AttributeFactory.query($routeParams);
    }]);