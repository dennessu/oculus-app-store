#subscription
API about subscription

## /subscriptions/{subscriptionid}
###GET
get subscription by subscription Id

+ Response 200 (application/json)

        { 
            "uri": "subscriptions/123",
            "userUri": "users/123",
            "type": "ONLINE_ACCESS",
            "status":"ENABLE" 
            "partnerId": "OVR", 
            "tag": "GOLDEN_PLAYER", 
            "dateCreated": "2014-01-01", 
            "dateModified": "2014-01-01" 
        }


##POST /users/{userid}/subscriptions
purchase a new subscription

+ Request (application/json)

        { 
            "offerId": "SUBSCRIPTION_FREE_01", 
            "paymentMethodId": "123"
        } 

+ Response 200 (application/json)

        {
            "uri": "/subscriptions/123",
            "userUri": "/users/123",
            "type": "ONLINE_ACCESS",
            "status":"ENABLE" 
            "partnerId": "OVR", 
            "tag": "GOLDEN_PLAYER", 
            "dateCreated": "2014-01-01", 
            "dateModified": "2014-01-01" 
        }


##GET /users/{userid}/subscriptions
get all subscriptions for specified user

+ Response 200 (application/json)

        {
            "subscriptions": 
            [{ 
                "uri": "/subscriptions/123",
                "status": "ENABLE", 
                ... 
            }, 
            { 
                "uri": "/subscriptions/234",
                "status": "ENABLE", 
                ... 
            }] 
        }

