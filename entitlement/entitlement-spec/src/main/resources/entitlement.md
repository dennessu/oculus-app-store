#entitlement
API about entitlement. trackingUuid is only used for post and put.

##resource entitlement

       {
           "self": {                 //client immutable
               "href": "/v1/entitlements/123",
               "id": “123”
           },
           "user": {
               "href": "/v1/users/123",
               "id": “123”
           },
           "item": {
               "href": "/v1/items/123",
               "id": "123"
           },
           "isActive": true,         //client immutable
           "isSuspended": false,
           "grantTime": "2014-01-01T00:00:00Z",
           "expirationTime": "2014-04-01T00:00:00Z",
           "useCount": null,
           "entitlementType": "DOWNLOAD",
           "futureExpansion": {},                    //required, never null, empty (‘{}’) if no properties at this time
           "rev": "k930dkd03kdk3k3k5",
           "createdTime": "2013-01-01T01:32:53Z",  //client immutable; must be ISO 8601
           "updatedTime": "2013-01-01T01:32:53Z",  //client immutable; must be ISO 8601
           "admininfo": {                            //present only for highly authorized users/processes and only when ?adminInfo=true is specified
               "createdBy": {"href": "/v1/users/1234", "id": "1234"}, //client immutable
               "updatedBy": {"href": "/v1/users/1234", "id": "1234"}  //client immutable
          }
       }

##resource entitlement list

        {
            "self":{
                "href": "https://data.oculusvr.com/v1/users/123/entitlements",
                "id": ""
            },
            "items": 
            [{
                 "self": {                 //client immutable
                     "href": "/v1/entitlements/123",
                     "id": “123”
                 },
                 "user": {
                     "href": "/v1/users/123",
                     "id": “123”
                 },
                 "item": {
                     "href": "/v1/items/123",
                     "id": "123"
                 },
                 "isActive": true,         //client immutable
                 "isSuspended": false,
                 "grantTime": "2014-01-01T00:00:00Z",
                 "expirationTime": "2014-04-01T00:00:00Z",
                 "useCount": null,
                 "entitlementType": "DOWNLOAD",
                 "futureExpansion": {},                    //required, never null, empty (‘{}’) if no properties at this time
                 "rev": "k930dkd03kdk3k3k5",
                 "createdTime": "2013-01-01T01:32:53Z",  //client immutable; must be ISO 8601
                 "updatedTime": "2013-01-01T01:32:53Z",  //client immutable; must be ISO 8601
                 "admininfo": {                            //present only for highly authorized users/processes and only when ?adminInfo=true is specified
                     "createdBy": {"href": "/v1/users/1234", "id": "1234"}, //client immutable
                     "updatedBy": {"href": "/v1/users/1234", "id": "1234"}  //client immutable
                }
             },
            {
                "self": {                 //client immutable
                    "href": "/v1/entitlements/456",
                    "id": “456”
                },
                "user": {
                    "href": "/v1/users/123",
                    "id": “123”
                },
                "item": {
                    "href": "/v1/items/234",
                    "id": "234"
                },
                "isActive": true,         //client immutable
                "isSuspended": false,
                "grantTime": "2014-01-01T00:00:00Z",
                "expirationTime": "2014-04-01T00:00:00Z",
                "useCount": null,
                "entitlementType": "DOWNLOAD",
                "futureExpansion": {},                    //required, never null, empty (‘{}’) if no properties at this time
                "rev": "k930dkd03kdk3k3k5",
                "createdTime": "2013-01-01T01:32:53Z",  //client immutable; must be ISO 8601
                "updatedTime": "2013-01-01T01:32:53Z",  //client immutable; must be ISO 8601
                "admininfo": {                            //present only for highly authorized users/processes and only when ?adminInfo=true is specified
                    "createdBy": {"href": "/v1/users/1234", "id": "1234"}, //client immutable
                    "updatedBy": {"href": "/v1/users/1234", "id": "1234"}  //client immutable
               }
            }],
            "next": "END"
        }


## /entitlements/{id}
###GET
get entitlement by entitlement Id

+ Response 200 (application/json)

    the [entitlement](#resource-entitlement)

+ Response 404

###PUT
update an existing entitlement

+ Request (application/json)

    the entire [entitlement](#resource-entitlement) which needs to be updated

+ Response 200 (application/json)

    the updated [entitlement](#resource-entitlement)

###DELETE
delete an existing entitlement

+ Response 204

+ Response 404 (application/json)

##POST /entitlements
create a new entitlement

+ Request (application/json)

    the [entitlement](#resource-entitlement) which needs to be created

+ Response 200 (application/json)

    the created [entitlement](#resource-entitlement)


##GET /entitlements?userId={userId}&isActive={isActive}&isSuspended={isSuspended}&itemIds={itemIds}&type={type}
get and filter all entitlements for specified user.

+ Parameters

    + userId(required)
    + isActive(optional)
    + isSuspended(optional)
    + type(optional)
    + itemIds(optional)...can have more than one
    + startGrantTime(optional)
    + endGrantTime(optional)
    + startExpirationTime(optional)
    + endExpirationTime(optional)
    + lastModifiedTime(optional)

+ Response 200 (application/json)

    the result [entitlements](#resource-entitlement-list)        

##POST /entitlements/transfer
transfer an entitlement to another user

+ Request (application/json)

        {
            "targetUser": {
                "href": "https://data.oculusvr.com/v1/users/12345",
                "id": 12345
            }, 
            "entitlement": {
                "href": "https://data.oculusvr.com/v1/entitlements/123",
                "id": 123
            }
        }

+ Response 200 (application/json)

    the transferred [entitlement](#resource-entitlement)

+ Response 400 (application/json)