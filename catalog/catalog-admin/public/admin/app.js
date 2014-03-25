'use strict';


// Declare app level module which depends on filters, and services
var app = angular.module('catalogAdmin', [
        'ngRoute',
        'catalog.filters',
        'catalog.services',
        'catalog.directives',
        'catalog.controllers'
    ]);

app.config(['$routeProvider', function($routeProvider) {
    $routeProvider.when('/offers', {templateUrl: '../views/offer/offer-list.html', controller: 'OfferListCtrl'});
    $routeProvider.when('/offers/review', {templateUrl: '../views/offer/offer-list.html', controller: 'OfferReviewListCtrl'});
    $routeProvider.when('/offers/:id', {templateUrl: '../views/offer/offer-detail-admin.html', controller: 'OfferDetailCtrl'});
    $routeProvider.when('/offers/:id/response', {templateUrl: '../views/offer/offer-response.html', controller: 'OfferResponseCtrl'})

    $routeProvider.when('/items', {templateUrl: '../views/item/item-list.html', controller: 'ItemListCtrl'});
    $routeProvider.when('/items/review', {templateUrl: '../views/item/item-list.html', controller: 'ItemReviewListCtrl'});
    $routeProvider.when('/items/:id', {templateUrl: '../views/item/item-detail-admin.html', controller: 'ItemDetailCtrl'});
    $routeProvider.when('/items/:id/response', {templateUrl: '../views/item/item-response.html', controller: 'ItemResponseCtrl'})

    $routeProvider.when('/attributes', {templateUrl: '../views/attribute/attribute-list.html', controller: 'AttributeListCtrl'});
    $routeProvider.when('/attributes/creation', {templateUrl: '../views/attribute/attribute-creation.html', controller: 'AttributeListCtrl'});
    $routeProvider.when('/attributes/:id', {templateUrl: '../views/attribute/attribute-detail.html', controller: 'AttributeDetailCtrl'});

    $routeProvider.when('/view2', {templateUrl: '../views/partial2.html', controller: 'MyCtrl2'});
    $routeProvider.otherwise({redirectTo: '/offers/review'});
    }]);