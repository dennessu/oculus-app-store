'use strict';

/* Services */
var services = angular.module('catalogAdmin.services', ['ngResource']);

// Demonstrate how to register services
// In this case it is a simple value service.
services.value('version', '0.1');

services.factory('OffersFactory', function ($http, $resource) {
	$http.defaults.useXDomain = true;
    return $resource('api/offers', {}, {
        query: { method: 'GET', isArray: true },
        create: { method: 'POST' }
    })
});

/*
services.factory('OffersFactory', function ($resource) {
    return $resource('data/offer.json', {}, {
        query: { method: 'GET', isArray: false },
        create: { method: 'POST' }
    })
});
*/