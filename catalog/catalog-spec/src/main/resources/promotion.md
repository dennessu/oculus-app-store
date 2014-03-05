#Promotions
APIs about promotion
##resource promotion

        {
            "self": {"href": "https://data.oculusvr.com/v1/promotions/123", "id": "123"},
            "type": "OFFER_PROMOTION",
            "status":"PUBLISHED",
            "name": "10% off for books",
            "revision": 1,
            "currency": "USD",
            "startDate": "2013-12-29T00:00:00Z",
            "endDate": "2014-03-27T00:00:00Z",
            "criteria":
            [{
                 "type": "INCLUDE_OFFER",
                 "entities": 
                 [
                     {"href": "https://data.oculusvr.com/v1/offers/offer001", "id": "offer001"},
                     {"href": "https://data.oculusvr.com/v1/offers/offer002", "id": "offer002"},
                     {"href": "https://data.oculusvr.com/v1/offers/offer003", "id": "offer003"}
                 ]
            },
            {
                 "type": "INCLUDE_CATEGORY",
                 "entities": 
                 [
                     {"href": "https://data.oculusvr.com/v1/categories/ctg001", "id": "ctg001"}
                 ]
            },
            {
                 "type": "EXCLUDE_OFFER",
                 "entities":
                 [
                     { "href": "https://data.oculusvr.com/v1/offers/offer007", "id": "offer007"}
                 ]
            },
            {
                 "type": "INCLUDE_ENTITLEMENT",
                 "entitlements":
                 [{
                      "entitlementId": {"href": "https://data.oculusvr.com/v1/entitlementDefinitions/123", "id": "123"},
                      "group": "XXX",
                      "tag": "GOLDEN_PLAYER"
                 },
                 {
                      "entitlementId": {"href": "https://data.oculusvr.com/v1/entitlementDefinitions/345", "id": "345"},
                      "group": "YYY",
                      "tag": "GOLDEN_PLAYER"
                 }]
            },
            {
                    "type": "COUPON_CODE",
                    "couponClass": "codeClass001"
            }],
            "benefit":
            {
                "type": "RATIO_DISCOUNT",
                "amount": 0.3
            },
            "createdTime": "2013-01-01T00:00:00Z",
            "createdBy": {"href": "https://data.oculusvr.com/v1/users/3234", "id": "3234"},
            "updatedTime": "2013-01-01T00:00:00Z",
            "updatedBy": {"href": "https://data.oculusvr.com/v1/users/3234", "id": "3234"}
        }

##resource promotion list

        {
            "self": {"href": "https://data.oculusvr.com/v1/promotions", "id": ""},
            "results":
            [{
                "promotionId": {"href": "https://data.oculusvr.com/v1/promotions/123", "id": "123"},
                "type": "OFFER_PROMOTION",
                "status":"PUBLISHED",
                "name": "10% off for books",
                "revision": 1,
                "currency": "USD",
                "startDate": "2013-12-29T00:00:00Z",
                "endDate": "2014-03-27T00:00:00Z",
                "criteria":
                [{
                     "type": "INCLUDE_OFFER",
                     "entities":
                     [
                         {"href": "https://data.oculusvr.com/v1/offers/offer001", "id": "offer001"},
                         {"href": "https://data.oculusvr.com/v1/offers/offer002", "id": "offer002"},
                         {"href": "https://data.oculusvr.com/v1/offers/offer003", "id": "offer003"}
                     ]
                },
                {
                     "type": "INCLUDE_CATEGORY",
                     "entities": 
                     [
                         {"href": "https://data.oculusvr.com/v1/offers/ctg001", "id": "ctg001"}
                     ]
                },
                {
                     "type": "EXCLUDE_OFFER",
                     "entities":
                     [
                         {"href": "https://data.oculusvr.com/v1/offers/offer009", "id": "offer009"}
                     ]
                },
                {
                     "type": "INCLUDE_ENTITLEMENT",
                     "entitlements":
                     [{
                         "entitlementId": {"href": "https://data.oculusvr.com/v1/entitlementDefinitions/123", "id": "123"},
                         "group": "XXX",
                         "tag": "GOLDEN_PLAYER"
                     },
                     {
                         "entitlementId": {"href": "https://data.oculusvr.com/v1/entitlementDefinitions/345", "id": "345"},
                         "group": "YYY",
                         "tag": "GOLDEN_PLAYER"
                     }]
                },
                {
                     "type": "COUPON_CODE",
                     "couponClass": "codeClass001"
                }],
                "benefit":
                {
                    "type": "RATIO_DISCOUNT",
                    "amount": 0.3
                },
                "createdTime": "2013-01-01T00:00:00Z",
                "createdBy": {"href": "https://data.oculusvr.com/v1/users/3234", "id": "3234"},
                "updatedTime": "2013-01-01T00:00:00Z",
                "updatedBy": {"href": "https://data.oculusvr.com/v1/users/3234", "id": "3234"}
            },
            {
                "promotionId": {"href": "https://data.oculusvr.com/v1/promotions/345", "id": "345"},
                "type": "CART_PROMOTION",
                "status":"PUBLISHED",
                "name": "cart benefit",
                "revision": 1,
                "currency": "USD",
                "startDate": "2013-12-29T00:00:00Z",
                "endDate": "2014-03-27T00:00:00Z",
                "criteria":
                [{
                     "type": "INCLUDE_ENTITLEMENT",
                     "entitlements":
                     [{
                          "entitlementId": {"href": "https://data.oculusvr.com/v1/entitlementDefinitions/345", "id": "345"},
                          "group": "XXX",
                          "tag": "GOLDEN_PLAYER"
                     }]
                },
                {
                     "type": "CART_ITEM_COUNT",
                     "value": 10
                }],
                "benefit":
                {
                    "type": "FLAT_DISCOUNT",
                    "amount": 20
                },
                "createdTime": "2013-01-01T00:00:00Z",
                "createdBy": {"href": "https://data.oculusvr.com/v1/users/3234", "id": "3234"},
                "updatedTime": "2013-01-01T00:00:00Z",
                "updatedBy": {"href": "https://data.oculusvr.com/v1/users/3234", "id": "3234"}
            },
                ...
            ],
            "next": {
                "href": "https://data.oculusvr.com/v1/promotions?cursor=...&count=20",
                "id": ""
            }
        }

## /promotions/{key}
### GET
get promotion rule by rule id

+ Response 200 (application/json)

     the [promotion](#resource-promotion)

+ Response 404

###PUT
update an existing promotion rule

+ Request (application/json)

     the entire [promotion](#resource-promotion) which needs to be updated

+ Response 200 (application/json)

     the updated [promotion](#resource-promotion)


## /promotionRules
### GET
get promotion rules

+ Parameters

    + page(optional)
    + count(optional)
    + id(optional)

+ Response 200 (application/json)

     the [promotions](#resource-promotion-list)

### POST
create a new promotion rule

+ Request (application/json)

     the [promotion](#resource-promotion) which needs to be created

+ Response 200 (application/json)

     the created [promotion](#resource-promotion)


## /promotionRules/{key}/status
### PUT
update promotion rule status

+ Request (application/json)

        {
            "status": "PUBLISHED"
        }

+ Response 200 (application/json)

     the [promotion](#resource-promotion) with status updated

+ Response 500

        {
            "errorMsg": "INVALID_STATUS"
        }
