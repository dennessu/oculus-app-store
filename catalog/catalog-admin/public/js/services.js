'use strict';

/* Services */
var services = angular.module('catalog.services', ['ngResource']);

// Demonstrate how to register services
// In this case it is a simple value service.
services.value('version', '0.1');

services.factory('OffersFactory', function ($resource, CONFIG) {
    return $resource(CONFIG.baseUrl + 'offers', {}, {
        query: { method: 'GET' },
        create: { method: 'POST' }
    })
});

services.factory('OfferFactory', function ($resource, CONFIG) {
    return $resource(CONFIG.baseUrl + 'offers/:id', {}, {
        query: { method: 'GET' },
        update: { method: 'PUT', params: {id: '@id'} },
        delete: { method: 'DELETE', params: {id: '@id'} }
    })
});

services.factory('OfferRevisionsFactory', function ($resource, CONFIG) {
    return $resource(CONFIG.baseUrl + 'offer-revisions', {}, {
        query: { method: 'GET' },
        create: { method: 'POST' }
    })
});

services.factory('OfferRevisionFactory', function ($resource, CONFIG) {
    return $resource(CONFIG.baseUrl + 'offer-revisions/:id', {}, {
        query: { method: 'GET' },
        update: { method: 'PUT', params: {id: '@id'} },
        delete: { method: 'DELETE', params: {id: '@id'} }
    })
});

services.factory('ItemsFactory', function ($resource, CONFIG) {
    return $resource(CONFIG.baseUrl + 'items', {}, {
        query: { method: 'GET'},
        create: { method: 'POST' }
    })
});

services.factory('ItemRevisionsFactory', function ($resource, CONFIG) {
    return $resource(CONFIG.baseUrl + 'item-revisions', {}, {
        query: { method: 'GET' },
        create: { method: 'POST' }
    })
});

services.factory('ItemRevisionFactory', function ($resource, CONFIG) {
    return $resource(CONFIG.baseUrl + 'item-revisions/:id', {}, {
        query: { method: 'GET' },
        update: { method: 'PUT', params: {id: '@id'} },
        delete: { method: 'DELETE', params: {id: '@id'} }
    })
});

services.factory('ItemFactory', function ($resource, CONFIG) {
    return $resource(CONFIG.baseUrl + 'items/:id', {}, {
        query: { method: 'GET' },
        update: { method: 'PUT', params: {id: '@id'} },
        delete: { method: 'DELETE', params: {id: '@id'} }
    })
});

services.factory('ItemAttributesFactory', function ($resource, CONFIG) {
    return $resource(CONFIG.baseUrl + 'item-attributes', {}, {
        query: { method: 'GET' },
        create: { method: 'POST' }
    })
});

services.factory('OfferAttributesFactory', function ($resource, CONFIG) {
    return $resource(CONFIG.baseUrl + 'offer-attributes', {}, {
        query: { method: 'GET' },
        create: { method: 'POST' }
    })
});

services.factory('AttributeFactory', function ($resource, CONFIG) {
    return $resource(CONFIG.baseUrl + 'attributes/:id', {}, {
        query: { method: 'GET' },
        update: { method: 'PUT', params: {id: '@id'} },
        delete: { method: 'DELETE', params: {id: '@id'} }
    })
});

services.factory('PriceTiersFactory', function ($resource, CONFIG) {
    return $resource(CONFIG.baseUrl + 'price-tiers', {}, {
        query: { method: 'GET' },
        create: { method: 'POST' }
    })
});

services.factory('PriceTierFactory', function ($resource, CONFIG) {
    return $resource(CONFIG.baseUrl + 'price-tiers/:id', {}, {
        query: { method: 'GET' },
        update: { method: 'PUT', params: {id: '@id'} },
        delete: { method: 'DELETE', params: {id: '@id'} }
    })
});

services.factory('AuthFactory', function ($resource) {
    return $resource('/api/developer', {}, {
        query: { method: 'GET' }
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

services.factory('Developer', function() {
    var developer = {};
    var email = "";
    var userId = 0;
    developer.getEmail = function() {
        return email;
    };
    developer.setEmail = function(newEmail) {
        email = newEmail;
    };
    developer.getId = function() {
        return userId;
    };
    developer.setId = function(newId) {
        userId = newId;
    };
    return developer;
});

services.factory('MetaFactory', function() {
    return  {
        itemTypes: ["PHYSICAL", "DIGITAL", "STORED_VALUE"],
        platforms: ["PC", "MAC", "LINUX"],
        gameModes: ["SINGLE_PLAYER", "MULTI_PLAYER", "CO_OP"],
        locales: ["en_US", "zh_CN"],
        countries: [
            {"code":"AT", "name":"Austria", "currency": "AUD"},
            {"code":"CN", "name":"China", "currency": "RMB"},
            {"code":"CO", "name":"Colombia", "currency": "COP"},
            {"code":"KR", "name":"South Korea", "currency": "KRW"},
            {"code":"UA", "name":"Ukraine", "currency": "UAH"},
            {"code":"US", "name":"United States", "currency": "USD"},
            {"code":"JP", "name":"Japan", "currency": "JPY"},
            {"code":"IN", "name":"India", "currency": "INR"},
            {"code":"TW", "name":"Tai Wan", "currency": "TWD"},
            {"code":"MX", "name":"Mexico", "currency": "MXN"},
            {"code":"HK", "name":"Hong Kong", "currency": "HKD"},
            {"code":"NZ", "name":"New Zealand", "currency": "NZD"},
            {"code":"DEFAULT", "name":"DEFAULT", "currency": "USD"}
        ],
        offerMeta: {
            "shortDescription": { "display": "Short Description", "controlType": "TEXT_INPUT", "required": "false"},
            "longDescription": { "display": "Long Description", "controlType": "TEXT_INPUT", "required": "true"},
            "changeNotes": { "display": "Change Notes", "controlType": "TEXT_INPUT", "required": "false"},
            "mainImage": { "display": "Main Image", "controlType": "TEXT_INPUT", "required": "false"}
        }
    };
});