#Catalog
API about catalog

## /items/{item_id}
###GET
get item by item id

+ Response 200 (application/json)

        {
            "self": "/items/123",
            "itemId": 123,
            "type": "OVR_APP",
            "status":"PUBLISHED"
            "name": "OVR Demo",
            "categoryId": 6789,
            "ownerId": 3000,
            "revision": 1,
            "sku":
            {
                "skuId": 1,
                "name": "demo sku",
                "inventorySku": "abc1234",
                "price":
                {
                    "type": "TIER_PRICE",
                    "amount": 1
                }
            },
            "createdDate": "2013-01-01",
            "createdBy": "demo user" ,
            "updatedDate": null,
            "updatedBy": null
        }

###PUT
update an existing item

+ Request (application/json)

        {
            "name": "OVR Demo2"
        }

+ Response 200 (application/json)

        {
            "status": "success",
            "itemUri": "/items/123"
        }

## /items/{item_id}/revisions
###GET
get item revisions by item id

+ Response 200 (application/json)

        {
            "revisions":
            [
                 1,2,3
            ]
        }

## /items/{item_id}/revisions/{revision}
###GET
get a specific revision of an item by item id and revision


+ Response 200 (application/json)

        {
            "self": "/items/123/revisions/2"
            "itemId": 123,
            "type": "OVR_APP",
            "status":"PUBLISHED"
            "name": "OVR Demo",
            "categoryId": 6789,
            "ownerId": 3000,
            "revision": 2,
            ...
        }

## /items
###GET
get items

+ Response 200 (application/json)

        {
            "self": "/items"
            "itemUris":
            [
                "/items/123",
                "/items/234",
                ...
            ],
            "next": "/items?page=2&pagesize=20"
        }

### POST
create a new item

+ Request (application/json)

        {
            "type": "OVR_APP",
            "status":"PUBLISHED"
            "name": "OVR Demo",
            "categoryId": 6789,
            "ownerId": 3000,
            "revision": 1,
            "sku":
            {
                "skuId": 1,
                "name": "demo sku",
                "inventorySku": "abc1234",
                "price":
                {
                    "type": "TIER_PRICE",
                    "amount": 1
                }

            },
            "createdDate": "2013-01-01",
            "createdBy": "demo user" ,
            "updatedDate": null,
            "updatedBy": null
        }

+ Response 200 (application/json)

        {
            "status": "success",
            "itemUri":  "/items/123"
        }

## /items/{item_id}/status
### PUT
update item status

+ Request (application/json)

        {
            "status": "PUBLISHED"
        }

+ Response 200 (application/json)

        {
            "status": "success",
            "itemUri":  "/items/123"
        }

## /items/{item_id}/inventory
### PUT
update item status

+ Request (application/json)

        {
            "quantity": 30
        }

+ Response 200 (application/json)

        {
            "status": "success",
            "itemUri":  "/items/123"
        }

## /categories/{category_id}
### GET
get category by category id

+ Response 200 (application/json)

        {
            "self": "/categories/123",
            "categoryId": 123,
            "name": "demo category",
            "parentId": 6789,
            "status": "ACTIVE",
            "createdDate": "2013-01-01",
            "createdBy": "demo user" ,
            "updatedDate": null,
            "updatedBy": null
        }

## /categories
### GET
get categories

+ Response 200 (application/json)

        {
            "self": "/categories",
            "categories":
            [
                "/categories/123",
                "/categories/345",
                ...
            ],
            "next": "/categories?page=2&pagesize=20"
        }

### POST
create a new category

+ Request (application/json)

        {
            "name": "demo category",
            "parentId": 6789,
            "status": "ACTIVE",
            "createdDate": "2013-01-01",
            "createdBy": "demo user" ,
            "updatedDate": null,
            "updatedBy": null
        }

+ Response 200 (application/json)

        {
            "status": "success",
            "categoryUri":  "/categories/123"
        }

## /promotionRules/{rule_id}
### GET
get promotion rule by rule id 

+ Response 200 (application/json)

        {
            "self": "/promotionRules/123",
            "ruleId": 123,
            "type": "ITEM_PROMOTION_RULE",
            "status":"PUBLISHED"
            "name": "10% off for books",
            "revision": 1,
            "startDate": "2013-12-29",
            "endDate": "2014-03-27",
            "couponCodeSet": "codeSet001",
            "conditions":
            {
                "includeScope":
                {
                    "categories": ["category001"],
                    "offers": [],
                    "entitlements": []
                },
                "excludeScope":
                {
                    "categories": [],
                    "offers": ["offer001"],
                    "entitlements": []
                }
            },
            "discounts":
            {
                "type": "PERCENT",
                "amount": 0.3
            },
            "createdDate": "2013-01-01",
            "createdBy": "demo user" ,
            "updatedDate": null,
            "updatedBy": null
        }
        
###PUT
update an existing promotion rule

+ Request (application/json)

        {
            "startDate": "2013-12-08"
        }

+ Response 200 (application/json)

        {
            "status": "SUCCESS",
            "ruleUri": "/promotionRules/123"
        }
        
        
## /promotionRules
### GET
get promotion rules

+ Response 200 (application/json)

        {
            "self": "/promotionRules",
            "promotionRules":
            [
                "/promotionRules/123",
                "/promotionRules/345",
                ...
            ],
            "next": "/promotionRules?page=2&pagesize=20"
        }
        
### POST
create a new promotion rule

+ Request (application/json)

        {
            "type": "ITEM_PROMOTION_RULE",
            "name": "10% off for books",
            "startDate": "2013-12-29",
            "endDate": "2014-03-27",
            "couponCodeSet": null,
            "conditions":
            {
                "includeScope":
                {
                    "categories": ["category001"],
                    "offers": [],
                    "entitlements": []
                },
                "excludeScope":
                {
                    "categories": [],
                    "offers": ["offer001"],
                    "entitlements": []
                }
            },
            "discounts":
            {
                "type": "PERCENT",
                "amount": 0.3
            },
            "createdDate": "2013-01-01",
            "createdBy": "demo user" ,
            "updatedDate": null,
            "updatedBy": null
        }

+ Response 200 (application/json)

        {
            "status": "SUCCESS",
            "ruleUri":  "/promotionRules/123"
        }
        
        
## /promotionRules/{rule_id}/status
### PUT
update promotion rule status

+ Request (application/json)

        {
            "status": "PUBLISHED"
        }

+ Response 200 (application/json)

        {
            "status": "SUCCESS",
            "ruleUri":  "/promotionRules/123"
        }