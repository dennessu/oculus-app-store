#fulfilment
API about fulfilment

## /fulfilments
###POST
post new fulfilments

+ Request (application/json)

        {
            "userId": {"href": "http://api.wan-san.com/v1/users/12345", "actionId": "12345"},
            "orderId": {"href": "http://api.wan-san.com/v1/orders/12345", "actionId": "12345"},
            "trackingGuid": "151907d5-807e-4a69-83e6-e709b6f75359",
            "items":[
                {
                    "orderItemId": {"href": "http://api.wan-san.com/v1/billing-lineitems/100000001", "actionId": "100000001"},
                    "offerId": {"href": "http://api.wan-san.com/v1/offers/11111", "actionId": "11111"},
                    "offerRevision": 1,
                    "quantity": 1
                },
                {
                    "orderItemId": {"href": "http://api.wan-san.com/v1/billing-lineitems/100000002", "actionId": "100000002"},
                    "offerId": {"href": "http://api.wan-san.com/v1/offers/22222", "actionId": "22222"},
                    "offerRevision": 2,
                    "quantity": 10
                }
            ],
            "shippingAddressId": {"href": "http://api.wan-san.com/v1/shipping-addresses/12345", "actionId": "12345"},
            "shippingMethodId": {"href": "http://api.wan-san.com/v1/shipping-methods/12345", "actionId": "12345"}
        }


+ Response 200 (application/json)

        {
            "self": {"href": "http://api.wan-san.com/v1/fulfilments?orderId=123456789, "actionId":""},
            "userId": {"href": "http://api.wan-san.com/v1/users/12345", "actionId": "12345"},
            "orderId": {"href": "http://api.wan-san.com/v1/orders/12345", "actionId": "12345"},
            "trackingGuid": "151907d5-807e-4a69-83e6-e709b6f75359",
            "items":[
                {
                    "self": {"href": "http://api.wan-san.com/v1/fulfilments/11111", "actionId": "11111"},
                    "orderItemId": {"href": "http://api.wan-san.com/v1/billing-lineitems/100000001", "actionId": "100000001"},
                    "offerId": {"href": "http://api.wan-san.com/v1/offers/11111", "actionId": "11111"},
                    "offerRevision": 1,
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
                    "self": {"href": "http://api.wan-san.com/v1/fulfilments/22222", "actionId": "22222"},
                    "orderItemId": {"href": "http://api.wan-san.com/v1/billing-lineitems/100000002", "actionId": "100000002"},
                    "offerId": {"href": "http://api.wan-san.com/v1/offers/22222", "actionId": "22222"},
                    "offerRevision": 2,
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
            "shippingAddressId": {"href": "http://api.wan-san.com/v1/shipping-addresses/12345", "actionId": "12345"},
            "shippingMethodId": {"href": "http://api.wan-san.com/v1/shipping-methods/12345", "actionId": "12345"}

        }

## /fulfilments/{key}
###GET
get fulfilment detail

+ Response 200 (application/json)

        {
            "self": {"href": "http://api.wan-san.com/v1/fulfilments/11111", "actionId": "11111"},
            "orderItemId": {"href": "http://api.wan-san.com/v1/billing-lineitems/100000001", "actionId": "100000001"},
            "offerId": {"href": "http://api.wan-san.com/v1/offers/11111", "actionId": "11111"},
            "offerRevision": 1,
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
            "self": {"href": "http://api.wan-san.com/v1/fulfilments?orderId=123456789, "actionId":""},
            "userId": {"href": "http://api.wan-san.com/v1/users/12345", "actionId": "12345"},
            "orderId": {"href": "http://api.wan-san.com/v1/orders/123456789", "actionId": "123456789"},
            "trackingGuid": "151907d5-807e-4a69-83e6-e709b6f75359",
            "items":[
                {
                    "self": {"href": "http://api.wan-san.com/v1/fulfilments/11111", "actionId": "11111"},
                    "orderItemId": {"href": "http://api.wan-san.com/v1/billing-lineitems/100000001", "actionId": "100000001"},
                    "offerId": "{"href": "http://api.wan-san.com/v1/offers/11111", "actionId": "11111"},
                    "offerRevision": 1,
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
                    "self": {"href": "http://api.wan-san.com/v1/fulfilments/22222", "actionId": "22222"},
                    "orderItemId": {"href": "http://api.wan-san.com/v1/billing-lineitems/100000002", "actionId": "100000002"},
                    "offerId": {"href": "http://api.wan-san.com/v1/offers/22222", "actionId": "22222"},
                    "offerRevision": 2,
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
            "shippingAddressId": {"href": "http://api.wan-san.com/v1/shipping-addresses/12345", "actionId": "12345"},
            "shippingMethodId": {"href": "http://api.wan-san.com/v1/shipping-methods/12345", "actionId": "12345"}
        }
