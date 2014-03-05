FORMAT: 1A

HOST: http://www.wan-san.com

# Order API

Order API is a collection of APIs that implements Order operation.

# Group Order

Fields:

+  <span id="self">self...href and id to identify the order.</span>
+  <span id="user">user...string to identity the customer</span>
+  <span id="trackingUuid">trackingUuid...tracking uuid of the order's creation transaction</span>
+  <span id="type">type...order type. can be payin, refund or payout</span>
+  <span id="status">status...the current status of order</span>
+  <span id="paymentInstruments">payment methods...the payment methods of the order</span>
+  <span id="country">country...2 character ISO 3166 country code.</span>
+  <span id="currency">currency...3 character ISO 4217 currency code.</span>
+  <span id="cart">cartId...shopping cart id used to create the order</span>
+  <span id="originalOrder">originalOrder...the original order id if this order is a refund</span>
+  <span id="refundOrders">refundOrders...list of the refund orders to this order</span>
+  <span id="shippingInfo">shippingInfo...the order's shipping info</span>
+  <span id="orderEvents">orderEvents...the event history of the order</span>
+  <span id="billingInfo">billingInfo...the billing info of this order</span>
+  <span id="orderItems">orderItems...the items of the order</span>
+  <span id="discounts">discounts...the order level discounts</span>
+  <span id="fulfillments">fulfillments...the order's fulfillments</span>
+  <span id="createdTime">createdTime...the order's created date</span>
+  <span id="createdBy">createdBy...indicates who creates the order</span>
+  <span id="updatedTime">updatedTime...the order's updated date</span>
+  <span id="updatedBy">updatedBy...indicates who modifies the order</span>


# Discounts [/Orders/Discount]

+ Model (application/json)

        {
            "self": {
                "href": "http://api.wan-san.com/orders/12345/discounts",
                "id": ""
            },
            "discounts":
            [{
                "self": {
                    "href": "http://api.wan-san.com/orders/12345/discounts/1",
                    "id": "1"
                }
                "order": {
                    "href": "http://api.wan-san.com/orders/12345",
                    "id": "12345"
                }
                "orderItem": {
                    "href":"http://api.wan-san.com/orders/12345/order-items/1",
                    "id": "1"
                }
                "discountType": "OfferDiscount",
                "discountAmount": 10.00,
                "discountRate": 0.16,
                "promotion": {
                    "href": "http://api.wan-san.com/promotions/12345",
                    "id": "12345"
                }
                "coupon": {
                    "href": "http://api.wan-san.com/coupons/ABCD-EFGH-0012"
                    "id": ""
                },
                "resourceAge": 1,
                "createdTime: "2014-01-25T17:08:07Z"
                "createdBy": "CheckoutService"
                "updatedTime: "2014-01-25T17:09:07Z"
                "updatedBy": "CheckoutService"
            }]
        }

# OrderEvents [/Orders/OrderEvents/]

+ Model (application/json)

        {
            "self": {
                "href": "http://api.wan-san.com/orders/12345/order-events",
                "id": ""
            },        
            [{
                "self": {
                    "href": "http://api.wan-san.com/orders/12345/order-events/1",
                    "id": "1"
                }
                "order": {
                    "href": "http://api.wan-san.com/orders/12345",
                    "id": "12345"
                }
                "action": "fulfill",
                "status": "completed",
                "resourceAge": 1,
                "createdTime: "2014-01-25 17:08:07",
                "createdBy": "CheckoutService",
                "updatedTime: "2014-01-25 17:09:07",
                "updatedBy": "CheckoutService"
            }]
        }

# OrderItemFulfillmentEvents [/OrderItems/FulfillmentEvents]

+ Model (application/json)
    
        {
            "self": {
                "href": "http://api.wan-san.com/orders/12345/fulfillment-events",
                "id": ""
            },
            [{
                "self": {
                    "href": "http://api.wan-san.com/orders/12345/orders-item/1/fulfillment-events/1",
                    "id": "1"
                }
                "orderItem": {
                    "href":"http://api.wan-san.com/orders/12345/order-items/1",
                    "id": "1"
                }
                "action": "fulfill",
                "status": "completed"
                "resourceAge": 1,
                "createdTime: "2014-01-25 17:08:07",
                "createdBy": "CheckoutService",
                "updatedTime: "2014-01-25 17:09:07",
                "updatedBy": "CheckoutService"
            }]
        }

# OrderItem [/OrderItems]

+ Model (application/json)

        {
            "self": {
                "href": "http://api.wan-san.com/orders/12345/order-items/1",
                "id": "12345"
            },
            "status": "fulfilled"
            "type": "Digital",
            "offer": {
                "href": "http://api.wan-san.com/offers/12345?revision=1",
                "id": "12345?revision=1"
            },
            "quantity": 1,
            "shippingInfo":
            {
                "shippingMethod": {
                    "href": "http://api.wan-san.com/shipping-methods/1",
                    "id": "1"
                },
                "shippingAddress": {
                    "href: "http://api.wan-san.com/shipping-addresses/12345",
                    "id": "12345"
                },
            }, // can override the order level shipping info
            "ratingInfo": {
                "unitPrice": 60.00,
                "quantity": 1,
                "totalAmount": 50.00,
                "totalDiscount": 10.00,
                "totalTax": 2.00
                "isTaxInclusive": false
                "totalPreorderAmount": 0.00,
                "totalPreorderTax": 0.00
            },
            "createdTime": "2014-01-25T17:08:10Z",
            "createdBy": "CheckoutService",
            "updatedTime": "2014-01-25T17:08:10Z",
            "updatedBy": "CheckoutService",
            "resourceAge": 1,
            "fulfillments": {
                "href": "http://api.wan-san.com/fulfillments?orderItem=12345",
                "id": ""
            },
            "fulfillmentEvents": {
                "href": "http://api.wan-san.com/orders/12345/order-items/1/fulfillmentEvents",
                "id": ""
            },
            "preorderInfo": {
                "billingTime": "2014-01-25T17:08:10Z"
                "preNotificationTime": "2014-01-25T17:08:10Z"
                "releaseTime": "2014-01-25T17:08:10Z"
                "updateHistory": [{
                        "updatedTime": "2014-01-25T17:08:10"
                        "updatedType": "Time"
                        "updatedProperty": "releaseTime"
                        "beforeValue": "2015-01-25T17:08:10Z"
                        "afterValue": "2014-01-25T17:08:10Z"
                }]
            },
            "sellerInfo": {
                "sellerProfiler": {
                    "href": "http://api.oculus.com/users/123456/user-profiles/1",
                    "id": "1",
                },
                "sellerTaxProfile": {
                    "href": "http://api.oculus.com/users/123456/tax-profiles/1",
                    "id": "1"
                }
            }
        }

# CompleteOrder [/CompleteOrder]

+ Model(application/json)

        {
            "self": {
                "href": "http://api.wan-san.com/orders/12345",
                "id": "12345"
            },
            "user": {
                "href": "http://api.oculus.com/users/123456",
                "id": "123456",
            },
            "trackingUuid" : "f47ac10b-58cc-4372-a567-0e02b2c3d479",
            "type: "PayIn", // "PayOut", ""
            "status: "completed",
            "country": "US",
            "currency": "USD",
            "tentative" false,
            "resourceAge": 1,
            "originalOrder": {
                "href": "http://api.wan-san.com/orders/12346",
                "id": "12346"
            }, // be there, if this is a refund order
            "createdTime: "2014-01-25 17:08:07",
            "createdBy": "CheckoutService",
            "updatedTime: "2014-01-25 17:09:07",
            "updatedBy": "CheckoutService",
            "ratingInfo:" {
                "totalAmount": 50.00,
                "totalShippingFee": 0.00
                "totalTax": 2.00,
                "isTaxInclusive": false,
                "totalDiscount": 10.00",
                "totalShippingFeeDiscount": 10.00
                "totalPreorderAmount": 0.00,
                "totalPreorderTax": 0.00,
                "HonorUntilTime": "2015-01-25T17:08:10Z"
            },
            "shippingInfo":
            {
                "shippingMethod": {
                    "href": "http://api.wan-san.com/shipping-methods/1",
                    "id": "1"
                },
                "shippingAddress": {
                    "href: "http://api.wan-san.com/shipping-addresses/12345",
                    "id": "12345"
                },
            },
            "paymentInstruments": [{
                "href": "http://api.wan-san.com/users/123/payment-instruments/12345",
                "id": "12345"
            }],
            "refundOrders": [{
                "http://api.wan-san.com/orders/12347"
                "id": "12347"
            }],
            "discounts": [{
                "href": "http://api.wan-san.com/orders/12345/discounts/1"
                "id": "1"
            }],
            "orderEvents": {
                "href": "http://api.wan-san.com/orders/12345/order-events"
                "id": ""
            },
            "balances": {
                    "href": "http://api.wan-san.com/balances?id=12345"
                    "id": ""
            },
            "orderItems": [{
                "href": "http://api.wan-san.com/orders/12345/order-items/1",
                "id": "1"
            }],
            "fulfillments": {
                "href": "http://api.wan-san.com/fulfillments?id=12345",
                "id": ""
            }
        }

# Quote [/Quotes]

+ Model(application/json)

        {
            "self": {
                "href": "http://api.wan-san.com/orders/12345",
                "id": "12345"
            },
            "user": {
                "href": "http://api.oculus.com/users/123456",
                "id": "123456",
            },
            "trackingUuid" : "f47ac10b-58cc-4372-a567-0e02b2c3d479",
            "type: "PayIn", // "PayOut", ""
            "status: "Open",
            "country": "US",
            "currency": "USD",
            "tentative": true,
            "resourceAge": 1,
            "originalOrder": {} // this is a refund order
            "createdTime: "2014-01-25 17:08:07",
            "createdBy": "CheckoutService",
            "updatedTime: "2014-01-25 17:09:07",
            "updatedBy": "CheckoutService",
            "ratingInfo:" {
                "totalAmount": 50.00,
                "totalShippingFee": 0.00
                "totalTax": 2.00,
                "isTaxInclusive": false,
                "totalDiscount": 10.00",
                "totalShippingFeeDiscount": 10.00
                "totalPreorderAmount": 0.00,
                "totalPreorderTax": 0.00,
                "HonorUntilTime": "2015-01-25T17:08:10Z"
            },
            "shippingInfo":
            {
                "shippingMethod": {
                    "href": "http://api.wan-san.com/shipping-methods/1",
                    "id": "1"
                },
                "shippingAddress": {
                    "href: "http://api.wan-san.com/shipping-addresses/12345",
                    "id": "12345"
                },
            },
            "paymentInstruments": [{
                "href": "http://api.wan-san.com/users/123/payment-instruments/12345",
                "id": "12345"
            }],
            "refundOrders": [],
            "discounts": {
                "href": "http://api.wan-san.com/orders/12345/discounts"
                "id": ""
            },
            "orderEvents": {
                "href": "http://api.wan-san.com/orders/12345/order-events"
                "id": ""
            },
            "balances": {},
            "orderItems": {
                "href": "http://api.wan-san.com/orders/12345/order-items",
                "id"
            },
            "fulfillments": {}
        }

# OrderUrls [/OrderUrls]

+ Model(application/json)

        {
            "self": {
                "href" : "http://api.wan-san.com/orders?user",
                "id": ""
            },
            orders
            [{
                "href": "http://api.wan-san.com/orders/12345",
                "id": "12345"
            }]
        }

## PUT /orders/{key}
Update orders

+ Request with Quote (application/json)

    + Body

            {   // update a tentative order to a real order. update the tentative to false
                "self": {}
                "user": {
                    "href": "http://api.oculus.com/users/123456",
                    "id": "123456",
                },
                "trackingUuid" : "f47ac10b-58cc-4372-a567-0e02b2c3d479",
                "type: "PayIn", // "PayOut", "Refund"
                "status: "Open",
                "country": "US",
                "currency": "USD",
                "tentative": false, // this is the updated column
                "resourceAge": null,
                "originalOrder": {} // this is a refund order
                "createdTime: null,
                "createdBy": null,
                "updatedTime: null,
                "updatedBy": null,
                "ratingInfo:" {
                    "totalAmount": 50.00,
                    "totalShippingFee": 0.00
                    "totalTax": 2.00,
                    "isTaxInclusive": false,
                    "totalDiscount": 10.00",
                    "totalShippingFeeDiscount": 10.00
                    "totalPreorderAmount": 0.00,
                    "totalPreorderTax": 0.00,
                    "HonorUntilTime": "2015-01-25T17:08:10Z"
                },
                "shippingInfo":
                {
                    "shippingMethod": {
                        "href": "http://api.wan-san.com/shipping-methods/1",
                        "id": "1"
                    },
                    "shippingAddress": {
                        "href: "http://api.wan-san.com/shipping-addresses/12345",
                        "id": "12345"
                    },
                },
                "paymentInstruments": [{
                    "href": "http://api.wan-san.com/users/123/payment-instruments/12345",
                    "id": "12345"
                }],
                "refundOrders": [],
                "discounts": [{
                    "href": "http://api.wan-san.com/orders/12345/discounts/1"
                    "id": "1"
                }],
                "orderEvents": {
                    "href": "http://api.wan-san.com/orders/12345/order-events"
                    "id": ""
                },
                "balances": {},
                "orderItems": [{
                    "href": "http://api.wan-san.com/orders/12345/order-items/1",
                    "id": "1"
                }],
                "fulfillments": {}
            }

+ Request with cancel order (application/json)

    + Body

            {   // update order status to canceled
                "self": {}
                "user": {
                    "href": "http://api.oculus.com/users/123456",
                    "id": "123456",
                },
                "trackingUuid" : "f47ac10b-58cc-4372-a567-0e02b2c3d479",
                "type: "PayIn", // "PayOut", "Refund"
                "status: "Canceled",
                "country": "US",
                "currency": "USD",
                "tentative": true,
                "resourceAge": null,
                "originalOrder": {} // this is a refund order
                "createdTime: null,
                "createdBy": null,
                "updatedTime: null,
                "updatedBy": null,
                "ratingInfo:" {
                    "totalAmount": 50.00,
                    "totalShippingFee": 0.00
                    "totalTax": 2.00,
                    "isTaxInclusive": false,
                    "totalDiscount": 10.00",
                    "totalShippingFeeDiscount": 10.00
                    "totalPreorderAmount": 0.00,
                    "totalPreorderTax": 0.00,
                    "HonorUntilTime": "2015-01-25T17:08:10Z"
                },
                "shippingInfo":
                {
                    "shippingMethod": {
                        "href": "http://api.wan-san.com/shipping-methods/1",
                        "id": "1"
                    },
                    "shippingAddress": {
                        "href: "http://api.wan-san.com/shipping-addresses/12345",
                        "id": "12345"
                    },
                },
                "paymentInstruments": [{
                    "href": "http://api.wan-san.com/users/123/payment-instruments/12345",
                    "id": "12345"
                }],
                "refundOrders": [],
                "discounts": [{
                    "href": "http://api.wan-san.com/orders/12345/discounts/1"
                    "id": "1"
                }],
                "orderEvents": {
                    "href": "http://api.wan-san.com/orders/12345/order-events"
                    "id": ""
                },
                "balances": {},
                "orderItems": [{
                    "href": "http://api.wan-san.com/orders/12345/order-items/1",
                    "id": "1"
                }],
                "fulfillments": {},
            }

+ Response 200 (application/json)

    [OrderUrls][]

## POST /orders
Post a tentative order without rating info.
Post a refund order.

+ Request with full object (application/json)

    + Body
    
            {   
                // POST a tentative order
                "self": {}
                "user": {
                    "href": "http://api.oculus.com/users/123456",
                    "id": "123456",
                },
                "trackingUuid" : "f47ac10b-58cc-4372-a567-0e02b2c3d479",
                "type: "PayIn", // "PayOut", "Refund"
                "status: null,
                "country": "US",
                "currency": "USD",
                "tentative" true, // this is the updated column
                "resourceAge": null,
                "originalOrder": {} // this is a refund order
                "createdTime: null,
                "createdBy": null,
                "updatedTime: null,
                "updatedBy": null,
                "ratingInfo:" {},
                "shippingInfo":
                {
                    "shippingMethod": {
                        "href": "http://api.wan-san.com/shipping-methods/1",
                        "id": "1"
                    },
                    "shippingAddress": {
                        "href: "http://api.wan-san.com/shipping-addresses/12345",
                        "id": "12345"
                    },
                },
                "paymentInstruments": [{
                    "href": "http://api.wan-san.com/users/123/payment-instruments/12345",
                    "id": "12345"
                }],
                "refundOrders": [],
                "orderEvents": {
                    "href": "http://api.wan-san.com/orders/12345/order-events"
                    "id": ""
                },
                "balances": {},
                "discounts": [{
                    "coupon": {
                        "href": "http://api.wan-san.com/coupons?code=ABCD-EFGH-0013",
                        "id": ""
                    }
                }],
                "orderItems": [{
                    offer: {
                        "href": "http://api.wan-san.com/offers/12345?revision=1",
                        "id": ""
                    },
                    "sku": {
                        "colour": "red",
                        "size": "black"
                    },
                    "quantity": "1"                
                }],
                "fulfillments": {},
            }

+ Request for refund (application/json)

    + Body
    
            {
                // POST a refund order
                "self": {}
                "user": {
                    "href": "http://api.oculus.com/users/123456",
                    "id": "123456",
                },
                "trackingUuid" : "f47ac10b-58cc-4372-a567-0e02b2c3d479",
                "type: "Refund", // "PayOut", "Refund"
                "status: null,
                "country": "US",
                "currency": "USD",
                "tentative" false, // this is the updated column
                "resourceAge": null,
                "originalOrder": {
                    "href": "http://api.wan-san.com/orders/12346",
                    "id": "12346"
                }, // be there, if this is a refund order
                "createdTime: null,
                "createdBy": null,
                "updatedTime: null,
                "updatedBy": null,
                "ratingInfo:" {},
                "shippingInfo":{},
                "paymentInstruments": [],
                "refundOrders": [],
                "discounts": [],
                "orderEvents": {},
                "balances": {},
                "coupons": [],
                "offers": [],
                "orderItems": []
                "fulfillments": {},
            }

+ Response 200 (application/json)

    [Quote][]

## POST /orders/{key}/order-items
Add new offers to the tentative order

+ Request

    + Body

            {
                "self": {}
                "orderItems": [{
                    offer: {
                        "href": "http://api.wan-san.com/offers/12345?revision=1",
                        "id": ""
                    },
                    "sku": {
                        "colour": "red",
                        "size": "black"
                    },
                    "quantity": "1"
                }],
                "fulfillments": {},
            }

+ Response 200 (application/json)

        {
            "self": {
                "href" : "http://api.wan-san.com/orders12345/order-items",
                "id": ""
            },
            orderItems
            [{
                "href": "http://api.wan-san.com/orders/12345/order-items/1",
                "id": "1"
            }]
        }

## POST /orders/{key}/discounts
Add new discounts to the tentative order

+ Request

    + Body

            {
                "self": {}
                "discounts": [{
                    coupon: {
                        "href": "http://api.wan-san.com/coupons?code=ABCD-EFGH-0013",
                        "id": ""
                    }]
            }

+ Response 200 (application/json)

        {
            "self": {
                "href" : "http://api.wan-san.com/orders12345/discounts",
                "id": ""
            },
            orderItems
            [{
                "href": "http://api.wan-san.com/orders/12345/discounts/1",
                "id": "1"
            }]
        }

## GET /orders/{id}
Get the order information with id

+ Parameters

    + id (required, string) ... indicates the order.

+ Response 200 (application/json)

    [CompleteOrder][]

## GET /orders(?trackingUuid)
Get the orders by trackingUuid.

+ Parameters

    + trackingUuid (required, uuid) ... indicates the tracking uuid when posting the orders.

+ Response 200 (application/json)
    
    [OrderUrls][]

##GET /orders(?user)
Get the orderUrls by user.

+ Parameters

    + user (required, string) ... indicates the user who owns the order.

+ Response 200 (application/json)
    
    [OrderUrls][]

## POST /orders/{key}/order-events
Update Order Status with order events

+ Request Resume Order (application/json)
            
    + Body

            {
                "trackingUuid": "f47ac10b-58cc-4372-a567-0e02b2c3d479"
                "action": "RiskReview",
                "status": "Completed",
                "properties": [{
                    "name":"RiskResult",
                    "namespace": "Risk",
                    "value": "Approved"
                }]
            }              

+ Request Update Billing Status (application/json)

    + Body

            {
                "trackingUuid": "f47ac10b-58cc-4372-a567-0e02b2c3d479"
                "action": "SettleBalance",
                "status": "Completed",
                "properties": [{
                    "name":"Balance",
                    "namespace": "Billing",
                    "value": "http://api.wan-san.com/balances/12345"
                }]
            }

+ Request update order with fulfillment status change

    + Body

            {
                "trackingUuid": "f47ac10b-58cc-4372-a567-0e02b2c3d479"
                "action": "Fulfill",
                "status": "Completed",
                "properties": [{
                    "name":"Fulfillment",
                    "namespace": "Fulfillment",
                    "value": "http://api.wan-san.com/fulfillments/12345"
                }]
            }

+ Response 200 (application/json)

    [OrderUrls][]

##GET /orders/{id}/discounts/
Get the discounts by order id.

+ Parameters

    + id (required, string) ... indicates the order.

+ Response 200 (application/json)
    
    [Discounts][]   

##GET /orders/{id}/order-events/
Get the order events by order id.

+ Parameters

    + id (required, string) ... indicates the order.

+ Response 200 (application/json)
    
    [OrderEvents][]  

##GET /orders/12345/order-items(?id)
Get the order item urls by order id.

+ Parameters

    + id (required, string) ... indicates the order.

+ Response 200 (application/json)
    
            [{
                "href":"http://api.wan-san.com/orders/12345/order-items/1",
                "id": 1
            }]

##GET /orders/12345/order-items/{id}/fulfillment-events
Get the order item fulfillment events by order item id.

+ Parameters

    + id (required, string) ... indicates the order item.

+ Response 200 (application/json)
    
    [OrderItemFulfillmentEvents][]