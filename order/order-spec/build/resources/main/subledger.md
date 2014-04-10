FORMAT: 1A

HOST: http://www.wan-san.com

# Subledger API

Subledger API is a collection of APIs that implements Subledger operation.

# Group Subledger

Fields:

+  <span id="seller">seller...string to identify the seller.</span>
+  <span id="sellerTaxProfile">sellerTaxProfileId...seller's active taxProfile. </span>
+  <span id="subledger">subledgerId...the subledger</span>
+  <span id="payoutStatus">payoutStatus...the status of the payout balance</span>
+  <span id="payoutDate">payoutDate...the scheduled payout date</span>
+  <span id="country">country...2 character ISO 3166 country code.</span>
+  <span id="currency">currency...3 character ISO 4217 currency code.</span>
+  <span id="payoutAmount">payoutAmount...the payout amount</span>

# Subledger [/Subledger]

+ Model(application/json)

        {
            "self": {
                "href": "http://api.oculus.com/subledgers/123456",
                "id": "123456"
            },
            "seller": {
                "href": "http://api.oculus.com/users/123456",
                "id": "123456",
            },
            "sellerTaxProfile": {
                "href": "http://api.oculus.com/taxProfile/123456",
                "id": "123456"
            }
            "payoutStatus":"pending",
            "payoutTime":"2014-02-25T17:08:07Z",
            "country":"US",
            "currency":"USD",
            "payoutAmount":"123.45",
            "rev": 1,
            "createdTime":"2014-02-25T17:08:07Z",
            "createdBy":"payoutService",
            "modifiledTime":"2014-02-25T17:08:07Z",
            "modifiledBy":"payoutService"
         }

# Subledgers [/Subledgers]

+ Model(application/json)

        {
            "self" :
            {
                "href": "http://api.oculus.com/subledgers?sellerId=12345"
                "id": ""
            }
            [{
                "self": {
                    "href": "http://api.oculus.com/subledgers/123456",
                    "id": "123456"
                },
                "seller": {
                    "href": "http://api.oculus.com/users/123456",
                    "id": "123456",
                },
                "sellerTaxProfile": {
                    "href": "http://api.oculus.com/taxProfile/123456",
                    "id": "123456"
                }
                "payoutStatus":"pending",
                "payoutTime":"2014-02-25T17:08:07Z",
                "country":"US",
                "currency":"USD",
                "payoutAmount":"123.45",
                "rev": 1,
                "createdTime":"2014-02-25T17:08:07Z",
                "createdBy":"payoutService",
                "modifiledTime":"2014-02-25T17:08:07Z",
                "modifiledBy":"payoutService"
             },
            {
                "self": {
                    "href": "http://api.oculus.com/subledgers/123457",
                    "id": "123457"
                },
                "seller": {
                    "href": "http://api.oculus.com/users/123456",
                    "id": "123456",
                },
                "sellerTaxProfile": {
                    "href": "http://api.oculus.com/taxProfile/123456",
                    "id": "123456"
                }
                "payoutStatus":"completed",
                "payoutTime":"2014-01-25T17:08:07Z",
                "country":"US",
                "currency":"USD",
                "payoutAmount":"123.45",
                "rev": 1,
                "createdTime":"2014-01-25T17:08:07Z",
                "createdBy":"payoutService",
                "modifiledTime":"2014-01-25T17:08:07Z",
                "modifiledBy":"payoutService"
            }]
        }

# SubledgerItem [/Subledgers/SubledgerItem]

+ Model(application/json)

        {
                "subledger": {
                    "href": "http://api.oculusvr.com/subledger/123456",
                    "id": "12345"
                }
                "orderItem": {
                    "href": "http://api.oculusvr.com/order-items/12345"
                    "id": "12345"
                }
                "rev": 1
         }

## GET /subledgers(?sellerId, status, fromDate, toDate)
Get seller subledgers with seller id.

+ Parameters

    + sellerId(required, string) ... indicates the seller's id.
    + status (optional, string) ... filters the status.
    + fromDate (optional, date) ... indicates the from date.
    + toDate (optional, date) ... indicates the to date.

+ Request

    + Headers

            Delegate-User-Id: "users/54321"
            Requestor-Id: "PayoutService"
            On-Behalf-Of-Requestor-Id: "DigitalGameStore"
            User-Ip: "157.123.45.67"

+ Response 200 (application/json)

    [Subledgers][]

## GET /subledgers/{key}
Get seller subledger with the subledger id.

+ Parameters

    + key(required, string) ... indicates the subledger id.

+ Request

    + Headers

            Delegate-User-Id: "users/54321"
            Requestor-Id: "PayoutService"
            On-Behalf-Of-Requestor-Id: "DigitalGameStore"
            User-Ip: "157.123.45.67"

+ Response 200 (application/json)

    [Subledger][]

##POST /subledger-items
Append a payin item to payout subledger.

+ Request (application/json)

    + Headers

            Delegate-User-Id: "users/54321"
            Requestor-Id: "PayoutService"
            On-Behalf-Of-Requestor-Id: "DigitalGameStore"
            Tracking-Uuid: "f47ac10b-58cc-4372-a567-0e02b2c3d479"
            User-Ip: "157.123.45.67"

    + Body
    
            {
                "subledger": {
                    "href": "http://api.oculusvr.com/subledger/123456",
                    "id": "12345"
                }
                "orderItem": {
                    "href": "http://api.oculusvr.com/order-items/12345"
                    "id": "12345"
                }
            }

+ Response 200 (application/json)

    [SubledgerItem][]