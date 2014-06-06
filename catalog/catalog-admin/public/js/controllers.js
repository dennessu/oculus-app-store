'use strict';

/* Controllers */
var app = angular.module('catalog.controllers', ['ui.bootstrap', 'checklist-model', 'ngCookies']);

// Clear browser cache (in development mode)
//
// http://stackoverflow.com/questions/14718826/angularjs-disable-partial-caching-on-dev-machine
app.run(function ($rootScope, $templateCache) {
  $rootScope.$on('$viewContentLoaded', function () {
    $templateCache.removeAll();
  });
});

app.controller('OfferListCtrl', ['$scope', 'OffersFactory', '$routeParams', '$cookies',
  function($scope, OffersFactory, $routeParams, $cookies) {
  	  OffersFactory.query($routeParams, function(offers) {
          $scope.offers = offers.results;
      });
      $scope.user_id=$cookies.user_id;
      $scope.email=$cookies.email;
  }]);

app.controller('OfferEditCtrl',
    ['$scope', 'OffersFactory', 'ItemsFactory', 'MetaFactory', 'AttributesFactory', 'PriceTiersFactory', 'PriceTierFactory','$routeParams',
        function($scope, OffersFactory, ItemsFactory, MetaFactory, AttributesFactory, PriceTiersFactory, PriceTierFactory, $routeParams) {
            $scope.addItem = function(item) {
                if (typeof $scope.selectedItems[item.self.id] == "undefined") {
                    $scope.selectedItems[item.self.id] = item;
                    $scope.offer.items.push({itemId:item.self});
                }
            };
            $scope.removeItem = function(item) {
                delete $scope.selectedItems[item.self.id];

                for (var i=0; i<$scope.offer.items.length; i++) {
                    if ($scope.offer.items[i].itemId.id == item.self.id) {
                        break;
                    }
                }
                $scope.offer.items.splice(i, 1);
            };

            $scope.removePrice = function(countryCode) {
                delete $scope.offer.prices[countryCode];
            };
            $scope.totalItems = function() {
                return Object.keys( $scope.selectedItems).length;
            };
            $scope.updateDeveloper = function() {
                $scope.offer.developer.href="http://localhost:8083/rest/api/users/" + $scope.offer.developer.id;
            };
            $scope.updateCategories = function() {
                $scope.offer.categories = [$scope.selectedCategory];
            };
            $scope.updateGenres = function() {
                $scope.offer.genres = [$scope.selectedGenre];
            };
            $scope.updatePriceTier = function(priceTier) {
                $scope.offer.priceTier = priceTier.self;
                $scope.selectedTier = priceTier;
            };
            $scope.addPrice = function(country) {
                if (angular.isUndefined($scope.offer)) {
                    $scope.offer = {};
                }
                if (angular.isUndefined($scope.offer.prices)) {
                    $scope.offer.prices = {};
                }

                $scope.offer.prices[country.code] = {"currency": country.currency};
            };
            $scope.selectAllCountries = function() {
                $scope.offer.eligibleCountries = [];
                $scope.countries.forEach(function(country) {
                    if (country.code != "DEFAULT") {
                        $scope.offer.eligibleCountries.push(country.code);
                    }
                });
            };
            $scope.deselectAllCountries = function() {
                $scope.offer.eligibleCountries = [];
            };

            $scope.updateSelectedPriceTier = function(priceTier) {
                console.log(priceTier);
                $scope.selectedTier = priceTier;
            };

            $scope.updatePriceTier = function() {
                $scope.offer.priceTier = $scope.priceTiers[$scope.selectedTierIndex].self;
                $scope.selectedTier = $scope.priceTiers[$scope.selectedTierIndex];
            };

            $scope.updatePriceType = function(priceType) {
                if (priceType=="Free") {
                    $scope.offer.priceTier = {};
                    $scope.selectedTier = {};
                    $scope.offer.prices = {};
                }else if (priceType=="TierPricing") {
                    $scope.offer.prices = {};
                } else {
                    $scope.offer.priceTier = {};
                    $scope.selectedTier = {};
                }
            };

            $scope.isCollapsed = true;
            $scope.items = ItemsFactory.query({status: "Released"});
            $scope.priceTiers = PriceTiersFactory.query();
            $scope.categoryAttributes = AttributesFactory.query({type: "Category"});
            $scope.genreAttributes = AttributesFactory.query({type: "Genre"});
            $scope.offers = OffersFactory.query($routeParams);
            $scope.countries = MetaFactory.countries;
        }]);

app.controller('OfferAdminListCtrl', ['$scope', 'OffersFactory',
    function($scope, OffersFactory) {
        $scope.updateList = function () {
            if ($scope.showStatus=="PendingReview") {
                $scope.offers = OffersFactory.query({status: 'PendingReview'});
            } else {
                $scope.offers = OffersFactory.query({status: 'Released'});
            }
        };

        $scope.offers = OffersFactory.query({status: 'PendingReview'});
    }]);

app.controller('OfferResponseCtrl', ['$scope', '$routeParams', 'OfferResponse',
    function($scope, $routeParams, OfferResponse) {
        $scope.response = OfferResponse.data;
        $scope.offerId = $routeParams.id;
    }]);

app.controller('OfferDetailCtrl', ['$scope', 'OfferFactory', 'ItemFactory', 'MetaFactory', 'AttributesFactory', 'PriceTierFactory', '$routeParams', '$location','OfferResponse',
    function($scope, OfferFactory, ItemFactory, MetaFactory, AttributesFactory, PriceTierFactory, $routeParams, $location, OfferResponse) {
        console.log("OfferDetailCtrl");
        console.log($routeParams);

        $scope.updateOffer = function () {
            $scope.offer.status="Design";
            OfferFactory.update({id: $routeParams.id}, $scope.offer, function(){
                OfferResponse.data = "Offer updated successfully!";
                $location.path('/offers/' + $routeParams.id + '/response');
            });
        };

        $scope.releaseOffer = function () {
            $scope.offer.status="Released";
            OfferFactory.update({id: $routeParams.id}, $scope.offer, function(){
                OfferResponse.data = "Offer released successfully!";
                $location.path('/offers/' + $routeParams.id + '/response');
            });
        };

        $scope.reviewOffer = function () {
            $scope.offer.status="PendingReview";

            OfferFactory.update({id: $routeParams.id}, $scope.offer, function(){
                OfferResponse.data = "Offer submitted for review!";
                $location.path('/offers/' + $routeParams.id + '/response');
            });
        };

        $scope.rejectOffer = function () {
            $scope.offer.status="Rejected";
            OfferFactory.update({id: $routeParams.id}, $scope.offer, function(){
                OfferResponse.data = "Offer rejected successfully!";
                $location.path('/offers/' + $routeParams.id + '/response');
            });
        };

        $scope.cancel = function () {
            $location.path('/offers/' + $routeParams.id);
        };

        $scope.selectedItems = {};
        $scope.offerMetaDefinitions = MetaFactory.offerMeta;

        $scope.offer = OfferFactory.query($routeParams, function(offer){
           if (offer.priceType == "TierPricing") {
             $scope.selectedTier = PriceTierFactory.query({id:offer.priceTier.id});
           }

           if (typeof offer.items != "undefined") {
             for (var i=0; i<offer.items.length; i++) {
               $scope.selectedItems[offer.items[i].itemId.id] = ItemFactory.query({id:offer.items[i].itemId.id});
             }
           }

            if (typeof $scope.offer.properties == "undefined") {
                $scope.offer.properties = {};
            }
            Object.keys($scope.offerMetaDefinitions).forEach(function(key) {
                if (typeof $scope.offer.properties[key] == "undefined") {
                    $scope.offer.properties[key] = "";
                    if ($scope.offerMetaDefinitions[key].controlType == "MULTI_SELECT") {
                        $scope.offer.properties[key] = [];
                    }
                }
            });

        });

    }]);

app.controller('ItemListCtrl', ['$scope', 'ItemsFactory', '$routeParams', '$location', '$cookies',
    function($scope, ItemsFactory, $routeParams, $location, $cookies) {
        $scope.createItem = function () {
            ItemsFactory.create($scope.item, function(item){
                $location.path('/items/' + item.self.id);
            });
        };
        $scope.cancel = function () {
            $location.path('/items');
        };
        $scope.user_id=$cookies.user_id;
        ItemsFactory.query($routeParams, function(items) {
            $scope.items = items.results;
        });
    }]);

app.controller('ItemEditCtrl', ['$scope', 'ItemsFactory', 'MetaFactory', '$routeParams', '$location',
    function($scope, ItemsFactory, MetaFactory, $routeParams, $location) {
        $scope.updateDeveloper = function() {
            $scope.item.developer.href="http://localhost:8083/rest/api/users/" + $scope.item.developer.id;
        };

        //$scope.metaDefinitions = MetaFactory.itemMeta;
        $scope.items = ItemsFactory.query($routeParams);
        $scope.itemTypes = MetaFactory.itemTypes;
    }]);

app.controller('ItemDetailCtrl', ['$scope', 'ItemFactory', 'MetaFactory', '$routeParams', '$location','ItemResponse',
    function($scope, ItemFactory, MetaFactory, $routeParams, $location, ItemResponse) {
        console.log("ItemDetailCtrl");
        console.log($routeParams);

        $scope.updateItem = function () {
            $scope.item.status="Design";
            ItemFactory.update({id: $routeParams.id}, $scope.item, function(){
                ItemResponse.data = "Item updated successfully!";
                $location.path('/items/' + $routeParams.id + '/response');
            });
        };

        $scope.releaseItem = function () {
            $scope.item.status="Released";
            ItemFactory.update({id: $routeParams.id}, $scope.item, function(){
                ItemResponse.data = "Item released successfully!";
                $location.path('/items/' + $routeParams.id + '/response');
            });
        };

        $scope.reviewItem = function () {
            $scope.item.status="PendingReview";

            ItemFactory.update({id: $routeParams.id}, $scope.item, function(){
                ItemResponse.data = "Item submitted for review!";
                $location.path('/items/' + $routeParams.id + '/response');
            });
        };

        $scope.rejectItem = function () {
            $scope.item.status="Rejected";
            ItemFactory.update({id: $routeParams.id}, $scope.item, function(){
                ItemResponse.data = "Item rejected successfully!";
                $location.path('/items/' + $routeParams.id + '/response');
            });
        };

        $scope.cancel = function () {
            $location.path('/items/' + $routeParams.id);
        };

        $scope.metaDefinitions = MetaFactory.itemMeta;
        $scope.item = ItemFactory.query($routeParams, function(item) {
            if (typeof $scope.item.properties == "undefined") {
                $scope.item.properties = {};
            }
            Object.keys($scope.metaDefinitions).forEach(function(key) {
                if (typeof $scope.item.properties[key]=="undefined") {
                    $scope.item.properties[key] = "";
                    if ($scope.metaDefinitions[key].controlType == "MULTI_SELECT") {
                        $scope.item.properties[key] = [];
                    }
                }
            });
        });
    }]);


app.controller('ItemOverviewCtrl', ['$scope', 'ItemFactory', 'MetaFactory', '$routeParams', 'OfferFactory', 'ItemRevisionFactory', 'ItemRevisionsFactory',
    function($scope, ItemFactory, MetaFactory, $routeParams, OfferFactory, ItemRevisionFactory, ItemRevisionsFactory) {
        $scope.itemId = $routeParams.id;
        $scope.item = ItemFactory.query($routeParams, function(item) {
            if (item.currentRevision != undefined) {
                $scope.currentRevision = ItemRevisionFactory.query({'id': item.currentRevision.id});
            }
        });
        OfferFactory.query({'itemId': $scope.itemId}, function(offers) {
            $scope.offers = offers.results;
        });
        ItemRevisionsFactory.query({'itemId': $scope.itemId}, function(revisions) {
            $scope.itemRevisions = revisions.results;
        });
    }]);

app.controller('ItemRevisionCtrl', ['$scope', 'ItemRevisionFactory', 'ItemFactory', '$routeParams',
    function($scope, ItemRevisionFactory, ItemFactory, $routeParams) {
        $scope.approveRevision = function () {
            $scope.revision.status="APPROVED";

            ItemRevisionFactory.update({id: $routeParams.revisionId}, $scope.revision, function(){
                $location.path('/items/' + $routeParams.itemId + '/revisions/' + $scope.revision.self.id);
            });
        };
        $scope.revision = ItemRevisionFactory.query({'id': $routeParams.revisionId});
        $scope.item = ItemFactory.query({'id': $routeParams.id});
    }]);

app.controller('ItemCreationCtrl', ['$scope', 'MetaFactory', 'ItemsFactory', '$location', '$cookies',
    function($scope, MetaFactory, ItemsFactory, $location, $cookies) {
        $scope.saveItem = function () {
            ItemsFactory.create($scope.item, function(item){
                $location.path('/items/' + item.self.id + '/revisions/creation');
            });
        };

        //$scope.genres = [1, 2];
        $scope.itemTypes = MetaFactory.itemTypes;

        $scope.user_id = 0;//$cookies.user_id;
        var init = function() {
            $scope.item = {};
            $scope.item.developer = {"href": "http://localhost:3000/api/users/" + $scope.user_id, "id":$scope.user_id };
        };

        init();
    }]);

app.controller('ItemRevisionCreationCtrl', ['$scope', 'MetaFactory', '$routeParams', 'ItemFactory', 'ItemRevisionsFactory', '$location', '$cookies',
    function($scope, MetaFactory, $routeParams, ItemFactory, ItemRevisionsFactory, $location, $cookies) {
        $scope.saveItemRevision = function () {
            ItemRevisionsFactory.create($scope.revision, function(revision){
                $location.path('/items/' + $routeParams.itemId + '/offers/creation');
            });
        };

        $scope.addLocale = function(locale) {
            $scope.revision.locales[locale] = {};
        };
        $scope.removeLocale = function(locale) {
            delete $scope.revision.locales[locale];
        };
        $scope.addPlatform = function(platform) {
            $scope.revision.binaries[platform] = {};
        };
        $scope.removePlatform = function(platform) {
            delete $scope.revision.binaries[platform];
        };

        $scope.platforms = MetaFactory.platforms;
        $scope.gameModes = MetaFactory.gameModes;
        $scope.locales = MetaFactory.locales;

        $scope.user_id = 0;//$cookies.user_id;
        var init = function() {
            $scope.revision = {};
            $scope.revision.status = "DRAFT";
            $scope.revision.locales = {};
            $scope.revision.binaries = {};
            $scope.revision.item = {"href": "http://xxx.xxx.xxx", "id":$routeParams.itemId };
            $scope.revision.developer = {"href": "http://xxx.xxx.xxx", "id":$scope.user_id };
            $scope.itemId = $routeParams.itemId;
            $scope.item = ItemFactory.query({"id":$routeParams.itemId});
        };

        init();
    }]);

app.controller('OfferOverviewCtrl', ['$scope', 'MetaFactory', '$routeParams', 'OfferFactory', 'OfferRevisionsFactory', 'OfferRevisionFactory',
    function($scope, MetaFactory, $routeParams, OfferFactory, OfferRevisionsFactory, OfferRevisionFactory) {
        $scope.offerId = $routeParams.offerId;
        $scope.offer = OfferFactory.query({'id':$routeParams.offerId}, function(offer) {
            if (offer.currentRevision != undefined) {
                $scope.currentRevision = OfferRevisionFactory.query({'id': offer.currentRevision.id});
            }
        });
        OfferRevisionsFactory.query({'offerId': $scope.offerId}, function(revisions) {
            $scope.offerRevisions = revisions.results;
        });

    }]);

app.controller('OfferRevisionCtrl', ['$scope', 'OfferRevisionFactory', 'OfferFactory', '$routeParams',
    function($scope, OfferRevisionFactory, OfferFactory, $routeParams) {
        $scope.approveRevision = function () {
            $scope.revision.status="APPROVED";

            OfferRevisionFactory.update({id: $routeParams.revisionId}, $scope.revision, function(){
                $location.path('/offers/' + $routeParams.offerId + '/revisions/' + $scope.revision.self.id);
            });
        };
        $scope.revision = OfferRevisionFactory.query({'id': $routeParams.revisionId});
        $scope.offer = OfferFactory.query({'id':$routeParams.id});
    }]);

app.controller('OfferCreationCtrl', ['$scope', 'OffersFactory', 'MetaFactory', 'AuthFactory', '$location','$cookies', '$routeParams',
    function($scope, OffersFactory, MetaFactory, AuthFactory, $location, $cookies, $routeParams) {
        $scope.saveOffer = function () {
            $scope.submitted = true;
            OffersFactory.create($scope.offer, function(offer){
                $location.path('/items/' + $routeParams.itemId + '/offers/' + offer.self.id + '/revisions/creation');
            });
        };

        $scope.user_id = 0;//$cookies.user_id;
        $scope.environments = ['DEV', 'STAGING', 'PROD'];
        var init = function() {
            $scope.offer = {};
            $scope.offer.publisher = {"href": "http://xx.xx.xxx", "id":$scope.user_id };
        };

        init();
    }]);

app.controller('OfferRevisionCreationCtrl', ['$scope', 'MetaFactory', '$routeParams', 'OfferRevisionsFactory', 'OfferFactory', 'PriceTiersFactory', 'OfferAttributesFactory', '$location', '$cookies',
    function($scope, MetaFactory, $routeParams, OfferRevisionsFactory, OfferFactory, PriceTiersFactory, OfferAttributesFactory, $location, $cookies) {
        $scope.saveOfferRevision = function () {
            OfferRevisionsFactory.create($scope.revision, function(revision){
                $location.path('/offers/' + $routeParams.offerId);
            });
        };

        $scope.addLocale = function(locale) {
            $scope.revision.locales[locale] = {};
        };
        $scope.removeLocale = function(locale) {
            delete $scope.revision.locales[locale];
        };
        $scope.removePrice = function(countryCode) {
            delete $scope.revision.prices[countryCode];
        };
        $scope.updatePriceTier = function(priceTier) {
            $scope.revision.priceTier = priceTier.self;
            $scope.selectedTier = priceTier;
        };
        $scope.addPrice = function(country) {
            if (angular.isUndefined($scope.revision)) {
                $scope.revision = {};
            }
            if (angular.isUndefined($scope.revision.prices)) {
                $scope.revision.prices = {};
            }

            $scope.revision.prices[country.code] = {"currency": country.currency};
        };
        $scope.selectAllCountries = function() {
            $scope.revision.eligibleCountries = [];
            $scope.countries.forEach(function(country) {
                if (country.code != "DEFAULT") {
                    $scope.revision.eligibleCountries.push(country.code);
                }
            });
        };
        $scope.deselectAllCountries = function() {
            $scope.revision.eligibleCountries = [];
        };

        $scope.updateSelectedPriceTier = function(priceTier) {
            console.log(priceTier);
            $scope.selectedTier = priceTier;
        };

        $scope.updatePriceTier = function() {
            $scope.revision.priceTier = $scope.priceTiers[$scope.selectedTierIndex].self;
            $scope.selectedTier = $scope.priceTiers[$scope.selectedTierIndex];
        };

        $scope.updatePriceType = function(priceType) {
            if (priceType=="FREE") {
                $scope.revision.priceTier = {};
                $scope.selectedTier = {};
                $scope.revision.prices = {};
            }else if (priceType=="TIERED") {
                $scope.revision.prices = {};
            } else {
                $scope.revision.priceTier = {};
                $scope.selectedTier = {};
            }
        };

        $scope.priceTiers = PriceTiersFactory.query();
        $scope.categoryAttributes = OfferAttributesFactory.query({type: "Category"});

        $scope.isCollapsed = true;
        $scope.locales = MetaFactory.locales;
        $scope.countries = MetaFactory.countries;
        $scope.offerId = $routeParams.offerId;
        $scope.user_id = 0;//$cookies.user_id;
        var init = function() {
            $scope.revision = {};
            $scope.revision.price = {};
            $scope.revision.status = "DRAFT";
            $scope.revision.locales = {};
            $scope.revision.offer = {"href": "http://xxx.xxx.xxx", "id":$routeParams.offerId };
            $scope.revision.publisher = {"href": "http://xxx.xxx.xxx", "id":$scope.user_id };
            $scope.offer = OfferFactory.query({"id":$routeParams.offerId});
        };

        init();
    }]);

app.controller('ItemAdminListCtrl', ['$scope', 'ItemsFactory',
    function($scope, ItemsFactory) {
        $scope.updateList = function () {
            if ($scope.showStatus=="PendingReview") {
                $scope.items = ItemsFactory.query({status: 'PendingReview'});
            } else {
                $scope.items = ItemsFactory.query({status: 'Released'});
            }
        };
        $scope.items = ItemsFactory.query({status: 'PendingReview'});
    }]);

app.controller('ItemResponseCtrl', ['$scope', '$routeParams', 'ItemResponse',
    function($scope, $routeParams, ItemResponse) {
        $scope.response = ItemResponse.data;
        $scope.itemId = $routeParams.id;
    }]);

app.controller('AttributeListCtrl', ['$scope', 'AttributesFactory', '$location',
    function($scope, AttributesFactory, $location) {
        $scope.createAttribute = function () {
            AttributesFactory.create($scope.attribute, function(attribute){
                $location.path('/attributes/' + attribute.self.id);
            });
        };
        $scope.cancel = function () {
            $location.path('/attributes');
        };
        $scope.attributes = AttributesFactory.query();
    }]);

app.controller('AttributeDetailCtrl', ['$scope', 'AttributeFactory', '$routeParams', '$location',
    function($scope, AttributeFactory, $routeParams, $location) {
        console.log("AttributeDetailCtrl");
        console.log($routeParams);

        $scope.cancel = function () {
            $location.path('/attributes/' + $routeParams.id);
        };

        $scope.attribute = AttributeFactory.query($routeParams);
    }]);

app.controller('LoginCtrl', ['$scope', '$routeParams', '$http', '$cookieStore', '$cookies', '$window',
    function($scope, $routeParams, $http, $cookieStore, $cookies, $window) {
        $scope.Login = function() {
            //$window.alert("Hello");
            //$window.location.href = "http://www.baidu.com";
            //$window.location.href = "http://10.0.1.137:8082/oauth2/authorize?client_id=catalog-admin&response_type=code&redirect_uri=http://localhost:3000/auth/&scope=identity%20catalog";
            $window.location.href = "http://api.oculusvr-demo.com:8081/rest/oauth2/authorize?client_id=catalog-admin&response_type=token%20id_token&redirect_uri=http://localhost:3000/auth/&scope=identity%20catalog%20openid&nonce=123";
        }

        $scope.Logout = function () {
            //$cookies.user_id = undefined;
            //$cookies.access_token = undefined;

            $cookieStore.remove("user_id");
            $cookieStore.remove("access_token");

            $scope.user_id=undefined;
            $scope.access_token=null;

            var logoutUrl = "http://api.oculusvr-demo.com:8081/rest/oauth2/end-session?id_token_hint=";// + id_token;

            $window.location.href = logoutUrl;
        };

        $scope.user_id=$cookies.user_id;//$cookieStore.get("user_id");
        $scope.access_token=$cookies.access_token;//$cookieStore.get("access_token");
    }]);

app.controller('DeveloperInfoCtrl', ['$scope', '$routeParams', '$http', '$cookieStore', '$cookies', '$window',
    function($scope, $routeParams, $http, $cookieStore, $cookies, $window) {

        $scope.Logout = function () {
            //$cookies.user_id = undefined;
            //$cookies.access_token = undefined;

            $cookieStore.remove("user_id");
            $cookieStore.remove("access_token");
            $cookieStore.remove("email");

            $scope.user_id=undefined;
            $scope.access_token=null;

            var logoutUrl = "http://api.oculusvr-demo.com:8081/rest/oauth2/end-session?post_logout_redirect_uri=http://localhost:3000/&id_token_hint=" + $cookies.id_token;

            $window.location.href = logoutUrl;
        };

        $scope.user_id=$cookies.user_id;//$cookieStore.get("user_id");
        $scope.email=$cookies.email;//$cookieStore.get("access_token");
    }]);