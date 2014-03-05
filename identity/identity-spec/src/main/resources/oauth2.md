FORMAT: 1A
HOST: http://www.wan-san.com

# OAuth2.0
OAuth2.0 API is a collection of APIs that implements the OAuth2.0 specification (`http://tools.ietf.org/html/rfc6749`)

# Group OAuth
OAuth2.0 related APIs.

## Authorization endpoint [/auth{?client_id,redirect_uri,response_type,scope}]

+ Parameters
    + client_id (required, string) ... The app client ID that the user is requesting
    + response_type (required, string) ... The response type the user wants
        + Values
            + `code`
            + `token`
    + redirect_uri (optional, string) ... The redirect uri the user wants along with the authorization code
    + scope (optional, string) ... The scope the user wants. If not provided, the app client must have a default scope defined

### Get an authorization code [GET]
+ Request

        GET http://oauth1.apiary.io/auth?client_id=client1&redirect_uri=http://localhost&response_type=code

+ Response 302

    + Header

            Location: http://localhost?code=aslkTImmmytlaUUoq


### Get an authorization code [POST]
Does the exactly same thing as the GET method, unless the query parameters **must** be posted in "application/x-www-form-urlencoded" format

+ Request

        GET http://oauth1.apiary.io/auth?client_id=client1&redirect_uri=http://localhost&response_type=code

+ Response 302

    + Header

            Location: http://localhost?code=aslkTImmmytlaUUoq

## Token endpoint [/token{?client_id,client_secret,grant_type,redirect_uri,code,username,password,scope,refresh_token}]
### Get an access token [POST]
All the query parameters **must** be posted in "application/x-www-form-urlencoded" format
+ Parameters
    + client_id (required, string) ... The app client ID that the user is requesting
    + client_secret (required, string) ... The app client secret of the app client
    + grant_type (required, string) ... The grant type of this request
        + Values
            + `authorization_code`
            + `password`
            + `refresh_token`
            + `client_credentials`
    + redirect_uri (optional, string) ... Used in `authorization_code` flow, it must be identical to the authorization code's `redirect_uri`
    + code (optional, string) ... Used in `authorization_code` flow, stands for the authorization code
    + username (optional, string) ... Used in `password` flow, stands for the credential's username
    + password (optional, string) ... Used in `password` flow, stands for the credential's password
    + scope (optional, string) ... The scope the user wants. If not provided, the app client must have a default scope defined
    + refresh_token (optional, string) ... Used in `refresh_token` flow, stands for the refresh token

+ Request

        POST http://oauth1.apiary.io/token?client_id=client1&client_secret=secret1&redirect_uri=http://localhost&grant_type=authorization_code&code=aslkTImmmytlaUUoq

+ Response 200 (application/json)

        {
            "access_token":"2YotnFZFEjr1zCsicMWpAA",
            "token_type":"Bearer",
            "expires_in":3600,
            "refresh_token":"tGzv3JOkF0XG5Qx2TlKWIA"
        }

