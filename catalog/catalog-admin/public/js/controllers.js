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

app.controller('OfferListCtrl', ['$scope', 'OffersFactory',
  function($scope, OffersFactory) {
  	  $scope.offers = OffersFactory.query();
  }]);

app.controller('OfferDetailCtrl', ['$scope', 'OfferFactory', '$routeParams',
    function($scope, OfferFactory, $routeParams) {
        $scope.offer = OfferFactory.query({id: $routeParams.id});
    }]);

app.controller('MyCtrl2', [function() {

  }]);