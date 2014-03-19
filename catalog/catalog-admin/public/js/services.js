'use strict';

/* Services */
var services = angular.module('catalog.services', ['ngResource']);

// Demonstrate how to register services
// In this case it is a simple value service.
services.value('version', '0.1');

services.factory('OffersFactory', function ($resource) {
    return $resource('/api/offers', {}, {
        query: { method: 'GET', isArray: true },
        create: { method: 'POST' }
    })
});

services.factory('OfferFactory', function ($resource) {
    return $resource('/api/offers/:id', {}, {
        query: { method: 'GET' },
        update: { method: 'PUT', params: {id: '@id'} },
        delete: { method: 'DELETE', params: {id: '@id'} }
    })
});

services.factory('OfferResponse', function() {
    return {
        data:{}
    };
});
