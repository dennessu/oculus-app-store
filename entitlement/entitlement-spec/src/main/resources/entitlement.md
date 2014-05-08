#entitlement
API about entitlement. trackingUuid is only used for post and put.

##resource entitlement

       { 
            "self": { 
                "href": "https://data.oculusvr.com/v1/entitlements/123",
                "id": “123”
            },
            "rev": "k930dkd03kdk3k3k5", 
            "isActive": true, 
            “isSuspended”: false, 
            "user": {
                "href": "https://data.oculusvr.com/v1/users/123",
                "id": “123”
            },
            "grantTime": "2014-01-01T00:00:00Z", 
            "expirationTime": "2014-04-01T00:00:00Z",
            "useCount": 10,
            "entitlementDefinition": {
                "href": "https://data.oculusvr.com/v1/entitlement-definitions/123",
                "id": “123”
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
                "self": { 
                    "href": "https://data.oculusvr.com/v1/entitlements/123",
                    "id": “123”
                },
                "rev": "k930dkd03kdk3k3k5", 
                "isActive": true, 
                “isSuspended”: false, 
                "user": {
                    "href": "https://data.oculusvr.com/v1/users/123",
                    "id": “123”
                },
                "grantTime": "2014-01-01T00:00:00Z", 
                "expirationTime": "2014-04-01T00:00:00Z",
                "useCount": 10,
                "entitlementDefinition": {
                    "href": "https://data.oculusvr.com/v1/entitlement-definitions/123",
                    "id": “123”
                }
            },
            { 
                "self": { 
                    "href": "https://data.oculusvr.com/v1/entitlements/123",
                    "id": “234”
                },
                "rev": "k930dkd03kdk3k3k5", 
                "isActive": true, 
                “isSuspended”: false, 
                "user": {
                    "href": "https://data.oculusvr.com/v1/users/123",
                    "id": “123”
                },
                "grantTime": "2014-01-01T00:00:00Z", 
                "expirationTime": "2014-04-01T00:00:00Z",
                "useCount": 10,
                "entitlementDefinition": {
                    "href": "https://data.oculusvr.com/v1/entitlement-definitions/123",
                    "id": “124”
                }
            }],
            "next": "END"
        }

##resource entitlementDefinition

        { 
            "self": {
                "href": "https://data.oculusvr.com/v1/entitlement-definitions/123",
                "id": “123”
            },
            "rev": "k930dkd03kdk3k3k5", 
            "developer": {
                href": "https://data.oculusvr.com/v1/users/12345",
                id": “12345”
            },
            "allowedClients": [
                {
                    “href”: “https://data.oculusvr.com/v1/oauth2/clients/12345”,
                    “id”: "12345"
                },
                {
                    “href”: “https://data.oculusvr.com/v1/oauth2/clients/23456”,
                    “id”: "23456"
                }
            ],
            "isConsumable": true,
            "type": "ONLINE_ACCESS",
            "item": {
                "href": "https://data.oculusvr.com/v1/items/23456", 
                "id": "23456"
            },
            "name": "ONLINE_ACCESS"
        }


##resource entitlementDefinition list

        {
            "self":{
                "href": "https://data.oculusvr.com/v1/entitlement-definitions?type=ONLINE_ACCESS",
                "id": ""
            },
            "items": 
            [{ 
                "self": {
                    "href": "https://data.oculusvr.com/v1/entitlement-definitions/123",
                    "id": “123”
                },
                "rev": "k930dkd03kdk3k3k5", 
                "developer": {
                    href": "https://data.oculusvr.com/v1/users/12345",
                    id": “12345”
                },
                "allowedClients": [
                    {
                        “href”: “https://data.oculusvr.com/v1/oauth2/clients/12345”,
                        “id”: "12345"
                    },
                    {
                        “href”: “https://data.oculusvr.com/v1/oauth2/clients/23456”,
                        “id”: "23456"
                    }
                ],
                "isConsumable": true,
                "type": "ONLINE_ACCESS",
                "item": {
                    "href": "https://data.oculusvr.com/v1/items/23456", 
                    "id": "23456"
                },
                "name": "ONLINE_ACCESS"
            },
            { 
                "self": {
                    "href": "https://data.oculusvr.com/v1/entitlement-definitions/123",
                    "id": "124"
                },
                "rev": "k930dkd03kdk3k3k5", 
                "developer": {
                    href": "https://data.oculusvr.com/v1/users/12345",
                    id": “12345”
                },
                "allowedClients": [
                    {
                        “href”: “https://data.oculusvr.com/v1/oauth2/clients/12345”,
                        “id”: "12345"
                    },
                    {
                        “href”: “https://data.oculusvr.com/v1/oauth2/clients/23456”,
                        “id”: "23456"
                    }
                ],
                "isConsumable": true,
                "type": "ONLINE_ACCESS",
                "item": {
                    "href": "https://data.oculusvr.com/v1/items/234564", 
                    "id": "234564"
                },
                "name": "ONLINE_ACCESS"
            }],
            "next": "https://data.oculusvr.com/v1/entitlement-definitions?type=ONLINE_ACCESS&start=50"
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


##GET /entitlements?userId={userId}&isActive={isActive}&isSuspended={isSuspended}&definitionIds={definitionIds}&type={type}
get and filter all entitlements for specified user.

+ Parameters

    + userId(required)
    + isActive(optional)
    + isSuspended(optional)
    + type(optional)
    + definitionIds(optional)...can have more than one
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

+ Response 404 (application/json)

##POST /entitlement-definitions
create a new entitlementDefinition

+ Request (application/json)

    the [entitlementDefinition](#resource-entitlementdefinition) which needs to be created

+ Response 200 (application/json)

    the created [entitlementDefinition](#resource-entitlementdefinition)


##GET /entitlement-definitions?developerId={developerId}&clientId={clientId}&isConsumable={isConsumable}&types={types}&itemId={itemId}&names={names}
get and filter entitlementDefinitions.

+ Parameters

    + developerId(required)
    + clientId(optional)
    + isConsumable(optional)
    + types(optional)...can have more than one
    + itemId(optional)
    + names(optional)...can have more than one

+ Response 200 (application/json)

    the result [entitlementDefinitions](#resource-entitlementdefinition-list)        
