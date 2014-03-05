FORMAT: 1A

HOST: http://www.wan-san.com

# Balance API

Balance API is a collection of APIs that implements Balance operation.

# Group Balance

Fields:

+  <span id="balanceId">balanceId...string to identify the balance.</span>
+  <span id="orderId">orderId...string to identify the order.</span>
+  <span id="piId">orderId...string to identify the payment instructment to collect this balance.</span>
+  <span id="country">country...2 character ISO 3166 country code.</span>
+  <span id="currency">currency...3 character ISO 4217 currency code.</span>
+  <span id="dueDate">dueDate...due date to collect the balance.</span>
+  <span id="balanceItems">balanceItems...list of the balance items.</span>
+  <span id="createdDate">createdDate...the balance's created date</span>
+  <span id="createdBy">createdBy...indicates who creates the balance</span>
+  <span id="modifiedDate">modifiedDate...the balance's modified date</span>
+  <span id="modifiedBy">modifiedBy...indicates who modifies the balance</span>


## POST /balances
Create balances for an order

+ Request
    + Headers

            Delegate-User-Id: "users/54321"
            Requestor-Id: "CheckoutService"
            On-Behalf-Of-Requestor-Id: "DigitalGameStore"
            Tracking-Uuid: "f47ac10b-58cc-4372-a567-0e02b2c3d479"
            User-Ip: "157.123.45.67"

    + Body
        
            [
            {
                "order": {
                     "href": "http://api.wan-san.com/orders/12345",
                     "id": "12345"
                 },
                "pi": {
                    "href": "http://api.wan-san.com/pis/12345",
                    "id": "12345"
                },
                "type": "DEBIT",
                "status": "SETTLE",
                "currency": "USD",
                "country": "US",
                "balanceItems":
                [
                {
                    "orderItem": {
                        "href": "http://api.wan-san.com/order-items/12345",
                        "id": "12345"
                    },
                    "amount": 9.99,
                    "financeId": "9999",
                    "taxIncluded": false,
                    "taxItems":
                    [{
                        "taxAuthority": "CITY",
                        "taxAmount": 0.45,
                        "taxRate": 0.045
                    },
                    {
                        "taxAuthority": "STATE",
                        "taxAmount": 0.5,
                        "taxRate": 0.05
                    }],
                    "discountItems":
                    [{
                        "discountAmount": 1.00
                    }]
                }
                ]
            }
            ]
    
+ Response 200 (application/json)
        
            {
                "self": {
                    "href": "http://api.wan-san.com/balances/70953532335535",
                    "id": "70953532335535"
                },
                "pi": {
                    "href": "http://api.wan-san.com/pis/12345",
                    "id": "12345"
                },
                "type": "DEBIT",
                "status": "SETTLE",
                "totalAmount": 9.94,
                "taxAmount": 0.95,
                "taxIncluded": false,
                "currency": "USD",
                "country": "US",
                "balanceItems": [
                    {
                        "self": {
                            "href": "http://api.wan-san.com/balance-items/51192766726543",
                            "id": "51192766726543"
                        },
                        "orderItem": {
                            "href": "http://api.wan-san.com/order-items/12345",
                            "id": "12345"
                        },
                        "amount": 9.99,
                        "taxAmount": 0.95,
                        "taxIncluded": false,
                        "financeId": "9999",
                        "taxItems": [
                            {
                                "self": {
                                    "href": "http://api.wan-san.com/tax-items/3905342030782",
                                    "id": "3905342030782"
                                },
                                "taxAuthority": "STATE",
                                "taxAmount": 0.5,
                                "taxRate": 0.05
                            },
                            {
                                "self": {
                                    "href": "http://api.wan-san.com/tax-items/1866799348859",
                                    "id": "1866799348859"
                                },
                                "taxAuthority": "CITY",
                                "taxAmount": 0.45,
                                "taxRate": 0.045
                            }
                        ],
                        "discountItems": [
                            {
                                "self": {
                                    "href": "http://api.wan-san.com/discount-items/64537080382892",
                                    "id": "64537080382892"
                                },
                                "discountAmount": 1
                            }
                        ]
                    }
                ]
            }

##GET /balances/{key}
Get the balances information with balance id

+ Parameters

    + key (required, string) ... indicates the balance.

+ Request

    + Headers

            Delegate-User-Id: "users/54321"
            Requestor-Id: "CheckoutService"
            On-Behalf-Of-Requestor-Id: "DigitalGameStore"
            User-Ip: "157.123.45.67"

+ Response 200 (application/json)

            {
                "self": {
                    "href": "http://api.wan-san.com/balances/70953532335535",
                    "id": "70953532335535"
                },
                "pi": {
                    "href": "http://api.wan-san.com/pis/12345",
                    "id": "12345"
                },
                "type": "DEBIT",
                "status": "SETTLE",
                "totalAmount": 9.94,
                "taxAmount": 0.95,
                "taxIncluded": false,
                "currency": "USD",
                "country": "US",
                "balanceItems": [
                    {
                        "self": {
                            "href": "http://api.wan-san.com/balance-items/51192766726543",
                            "id": "51192766726543"
                        },
                        "orderItem": {
                            "href": "http://api.wan-san.com/order-items/12345",
                            "id": "12345"
                        },
                        "amount": 9.99,
                        "taxAmount": 0.95,
                        "taxIncluded": false,
                        "financeId": "9999",
                        "taxItems": [
                            {
                                "self": {
                                    "href": "http://api.wan-san.com/tax-items/3905342030782",
                                    "id": "3905342030782"
                                },
                                "taxAuthority": "STATE",
                                "taxAmount": 0.5,
                                "taxRate": 0.05
                            },
                            {
                                "self": {
                                    "href": "http://api.wan-san.com/tax-items/1866799348859",
                                    "id": "1866799348859"
                                },
                                "taxAuthority": "CITY",
                                "taxAmount": 0.45,
                                "taxRate": 0.045
                            }
                        ],
                        "discountItems": [
                            {
                                "self": {
                                    "href": "http://api.wan-san.com/discount-items/64537080382892",
                                    "id": "64537080382892"
                                },
                                "discountAmount": 1
                            }
                        ]
                    }
                ]
            }

## GET /balances(?balanceId)
Get the balances by balanceId.

+ Parameters

    + balanceId (required, loong) ... indicates the tracking uuid when posting the balances.

+ Request

    + Headers

            Delegate-User-Id: "users/54321"
            Requestor-Id: "CheckoutService"
            On-Behalf-Of-Requestor-Id: "DigitalGameStore"
            User-Ip: "157.123.45.67"


+ Response 200 (application/json)
    
            {
                "balanceUri":  "/balances/98765432"
            }     


## GET /balances(?orderId)
Get the balances by orderId.

+ Parameters

    + orderId (required, long) ... indicates the order id when posting the balances.

+ Request

    + Headers

            Delegate-User-Id: "users/54321"
            Requestor-Id: "CheckoutService"
            On-Behalf-Of-Requestor-Id: "DigitalGameStore"
            User-Ip: "157.123.45.67"


+ Response 200 (application/json)
    
            {
                "balanceUri":  "/balances/98765432"
                "balanceUri":  "/balances/98765431"
            }     
