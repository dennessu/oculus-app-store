#entitlement
API about entitlement. trackingUuid is only used for post and put.

##resource entitlement
period is only used in post and put actions with timeUnit SECOND and has higher priority than expirationTime.

        { 
            "self": {
                "href": "https://data.oculusvr.com/v1/entitlements/123",
                "id": 123
            },
            "trackingUuid": "824afe80-9ebc-11e3-a5e2-0800200c9a66",
            "user": {
                "href": "https://data.oculusvr.com/v1/users/123",
                "id": 123
            },
            "developer": {
                "href": "https://data.oculusvr.com/v1/users/123456",
                "id": 123456
            },
            "offer": {
                "href": "https://data.oculusvr.com/v1/offers/12345",
                "id": 12345
            },
            "entitlementDefinition": {
                "href": "https://data.oculusvr.com/v1/entitlementDefinitions/123",
                "id": 123
            },
            "status":"ACTIVE",
            "grantTime": "2014-01-01T00:00:00Z", 
            "expirationTime": "2014-04-01T00:00:00Z",
            "period": 31536000,
            "consumable": true,
            "useCount": 10,
            "managedLifecycle": false,
            "group": "test",
            "tag": "test",
            "type": "DOWNLOAD"
        }

##resource entitlement list

        {
            "self":{
                "href": "https://data.oculusvr.com/v1/users/123/entitlements",
                "id": ""
            },
            "criteria": 
            [{ 
                "self": {
                    "href": "https://data.oculusvr.com/v1/entitlements/123",
                    "id": 123
                 },
                "user": {
                    "href": "https://data.oculusvr.com/v1/users/123",
                    "id": 123
                },
                "developer": {
                    "href": "https://data.oculusvr.com/v1/users/123456",
                    "id": 123456
                },
                "offer": {
                    "href": "https://data.oculusvr.com/v1/offers/12345",
                    "id": 12345
                },
                "entitlementDefinition": {
                    "href": "https://data.oculusvr.com/v1/entitlementDefinitions/123",
                    "id": 123
                },
                "status":"ACTIVE",
                "grantTime": "2014-01-01T00:00:00Z",
                "expirationTime": "2014-04-01T00:00:00Z",
                "consumable": true,
                "useCount": 10,
                "managedLifecycle": false,
                "group": "test",
                "tag": "test",
                "type": "DOWNLOAD"
            },
            { 
                "self": {
                    "href": "https://data.oculusvr.com/v1/entitlements/1234",
                    "id": 1234
                 },
                "user": {
                    "href": "https://data.oculusvr.com/v1/users/123",
                    "id": 123
                },
                "developer": {
                    "href": "https://data.oculusvr.com/v1/users/123456",
                    "id": 123456
                },
                "offer": {
                    "href": "https://data.oculusvr.com/v1/offers/123456",
                    "id": 123456
                },
                "entitlementDefinition": {
                    "href": "https://data.oculusvr.com/v1/entitlementDefinitions/1234",
                    "id": 1234
                },
                "status":"ACTIVE",
                "grantTime": "2014-01-01T00:00:00Z",
                "expirationTime": "2014-04-01T00:00:00Z",
                "consumable": true,
                "useCount": 10,
                "managedLifecycle": false,
                "group": "test",
                "tag": "test2",
                "type": "DOWNLOAD"
            }],
            "next": "END"
        }

##resource entitlementDefinition

        { 
            "self": {
                "href": "https://data.oculusvr.com/v1/entitlementDefinitions/123",
                "id": 123
            },
            "trackingUuid": "824afe80-9ebc-11e3-a5e2-0800200c9a66",
            "developerId": {
                "href": "https://data.oculusvr.com/v1/users/12345",
                "id": 12345
            },
            "type": "ONLINE_ACCESS",
            "group": "Football", 
            "tag": "ONLINE_ACCESS",
        }

##resource entitlementDefinition list

        {
            "self":{
                "href": "https://data.oculusvr.com/v1/entitlementDefinitions?type=ONLINE_ACCESS",
                "id": ""
            },
            "criteria": 
            [{ 
                "self": {
                    "href": "https://data.oculusvr.com/v1/entitlementDefinitions/123",
                    "id": 123
                },
                "developerId": {
                    "href": "https://data.oculusvr.com/v1/users/12345",
                    "id": 12345
                },
                "type": "ONLINE_ACCESS",
                "group": "Football", 
                "tag": "ONLINE_ACCESS"
                },
                { 
                "self": {
                    "href": "https://data.oculusvr.com/v1/entitlementDefinitions/234",
                    "id": 123
                },
                "developerId": {
                    "href": "https://data.oculusvr.com/v1/users/12345",
                    "id": 12345
                },
                "type": "ONLINE_ACCESS",
                "group": "Basketball",
                "tag": "ONLINE_ACCESS"
            }],
            "next": "https://data.oculusvr.com/v1/entitlementDefinitions?type=ONLINE_ACCESS&start=50"
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

+ Response 400 (application/json)

        {
            "message": "ENTITLEMENT_NOT_FOUND"
        }

##POST /entitlements
create a new entitlement

+ Request (application/json)

    the [entitlement](#resource-entitlement) which needs to be created

+ Response 200 (application/json)

    the created [entitlement](#resource-entitlement)


##GET /users/{userId}/entitlements?developerId={developerId}status={status}&type={type}&offerId={offerId}&group={group}&tag={tag}&definitionId={definitionId}
get and filter all entitlements for specified user.

+ Parameters

    + developerId(required)
    + status(optional)
    + type(optional)
    + offerId(optional)
    + group(optional)
    + tag(optional)
    + definitionId(optional)
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
            "trackingUuid": "824afe80-9ebc-11e3-a5e2-0800200c9a66",
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

        {
            "message": "INVALID_ENTITLEMENT"
        }

## /entitlement-definitions/{id}
###GET
get entitlementDefinition by entitlementDefinition Id

+ Response 200 (application/json)

    the [entitlementDefinition](#resource-entitlementdefinition)

+ Response 404

###PUT
update an existing entitlementDefinition

+ Request (application/json)

    the entire [entitlementDefinition](#resource-entitlementdefinition) which needs to be updated

+ Response 200 (application/json)

    the updated [entitlementDefinition](#resource-entitlementdefinition)

###DELETE
delete an existing entitlementDefinition

+ Response 204

+ Response 400 (application/json)

        {
            "message": "ENTITLEMENT_DEFINITION_NOT_FOUND"
        }

##POST /entitlement-definitions
create a new entitlementDefinition

+ Request (application/json)

    the [entitlementDefinition](#resource-entitlementdefinition) which needs to be created

+ Response 200 (application/json)

    the created [entitlementDefinition](#resource-entitlementdefinition)


##GET /entitlement-definitions?developerId={developerId}type={type}&group={group}&tag={tag}
get and filter entitlementDefinitions.

+ Parameters

    + developerId(required)
    + type(optional)
    + group(optional)
    + tag(optional)

+ Response 200 (application/json)

    the result [entitlementDefinitions](#resource-entitlementdefinition-list)        
