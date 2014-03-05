#identity app
API about identity appId. appId is always needed in header.

## /identity/apps/{app_id}
###GET
get apps by app Id

+ Response 200 (application/json)

        {
            "appId": 123,
            "ownerId": 456,
            "name":"test Identity app name"
            "group": "Football",
            "redirectUris": "https://www.testSite.com",
            "defaultRedirectUri": "https://www.testSite.com",
            "logoutUris": "https://www.testSite.com/logout"
        }

###PUT
update an existing app

+ Request (application/json)

        {
            "name": "Update your name"
        }

+ Response 200 (application/json)

        {
            "appUri":  "/identity/apps/123"
        }

###DELETE
delete an existing app

+ Response 204

+ Response 500 (application/json)

        {
            "status": "failed"
            "errorMsg": "INVALID_APP_ID "
        }

##POST /identity/apps
create a new app

+ Request (application/json)

        {
            "ownerId": 456,
            "name":"test Identity app name"
            "group": "Football",
            "redirectUris": "https://www.testSite.com",
            "defaultRedirectUri": "https://www.testSite.com",
            "logoutUris": "https://www.testSite.com/logout"
        }

+ Response 200 (application/json)

        {
            "appUri":  "/identity/apps/123"
        }