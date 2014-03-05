#entitlement
API about entitlement.

## /entitlements/{entitlement_id}
###GET
get entitlement by entitlement Id

+ Response 200 (application/json)

        { 
            "entitlementId": 123, 
            "type": "ONLINE_ACCESS",
            "status":"ACTIVE" 
            "group": "Football", 
            "tag": "GOLDEN_PLAYER", 
            "grantDate": "2013-01-01", 
            "expirationDate": null 
        }

###PUT
update an existing entitlement

+ Request (application/json)

        { 
            "expirationDate": "2013-12-30" 
        }

+ Response 200 (application/json)

        {
            "entitlementUri":  "/entitlements/123" 
        }

###DELETE
delete an existing entitlement

+ Response 204

+ Response 500 (application/json)

        {
            "status": "failed" 
            "errorMsg": "INVALID_ENTITLEMENT_ID " 
        }

##POST /entitlements
create a new entitlement

+ Request (application/json)

        { 
            "type": "ONLINE_ACCESS", 
            "group": "Football", 
            "tag": "GOLDEN_PLAYER", 
            "grantDate": "2013-01-01", 
            "expirationDate": null 
        } 

+ Response 200 (application/json)

        {
            "entitlementUri":  "/entitlements/123" 
        }


##GET /users/{userId}/entitlements
get and filter all entitlements for specified user. Should use search for complex filter.

+ Parameters

    + status
    + type
    + group
    + tag

+ Response 200 (application/json)

        {
            "entitlements": 
            [{ 
                "entitlementId": 123, 
                "type": "ONLINE_ACCESS", 
                ... 
            }, 
            { 
                "entitlementId": 234, 
                "type": "SUBSCRIPTION", 
                ... 
            }] 
        }

##POST /entitlements/search
filter entitlements 

+ Request (application/json)

        {
            "group": "Football", 
            "startGrantDate":"2011-01-01", 
            "endGrantDate": "2011-02-02", 
            "startExpirationDate": "2012-09-09", 
            "endExpirationDate": "2012-10-10" 
        }

+ Response 200 (application/json)

        {
            "entitlements":
            [{ 
                "entitlementId": 134, 
                "type": "ONLINE_ACCESS", 
                ... 
            }, 
            { 
                "entitlementId": 256, 
                "type": "SUBSCRIPTION", 
                ... 
            }] 
        }

##POST /entitlements/transfer
transfer an entitlement to another user

+ Request (application/json)

        {
            "userId": "user1", 
            "targetUserId": "user2", 
            "entitlementId": "123"
        }

+ Response 200 (application/json)

        {
            "status": "success" 
            "entitlementUri": "/entitlements/456" 
        }

+ Response 500 (application/json)

        {
            "status": "failed" 
            "errorMsg": "INVALID_ENTITLEMENT" 
        }
