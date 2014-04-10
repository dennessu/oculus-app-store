#subscription
API about subscription

##resource subscription

        { 
            "href": "subscriptions/123",
            "key": 123,
            "userUri": "users/123",
            "status":"ENABLE", 
            "offerId":"Subs_Golden_Membership",
            "subsStartDate":"2014-01-01", 
            "subsEndDate":"2014-12-31", 
            "paymentMethodUri": "payment-methods/123",
            "partnerId": "OVR", 
            "tag": "GOLDEN_PLAYER", 
            "dateCreated": "2014-01-01", 
            "createdBy": "tester", 
            "dateModified": "2014-01-01", 
            "modifiedBy": "tester", 
        }

##resource subscription list

        {
            "href": "",
            "subscriptions": 
            [{ 
                "href": "subscriptions/123",
                "key": 123,
                "userUri": "users/123",
                "status":"ENABLE", 
                "offerId":"Subs_Golden_Membership",
                "subsStartDate":"2014-01-01", 
                "subsEndDate":"2014-12-31", 
                "paymentMethodUri": "payment-methods/123",
                "partnerId": "OVR", 
                "tag": "GOLDEN_PLAYER", 
                "dateCreated": "2014-01-01", 
                "createdBy": "tester", 
                "dateModified": "2014-01-01", 
                "modifiedBy": "tester", 
            }
            { 
                "href": "subscriptions/456",
                "key": 456,
                "userUri": "users/456",
                "status":"ENABLE", 
                "offerId":"Subs_Golden_Membership",
                "subsStartDate":"2014-01-01", 
                "subsEndDate":"2014-12-31", 
                "paymentMethodUri": "payment-methods/456",
                "partnerId": "OVR", 
                "tag": "GOLDEN_PLAYER", 
                "dateCreated": "2014-01-01", 
                "createdBy": "tester", 
                "dateModified": "2014-01-01", 
                "modifiedBy": "tester", 
            }] 
        }



## /subscriptions/{subscriptionid}
###GET
get subscription by subscription Id

+ Response 200 (application/json)

        the [subscription](#resource-subscription)


##POST subscriptions
purchase a new subscription

+ Request (application/json)

        the [subscription](#resource-subscription) which needs to be created 

+ Response 200 (application/json)

        the created [subscription](#resource-subscription)


##GET /users/{userid}/subscriptions?status={status}&tag={tag}&offerId={offerId}
get and filter all subscriptions for specified user

+ Response 200 (application/json)

    + status(optional)
    + tag(optional)
    + offerId(optional)

+ Response 200 (application/json)

    the result [subscription](#resource-subscriptions-list)      
