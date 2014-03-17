'use strict';


// Declare app level module which depends on filters, and services
angular.module('catalogAdmin', [
  'ngRoute',
  'catalogAdmin.filters',
  'catalogAdmin.services',
  'catalogAdmin.directives',
  'catalogAdmin.controllers'
]).
config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/offers/creation', {templateUrl: 'views/offer-creation.html', controller: 'OfferListCtrl'});
  $routeProvider.when('/offers', {templateUrl: 'views/offer-list.html', controller: 'OfferListCtrl'});
  $routeProvider.when('/offers/:id', {templateUrl: 'views/offer-detail.html', controller: 'OfferDetailCtrl'});
  $routeProvider.when('/offers/:id/edit', {templateUrl: 'views/offer-detail-edit.html', controller: 'OfferDetailCtrl'});
  $routeProvider.when('/view2', {templateUrl: 'views/partial2.html', controller: 'MyCtrl2'});
  $routeProvider.otherwise({redirectTo: '/offers'});
}]);
