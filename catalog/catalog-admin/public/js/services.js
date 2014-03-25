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

services.factory('ItemsFactory', function ($resource) {
    return $resource('/api/items', {}, {
        query: { method: 'GET', isArray: true },
        create: { method: 'POST' }
    })
});

services.factory('ItemFactory', function ($resource) {
    return $resource('/api/items/:id', {}, {
        query: { method: 'GET' },
        update: { method: 'PUT', params: {id: '@id'} },
        delete: { method: 'DELETE', params: {id: '@id'} }
    })
});

services.factory('AttributesFactory', function ($resource) {
    return $resource('/api/attributes', {}, {
        query: { method: 'GET', isArray: true },
        create: { method: 'POST' }
    })
});

services.factory('AttributeFactory', function ($resource) {
    return $resource('/api/attributes/:id', {}, {
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

services.factory('ItemResponse', function() {
    return {
        data:{}
    };
});

services.factory('MetaFactory', function() {
    return  {
        itemMeta: {
            "shortDescription": { "display": "Short Description", "controlType": "TEXT_INPUT"},
            "longDescription": { "display": "Long Description", "controlType": "TEXT_INPUT"},
            "platform": { "display": "Platform", "controlType": "MULTI_SELECT", "allowedValues":["PC", "Mac", "Linux"]},
            "changeNotes": { "display": "Change Notes", "controlType": "TEXT_INPUT"},
            "website": { "display": "Website", "controlType": "TEXT_INPUT"},
            "gameModes": { "display": "Game Modes", "controlType": "SINGLE_SELECT", "allowedValues":["Single Player", "Multi Player"]}
        },
        itemTypes: ["PHYSICAL", "IAP", "APP"]
    };
});