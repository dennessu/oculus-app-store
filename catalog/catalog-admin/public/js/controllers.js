'use strict';

/* Controllers */
var app = angular.module('catalog.controllers', []);

// Clear browser cache (in development mode)
//
// http://stackoverflow.com/questions/14718826/angularjs-disable-partial-caching-on-dev-machine
app.run(function ($rootScope, $templateCache) {
  $rootScope.$on('$viewContentLoaded', function () {
    $templateCache.removeAll();
  });
});

app.controller('OfferListCtrl', ['$scope', 'OffersFactory', '$location',
  function($scope, OffersFactory, $location) {
      $scope.createOffer = function () {
          OffersFactory.create($scope.offer, function(offer){
              $location.path('/offers/' + offer.self.id);
          });
      };
      $scope.cancel = function () {
          $location.path('/offers');
      };
  	  $scope.offers = OffersFactory.query();
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

app.controller('OfferDetailCtrl', ['$scope', 'OfferFactory', '$routeParams', '$location','OfferResponse',
    function($scope, OfferFactory, $routeParams, $location, OfferResponse) {
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

        $scope.offer = OfferFactory.query($routeParams);
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