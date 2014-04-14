'use strict';


// Declare app level module which depends on filters, and services
var app = angular.module('catalog', [
  'ngRoute',
  'catalog.filters',
  'catalog.services',
  'catalog.directives',
  'catalog.controllers'
]);

app.config(['$routeProvider', '$httpProvider', function($routeProvider, $httpProvider) {
  $routeProvider.when('/offers/creation', {templateUrl: 'views/offer/offer-creation.html', controller: 'OfferCreationCtrl'});
  $routeProvider.when('/offers', {templateUrl: 'views/offer/offer-list.html', controller: 'OfferListCtrl'});
  //$routeProvider.when('/offers/review', {templateUrl: 'views/offer/offer-list.html', controller: 'OfferReviewListCtrl'});
  $routeProvider.when('/offers/:id', {templateUrl: 'views/offer/offer-detail-view.html', controller: 'OfferDetailCtrl'});
  $routeProvider.when('/offers/:id/response', {templateUrl: 'views/offer/offer-response.html', controller: 'OfferResponseCtrl'});
  $routeProvider.when('/offers/:id/edit', {templateUrl: 'views/offer/offer-detail-edit.html', controller: 'OfferDetailCtrl'});

  $routeProvider.when('/items/creation', {templateUrl: 'views/item/item-creation.html', controller: 'ItemCreationCtrl'});
  $routeProvider.when('/items', {templateUrl: 'views/item/item-list.html', controller: 'ItemListCtrl'});
  //$routeProvider.when('/items/review', {templateUrl: 'views/item/item-list.html', controller: 'ItemReviewListCtrl'});
  $routeProvider.when('/items/:id', {templateUrl: 'views/item/item-detail-view.html', controller: 'ItemDetailCtrl'});
  $routeProvider.when('/items/:id/response', {templateUrl: 'views/item/item-response.html', controller: 'ItemResponseCtrl'});
  $routeProvider.when('/items/:id/edit', {templateUrl: 'views/item/item-detail-edit.html', controller: 'ItemDetailCtrl'});

  $routeProvider.when('/attributes', {templateUrl: '../views/attribute/attribute-list.html', controller: 'AttributeListCtrl'});
  $routeProvider.when('/attributes/:id', {templateUrl: '../views/attribute/attribute-detail.html', controller: 'AttributeDetailCtrl'});

  $routeProvider.when('/login', {templateUrl: 'views/auth/login.html', controller: 'LoginCtrl'});
  $routeProvider.otherwise({redirectTo: '/login'});
}]);
