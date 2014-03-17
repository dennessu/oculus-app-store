'use strict';

/* Controllers */
var app = angular.module('catalogAdmin.controllers', []);

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

app.controller('OfferDetailCtrl', ['$scope', 'OfferFactory', '$routeParams', '$location',
    function($scope, OfferFactory, $routeParams, $location) {
        console.log("OfferDetailCtrl");
        console.log($routeParams);

        $scope.updateOffer = function () {
            OfferFactory.update({id: $routeParams.id}, $scope.offer);
            $location.path('/offers/' + $routeParams.id);
        };

        $scope.cancel = function () {
            $location.path('/offers/' + $routeParams.id);
        };

        $scope.offer = OfferFactory.query($routeParams);
    }]);

app.controller('MyCtrl2', [function() {

  }]);