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

app.controller('OfferListCtrl', ['$scope', 'OffersFactory', '$routeParams', '$location',
  function($scope, OffersFactory, $routeParams, $location) {
      $scope.createOffer = function () {
          OffersFactory.create($scope.offer, function(offer){
              $location.path('/offers/' + offer.self.id);
          });
      };
      $scope.cancel = function () {
          $location.path('/offers');
      };
  	  $scope.offers = OffersFactory.query($routeParams);
  }]);

app.controller('OfferCreationCtrl', ['$scope', 'OffersFactory', 'ItemsFactory', 'MetaFactory', 'AttributesFactory', '$routeParams', '$location',
    function($scope, OffersFactory, ItemsFactory, MetaFactory, AttributesFactory, $routeParams, $location) {
        $scope.createOffer = function () {
            OffersFactory.create($scope.offer, function(offer){
                $location.path('/offers/' + offer.self.id);
            });
        };
        $scope.cancel = function () {
            $location.path('/offers');
        };

        $scope.addItem = function(item) {
            $scope.selectedItems[item.self.id] = item;
        };
        $scope.removeItem = function(item) {
            delete $scope.selectedItems[item.self.id];
        };
        $scope.saveItems = function() {
            $scope.offer.items = [];
            Object.keys( $scope.selectedItems ).forEach(function( key ) {
                $scope.offer.items.push({itemId: $scope.selectedItems[key].self});
            });
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

        $scope.selectedItems = {};
        $scope.isCollapsed = true;
        $scope.items = ItemsFactory.query({status: "Released"});

        $scope.categoryAttributes = AttributesFactory.query({type: "Category"});
        $scope.genreAttributes = AttributesFactory.query({type: "Genre"});
        $scope.offers = OffersFactory.query($routeParams);
        $scope.countries = MetaFactory.countries;

        var init = function() {
            $scope.offer = {};
            $scope.offer.categories = [];
            $scope.offer.properties = {};
            $scope.offer.eligibleCountries = [];
        };

        init();
    }]);

app.controller('OfferReviewListCtrl', ['$scope', 'OffersFactory', '$location',
    function($scope, OffersFactory, $location) {
        $scope.createOffer = function () {
            OffersFactory.create($scope.offer, function(offer){
                $location.path('/offers/' + offer.self.id);
            });
        };
        $scope.cancel = function () {
            $location.path('/offers');
        };
        $scope.offers = OffersFactory.query({status: 'PendingReview'});
    }]);

app.controller('OfferResponseCtrl', ['$scope', '$routeParams', 'OfferResponse',
    function($scope, $routeParams, OfferResponse) {
        $scope.response = OfferResponse.data;
        $scope.offerId = $routeParams.id;
    }]);

app.controller('OfferDetailCtrl', ['$scope', 'OfferFactory', 'MetaFactory', 'AttributesFactory', '$routeParams', '$location','OfferResponse',
    function($scope, OfferFactory, MetaFactory, AttributesFactory, $routeParams, $location, OfferResponse) {
        console.log("OfferDetailCtrl");
        console.log($routeParams);

        $scope.updateOffer = function () {
            OfferFactory.update({id: $routeParams.id}, $scope.offer, function(offer){
                OfferResponse.data = "Offer updated successfully!";
                $location.path('/offers/' + $routeParams.id + '/response');
            });
        };

        $scope.releaseOffer = function () {
            $scope.offer.status="Released";
            OfferFactory.update({id: $routeParams.id}, $scope.offer, function(offer){
                OfferResponse.data = "Offer released successfully!";
                $location.path('/offers/' + $routeParams.id + '/response');
            });
        };

        $scope.reviewOffer = function () {
            $scope.offer.status="PendingReview";

            OfferFactory.update({id: $routeParams.id}, $scope.offer, function(offer){
                OfferResponse.data = "Offer submitted for review!";
                $location.path('/offers/' + $routeParams.id + '/response');
            });
        };

        $scope.rejectOffer = function () {
            $scope.offer.status="Rejected";
            OfferFactory.update({id: $routeParams.id}, $scope.offer, function(offer){
                OfferResponse.data = "Offer rejected successfully!";
                $location.path('/offers/' + $routeParams.id + '/response');
            });
        };

        $scope.cancel = function () {
            $location.path('/offers/' + $routeParams.id);
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

        $scope.genreAttributes = AttributesFactory.query({type: "Genre"});
        $scope.categoryAttributes = AttributesFactory.query({type: "Category"});
        $scope.offer = OfferFactory.query($routeParams);
        $scope.countries = MetaFactory.countries;
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

app.controller('ItemCreationCtrl', ['$scope', 'ItemsFactory', 'MetaFactory', '$routeParams', '$location',
    function($scope, ItemsFactory, MetaFactory, $routeParams, $location) {
        $scope.createItem = function () {
            ItemsFactory.create($scope.item, function(item){
                $location.path('/items/' + item.self.id);
            });
        };
        $scope.cancel = function () {
            $location.path('/items');
        };
        $scope.updateDeveloper = function() {
            $scope.item.developer.href="http://localhost:8083/rest/api/users/" + $scope.item.developer.id;
        };

        $scope.metaDefinitions = MetaFactory.itemMeta;
        $scope.items = ItemsFactory.query($routeParams);
        $scope.itemTypes = MetaFactory.itemTypes;

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

app.controller('ItemDetailCtrl', ['$scope', 'ItemFactory', 'MetaFactory', '$routeParams', '$location','ItemResponse',
    function($scope, ItemFactory, MetaFactory, $routeParams, $location, ItemResponse) {
        console.log("ItemDetailCtrl");
        console.log($routeParams);

        $scope.updateItem = function () {
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

        $scope.cancel = function () {
            $location.path('/items/' + $routeParams.id);
        };

        $scope.metaDefinitions = MetaFactory.itemMeta;
        $scope.item = ItemFactory.query($routeParams);
        $scope.itemTypes = MetaFactory.itemTypes;
    }]);

app.controller('ItemReviewListCtrl', ['$scope', 'ItemsFactory',
    function($scope, ItemsFactory) {
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

app.controller('MyCtrl2', [function() {

  }]);