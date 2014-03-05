#fulfilment
API about fulfilment

## /fulfilments
###POST
post new fulfilments

+ Request (application/json)

        {
            "userId": "user001",
            "orderId": "123456789",
            "items":[
                {
                    "lineItemId": 1000000001,
                    "sku": 1000000001,
                    "itemId": "item001",
                    "itemRevision": 2,
                    "quantity": 10
                },
                {
                    "lineItemId": 1000000002,
                    "sku": 1000000002,
                    "itemId": "item002",
                    "itemRevision": 3,
                    "quantity": 20
                },
                {
                    "lineItemId": 1000000003,
                    "sku": nil,
                    "itemId": "item003",
                    "itemRevision": 3,
                    "quantity": 1
                }
            ],
            "shippingInfo": {
                "firstName":  "Hello",
                "middleName":  "My",
                "lastName": "World",
                "shihppingMethod": "EMS",
                "addresss": {
                    "country": "US",
                    "state": "CA",
                    "city": "Redwood",
                    "street": "test street",
                    "stree1": "test street1",
                    "stree2": "test street2",
                    "stree3": "test street3",
                    "postCode": "12345",
                    "phoneNumber": "1234567"
                }
            }
        }


+ Response 200 (application/json)

        {
            "orderId": 123456789,
            "fulfilments": [
                {
                    "fulfilmentUri": "/fulfilments/1000001",
                    "status": "PENDING",
                    "lineItemId": 1000000001
                    "result": nil
                },
                {
                    "fulfilmentUri": "/fulfilments/1000001",
                    "status": "PENDING",
                    "lineItemId": 1000000002
                    "result": nil
                },
                {
                    "fulfilmentUri": "/fulfilments/1000003",
                    "status": "SUCCEED",
                    "lineItemId": 1000000003
                    "result": "/entitlements/123456"
                }
            ]
        }

+ Response 500 (application/json)

        {
            "orderId": 123456789,
            "errorCode": "INVALID_ITEM_ID",
            "errorMessage": "Item 1000000003 not found"
        }

## /fulfilments/{fulfilment_id}
###GET
get fulfilment detail

+ Response 200 (application/json)

        {
            "trackingGuid": "151907d5-807e-4a69-83e6-e709b6f75359",
            "status": "SUCCEED",
            "payload":  {
                "lineitemId": 1000000003,
                "sku": nil,
                "itemId": "item003",
                "itemRevision": 3,
                "quantity": 1
            },
            "result": "/entitlements/123456"
        }

+ Response 404 (application/json)


## /fulfilments?orderId=123456789
###GET
get fulfilments by billing order id

+ Response 200 (application/json)

        {
            "fulfilments": [
                {
                    "fulfilmentUri": "/fulfilments/1000001",
                    "status": "PENDING",
                    "lineItemId": 1000000001
                    "result": nil
                },
                {
                    "fulfilmentUri": "/fulfilments/1000001",
                    "status": "PENDING",
                    "lineItemId": 1000000002
                    "result": nil
                },
                {
                    "fulfilmentUri": "/fulfilments/1000003",
                    "status": "SUCCEED",
                    "lineItemId": 1000000003
                    "result": "/entitlements/123456"
                }
            ]
        }

+ Response 200 (application/json)

        {
            "fulfilments": []
        }