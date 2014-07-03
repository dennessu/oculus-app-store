#fulfilment
API about fulfilment

## /fulfilments
###POST
post new fulfilments

+ Request (application/json)

        {
            "userId": {"href": "http://api.wan-san.com/v1/users/12345", "id": "12345"},
            "orderId": "12345",
            "trackingUuid": "151907d5-807e-4a69-83e6-e709b6f75359",
            "items":[
                {
                    "itemReferenceId": "100000001",
                    "offerId": {"href": "http://api.wan-san.com/v1/offers/11111", "id": "11111"},
                    "timestamp": 10000000000000,
                    "quantity": 1
                },
                {
                    "itemReferenceId": "100000002",
                    "offerId": {"href": "http://api.wan-san.com/v1/offers/22222", "id": "22222"},
                    "timestamp": 10000000000000,
                    "quantity": 10
                }
            ],
            "shippingAddressId": {"href": "http://api.wan-san.com/v1/shipping-addresses/12345", "id": "12345"},
            "shippingMethodId": {"href": "http://api.wan-san.com/v1/shipping-methods/12345", "id": "12345"}
        }


+ Response 200 (application/json)

        {
            "self": {"href": "http://api.wan-san.com/v1/fulfilments?orderId=123456789, "id":""},
            "userId": {"href": "http://api.wan-san.com/v1/users/12345", "id": "12345"},
            "orderId": "12345",
            "trackingUuid": "151907d5-807e-4a69-83e6-e709b6f75359",
            "items":[
                {
                    "self": {"href": "http://api.wan-san.com/v1/fulfilments/11111", "id": "11111"},
                    "itemReferenceId": "100000001",
                    "offerId": {"href": "http://api.wan-san.com/v1/offers/11111", "id": "11111"},
                    "timestamp": 10000000000000,
                    "quantity": 1,
                    "status": "SUCCEED",
                    "fulfilmentActions":[
                        {
                            "fulfilmentType": "GRANT_ENTITLEMENT",
                            "status": "SUCCEED",
                            "result": "1234567"
                        }
                    ]
                },
                {
                    "self": {"href": "http://api.wan-san.com/v1/fulfilments/22222", "id": "22222"},
                    "itemReferenceId": "100000002",
                    "offerId": {"href": "http://api.wan-san.com/v1/offers/22222", "id": "22222"},
                    "timestamp": 10000000000000,
                    "quantity": 10,
                    "status": "PENDING",
                    "fulfilmentActions":[
                        {
                            "fulfilmentType": "GRANT_ENTITLEMENT",
                            "status": "SUCCEED",
                            "result": "1234567"
                        },
                        {
                            "fulfilmentType": "DEPOSIT_CSV",
                            "status": "SUCCEED",
                            "result": ""
                        },
                        {
                            "fulfilmentType": "DELIVER_PHYSICAL_GOODS",
                            "status": "PENDING",
                            "result": ""
                        }
                    ]
                }
            ],
            "shippingAddressId": {"href": "http://api.wan-san.com/v1/shipping-addresses/12345", "id": "12345"},
            "shippingMethodId": {"href": "http://api.wan-san.com/v1/shipping-methods/12345", "id": "12345"}

        }

## /fulfilments/{key}
###GET
get fulfilment detail

+ Response 200 (application/json)

        {
            "self": {"href": "http://api.wan-san.com/v1/fulfilments/11111", "id": "11111"},
            "itemReferenceId": "100000001",
            "offerId": {"href": "http://api.wan-san.com/v1/offers/11111", "id": "11111"},
            "timestamp": 10000000000000,
            "quantity": 1,
            "status": "SUCCEED",
            "fulfilmentActions":
                [
                    {
                        "fulfilmentType": "GRANT_ENTITLEMENT",
                        "status": "SUCCEED",
                        "result": "1234567"
                    }
                ]
        }

+ Response 404 (application/json)


## /fulfilments?orderId=123456789
###GET
get fulfilments by billing order addressId

+ Response 200 (application/json)

        {
            "self": {"href": "http://api.wan-san.com/v1/fulfilments?orderId=123456789, "id":""},
            "userId": {"href": "http://api.wan-san.com/v1/users/12345", "id": "12345"},
            "orderId": "123456789",
            "trackingUuid": "151907d5-807e-4a69-83e6-e709b6f75359",
            "items":[
                {
                    "self": {"href": "http://api.wan-san.com/v1/fulfilments/11111", "id": "11111"},
                    "itemReferenceId": "100000001",
                    "offerId": "{"href": "http://api.wan-san.com/v1/offers/11111", "id": "11111"},
                    "timestamp": 10000000000000,
                    "quantity": 10,
                    "status": "SUCCEED",
                    "fulfilmentActions":[
                        {
                            "fulfilmentType": "GRANT_ENTITLEMENT",
                            "status": "SUCCEED",
                            "result": "1234567"
                        }
                    ]
                },
                {
                    "self": {"href": "http://api.wan-san.com/v1/fulfilments/22222", "id": "22222"},
                    "itemReferenceId": "100000002",
                    "offerId": {"href": "http://api.wan-san.com/v1/offers/22222", "id": "22222"},
                    "timestamp": 10000000000000,
                    "quantity": 10,
                    "status": "PENDING",
                    "fulfilmentActions":[
                        {
                            "fulfilmentType": "GRANT_ENTITLEMENT",
                            "status": "SUCCEED",
                            "result": "1234567"
                        },
                        {
                            "fulfilmentType": "DEPOSIT_CSV",
                            "status": "SUCCEED",
                            "result": ""
                        },
                        {
                            "fulfilmentType": "DELIVER_PHYSICAL_GOODS",
                            "status": "PENDING",
                            "result": ""
                        }
                    ]
                }
            ],
            "shippingAddressId": {"href": "http://api.wan-san.com/v1/shipping-addresses/12345", "id": "12345"},
            "shippingMethodId": {"href": "http://api.wan-san.com/v1/shipping-methods/12345", "id": "12345"}
        }
