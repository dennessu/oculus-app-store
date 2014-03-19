'use strict';


// Declare app level module which depends on filters, and services
angular.module('catalogAdmin', [
        'ngRoute',
        'catalog.filters',
        'catalog.services',
        'catalog.directives',
        'catalog.controllers'
    ]).
    config(['$routeProvider', function($routeProvider) {
        $routeProvider.when('/offers', {templateUrl: '../views/offer-list.html', controller: 'OfferListCtrl'});
        $routeProvider.when('/offers/review', {templateUrl: '../views/offer-list.html', controller: 'OfferReviewListCtrl'});
        $routeProvider.when('/offers/:id', {templateUrl: '../views/offer-detail-admin.html', controller: 'OfferDetailCtrl'});
        $routeProvider.when('/offers/:id/response', {templateUrl: '../views/offer-response.html', controller: 'OfferResponseCtrl'})
        $routeProvider.when('/view2', {templateUrl: '../views/partial2.html', controller: 'MyCtrl2'});
        $routeProvider.otherwise({redirectTo: '/offers/review'});
    }]);