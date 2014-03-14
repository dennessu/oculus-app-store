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
  $routeProvider.when('/offers', {templateUrl: 'views/offer-list.html', controller: 'OfferListCtrl'});
  $routeProvider.when('/view2', {templateUrl: 'views/partial2.html', controller: 'MyCtrl2'});
  $routeProvider.otherwise({redirectTo: '/offers'});
}]);
