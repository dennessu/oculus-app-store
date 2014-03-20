'use strict';


// Declare app level module which depends on filters, and services
var app = angular.module('catalog', [
  'ngRoute',
  'catalog.filters',
  'catalog.services',
  'catalog.directives',
  'catalog.controllers'
]);

app.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/offers/creation', {templateUrl: 'views/offer/offer-creation.html', controller: 'OfferListCtrl'});
  $routeProvider.when('/offers', {templateUrl: 'views/offer/offer-list.html', controller: 'OfferListCtrl'});
  $routeProvider.when('/offers/review', {templateUrl: 'views/offer/offer-list.html', controller: 'OfferReviewListCtrl'});
  $routeProvider.when('/offers/:id', {templateUrl: 'views/offer/offer-detail-view.html', controller: 'OfferDetailCtrl'});
  $routeProvider.when('/offers/:id/response', {templateUrl: 'views/offer/offer-response.html', controller: 'OfferResponseCtrl'});
  $routeProvider.when('/offers/:id/edit', {templateUrl: 'views/offer/offer-detail-edit.html', controller: 'OfferDetailCtrl'});
  $routeProvider.when('/view2', {templateUrl: 'views/partial2.html', controller: 'MyCtrl2'});
  $routeProvider.otherwise({redirectTo: '/offers'});
}]);
