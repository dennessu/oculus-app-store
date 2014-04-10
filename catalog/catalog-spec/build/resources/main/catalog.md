#Catalog
API about catalog

##resource item

        {
            "self": {"href": "https://data.oculusvr.com/v1/items/123", "id": "123"},
            "type": "APP",
            "name": "demo item",
            "revision": 1,
            "status": "ACTIVE",
            "ownerId": {"href": "https://data.oculusvr.com/v1/users/3234", "id": "3234"},
            "skus":
            [
                {
                    "id": 1,
                    "externalSku": "abc-1",
                    "inventory": 100
                },
                {
                    "id": 2,
                    "externalSku": "abc-2",
                    "inventory": 10
                }
            ],

            "properties":
            {
                "mainImage": "the img url"
            },

            "createdTime": "2013-01-01T01:32:53Z",
            "createdBy": "1234",
            "updatedTime": "2013-01-01T01:32:53Z",
            "updatedBy": "1234"
        }

##resource offer

        {
            "self": { "href": "https://data.oculusvr.com/v1/offers/123", "id": "123"},
            "name": "demo offer",
            "revision": 1,
            "status":"ACTIVE",
            "ownerId": {"href": "https://data.oculusvr.com/v1/users/3234", "id": "3234"},

            "categories":
            [
                {"href": "https://data.oculusvr.com/v1/categories/123", "id": "123"},
                {"href": "https://data.oculusvr.com/v1/categories/124", "id": "124"}
            ],

            "priceTier": {"href": "http://data.oculusvr.com/v1/price-tier/1234", "id": "1234"},
            "prices":
            {
                "US":
                {
                    "currency": "USD",
                    "amount": "9.99"
                },
                "CN":
                {
                    "currency": "CNY",
                    "amount": "60.00"
                }
            },

            "subOffers":
            [
                {
                    "offerId": {"href": "https//data.oculusvr.com/v1/offers/3333", "id": "3333"},
                    "quantity": 1
                },
                {
                    "offerId": {"href": "https//data.oculusvr.com/v1/offers/2222", "id": "2222"},
                    "quantity": 3
                },
                {
                    "offerId": {"href": "https//data.oculusvr.com/v1/offers/1111", "id": "1111"},
                    "quantity": 1
                }
            ],

            "items":
            [
                {
                    "itemId": {"href": "https//data.oculusvr.com/v1/items/444", "id": "444"},
                    "sku": 1234,
                    "quantity": 1
                },
                {
                    "itemId": {"href": "https//data.oculusvr.com/v1/items/777", "id": "777"},
                    "sku": 2345,
                    "quantity": 2
                }
            ],

            "restriction":
            {
                "preconditionItems":
                [
                    {"href": "https//data.oculusvr.com/v1/items/2343", "id": "2343"},
                    {"href": "https//data.oculusvr.com/v1/items/5233", "id": "5233"},
                    {"href": "https//data.oculusvr.com/v1/items/1344", "id": "1344"}
                ],

                "exclusionItems":
                [
                    {"href": "https//data.oculusvr.com/v1/items/3333", "id": "3333"},
                    {"href": "https//data.oculusvr.com/v1/items/2222", "id": "2222"},
                    {"href": "https//data.oculusvr.com/v1/items/1111", "id": "1111"}
                ],

                "limitPerCustomer": 5,

                "limitPerOrder": 3
            },

            "events":
            [
                {
                    "name":"PURCHASE_EVENT",
                    "actions":
                    [
                        {
                            "type": "GRANT_DOWNLOAD_ENTITLEMENT",
                            "properties":
                            {
                                "tag": "item001_ANGRY.BIRD_ONLINE_ACCESS",
                                "group":"Angry Bird",
                                "type":"ONLINE_ACCESS",
                                "duration": "3Month"
                            }
                        }
                    ]
                }
            ],

            "countryProperties":
            {
                "DEFAULT":
                {
                    "releaseDate": "2016-01-01"
                },
                "US":
                {
                    "releaseDate": "2015-01-01"
                },
                "CN":
                {
                    "releaseDate": "2099-01-01"
                }
            },
            "localeProperties":
            {
                "DEFAULT":
                {
                    "description": "the default description"
                },
                "en_US":
                {
                    "description": "the description"
                }
            },

            "properties":
            {
                "mainImage": "the img url"
            },

            "createdTime": "2013-01-01T01:32:53Z",
            "createdBy": "3234",
            "updatedTime": "2013-01-01T01:32:53Z",
            "updatedBy": "3234"
        }

##resource category

        {
            "self": {"href": "https://data.oculusvr.com/v1/categories/123", "id": "123"},
            "name": "demo category",
            "parentId": {"href": "https://data.oculusvr.com/v1/categories/100", "id": "100"},
            "status": "ACTIVE",
            "revision":1,
            "properties":
            {
                "propertyName": "propertyValue"
            },
            "createdTime": "2013-01-01T01:32:53Z",
            "createdBy": "3234",
            "updatedTime": "2013-01-01T01:32:53Z",
            "updatedBy": "3234"
        }

##resource item list

        {
            "self": {"href": "https://data.oculusvr.com/v1/items", "id": ""},
            "results":
            [
                {
                    "self": {"href": "https://data.oculusvr.com/v1/items/123", "id": "123"}
                    "name": "demo offer 1",
                    "revision": 1,
                    "status":"ACTIVE",
                    ...
                },
                {
                    "self": {"href": "https://data.oculusvr.com/v1/items/124", "id": "124"}
                    "name": "demo offer 2",
                    "revision": 1,
                    "status":"ACTIVE",
                    ...
                },
                ...
            ],
            "next": {"href": "https://data.oculusvr.com/v1/items?start=21&size=20", "id": ""}
        }

##resource offer list

        {
            "self": {"href": "https://data.oculusvr.com/v1/offers", "id": ""},
            "results":
            [
                {
                    "self": {"href": "https://data.oculusvr.com/v1/offers/123", "id": "123"}
                    "name": "demo offer 1",
                    "revision": 1,
                    "status":"ACTIVE",
                    ...
                },
                {
                    "self": {"href": "https://data.oculusvr.com/v1/offers/124", "id": "124"}
                    "name": "demo offer 2",
                    "revision": 1,
                    "status":"ACTIVE",
                    ...
                },
                ...
            ],
            "next": {"href"="https://data.oculusvr.com/v1/offers?start=21&size=20", "id": ""}
        }

##resource category list

        {
            "self": {"href": "https://data.oculusvr.com/v1/categories", "id": ""},
            "results":
            [
                {
                    "self": {"href": "https://data.oculusvr.com/v1/categories/123", "id": "123"},
                    "name": "demo category",
                    "parentId": "cat-6789",
                    "status": "ACTIVE",
                    ...
                },
                {
                    "self": {"href": "https://data.oculusvr.com/v1/categories/124", "id": "124"},
                    "name": "demo category",
                    "parentId": ACTIVE",
                    ...
                },
                ...
            ],
            "next": {"href": "https://data.oculusvr.com/v1/categories?start=21&size=20", "id": ""}
        }

## /items/{key}
###GET
get item by item id

+ Response 200 (application/json)

    the [item](#resource-item)

+ Response 404

###PUT
update an existing item

+ Request (application/json)

    the [item](#resource-item) which needs to be updated

+ Response 200 (application/json)

    the updated item

## /items/{key1}/revisions/{key2}
###GET
get a specific revision of an item by item id and revision


+ Response 200 (application/json)

    the [item](#resource-item)

## /items
###GET
get items

+ Parameters

    + page(optional)
    + count(optional)
    + id(optional)

+ Response 200 (application/json)

    the [items](#resource-items-list)

### POST
create a new item

+ Request (application/json)

        {
          "type": "APP",
          "name": "demo item",
          "ownerId": {"href": "https://api.oculusvr.com/v1/users/3234", "id": "3234"},
          "skus":
          [
            {
              "id": 1,
              "externalSku": "abc-1",
              "inventory": 100
            },
            {
              "id": 2,
              "externalSku": "abc-2",
              "inventory": 10
            }
          ],

          "properties":
          {
            "mainImage": "the img url"
          }
        }

+ Response 200 (application/json)

    the created [item](#resource-item)

## /items/{key}/review
### POST
create review request for an item

+ Request (application/json)

        {
        }

+ Response 200 (application/json)

    the [item](#resource-item) in "PENDING_REVIEW" status

## /items/{key}/publish
### POST
publish an item

+ Request (application/json)

        {
        }

+ Response 200 (application/json)

    the [item](#resource-item) in "ACTIVE" status

## /items/{key}/unpublish
### POST
unpublish an items

+ Request (application/json)

        {
        }

+ Response 200 (application/json)

    the [item](#resource-item) in "DRAFT" status

## /offers/{key}
###GET
get offer by offer id

+ Response 200 (application/json)

    the [offer](#resource-offer)

+ Response 404

###PUT
update an existing offer

+ Request (application/json)

    the [offer](#resource-offer) which needs to be updated

+ Response 200 (application/json)

    the updated offer

## /offer/{key1}/revisions/{key2}
###GET
get a specific revision of an offer by offer id and revision


+ Response 200 (application/json)

    the [offer](#resource-offer)

## /offers
###GET
get offers

+ Parameters

    + page(optional)
    + count(optional)
    + id(optional)

+ Response 200 (application/json)

    the [offers](#resource-offers-list)

### POST
create a new offer

+ Request (application/json)

        {
            "name": "demo offer",
            "ownerId": {"href": "https://api.oculusvr.com/v1/users/3234", "id": "3234"},

            "categories": [
                {
                    "href": "http://api.oculusvr.com/v1/Category/123",
                    "id": "123"
                },
                {
                    "href": "http://api.oculusvr.com/v1/Category/124",
                    "id": "124"
                }
            ],
            "prices": {
                "US": {
                    "amount": 9.99,
                    "currency": "USD"
                },
                "CN": {
                    "amount": 60,
                    "currency": "CNY"
                }
            },

            "subOffers": [],
            "items": [
                {
                    "itemId": {
                        "href": "http://api.oculusvr.com/v1/Item/444",
                        "id": "444"
                    },
                    "sku": 1234,
                    "quantity": 1
                }
            ],
            "restriction": {
                "limitPerCustomer": 5,
                "limitPerOrder": 3,
                "preconditionItems": [],
                "exclusionItems": []
            },

            "events": [
                {
                    "name": "PURCHASE_EVENT",
                    "actions": [
                        {
                            "type": "GRANT_DOWNLOAD_ENTITLEMENT",
                            "properties": {
                                "tag": "item001_ANGRY.BIRD_ONLINE_ACCESS",
                                "group": "Angry Bird",
                                "type": "ONLINE_ACCESS",
                                "duration": "3Month"
                            }
                        }
                    ]
                }
            ],

            "countryProperties": {
                "DEFAULT": {
                    "releaseDate": "2016-01-01"
                },
                "US": {
                    "releaseDate": "2015-01-01"
                },
                "CN": {
                    "releaseDate": "2099-01-01"
                }
            },
            "localeProperties": {
                "DEFAULT": {
                    "description": "the default description"
                },
                "en_US": {
                    "description": "the description"
                }
            },

          "properties": {
                "mainImage": "the img url"
            }
        }

+ Response 200 (application/json)

    the created [offer](#resource-offer)

## /offers/{key}/review
### POST
create review request for an offer

+ Request (application/json)

        {
        }

+ Response 200 (application/json)

    the [offer](#resource-offer) in "PENDING_REVIEW" status

## /offers/{key}/publish
### POST
create review request for an offer

+ Request (application/json)

        {
        }

+ Response 200 (application/json)

    the [offer](#resource-offer) in "ACTIVE" status

## /offers/{key}/unpublish
### POST
create review request for an offer

+ Request (application/json)

        {
        }

+ Response 200 (application/json)

    the [offer](#resource-offer) in "DRAFT" status

## /categories/{key}
### GET
get category by category id

+ Response 200 (application/json)

    the [category](#resource-category)

## /categories
### GET
get categories

+ Response 200 (application/json)

    the [categories](#resource-category-list)

### POST
create a new category

+ Request (application/json)

        {
          "name": "demo category",
          "parentId": {"href": "https://api.oculusvr.com/v1/categories/100", "id": "100"},
          "properties":
          {
            "propertyName": "propertyValue"
          }
        }

+ Response 200 (application/json)

    the created [category](#resource-category)
