FORMAT: 1A
HOST: http://www.wan-san.com

# Identity User Authentication
Identity User Authentication API is a collection of APIs that provides access to user's authentication related information, such as user's password, security questions, external authentication reference and so on.

# Group User Credentials

## Authenticate user with User Credentials [/userCredentials{?username,password}]

### Authenticate a user with User Credentials [GET]

+ Parameters
    + username (required, string) ... The username of the authenticating user
    + password (required, string) ... The password of the authenticating user

+ Request

        GET http://www.wan-san.com/userCredentials?username=u1&password=Welcome123

+ Response 200 (application/json)

        {
            "userId": "1231234412455",
            "username": "u1",
            "email": "u1@wan-san.com",
            "status": "ACTIVE",
            "dateCreated": "2013-01-01"
        }

## User Credential management [/userCredentials/{userId}]

### Create a user credential [POST]

+ Parameters
    + userId (required, long) ... The user id of this user credential

+ Request (application/json)

        {
            "username": "u1",
            "password": "Welcome123",
            "status": "ACTIVE"
        }

+ Response 201

        {
            "userCredentialsUri": "/userCredentials/{userId}"
        }

### Change a user credential [PATCH]

+ Parameters
    + userId (required, long) ... The user id of this user credential

+ Request (application/json)

        {
            "oldPassword": "Welcome123",
            "password": "Welcome321",
            "status": "ACTIVE"
        }

+ Response 200

        {
            "userCredentialsUri": "/userCredentials/{userId}"
        }

# Group User Security Questions

## User Security Questions Management [/users/{userId}/securityQuestions]

+ Parameters
    + userId (required, long) ... The user id of this user's security questions

### Retrieve User Security Questions [GET]

+ Response 200

        {
            "userId": "1231234412455",
            "securityQuestion": "What's your name?",
            "securityAnswer": "Stan Marsh"
        }

### Create User Security Questions [POST]

+ Request (application/json)

        {
            "securityQuestion": "What's your name?",
            "securityAnswer": "Stan Marsh"
        }

+ Response 201 (application/json)

        {
            "userSecurityQuestionUri": "/users/1231234412455/securityQuestions"
        }

### Change User Security Questions [PUT]

+ Request (application/json)

        {
            "securityQuestion": "How old are you?",
            "securityAnswer": "27"
        }

+ Response 200 (application/json)

        {
            "userSecurityQuestionUri": "/users/1231234412455/securityQuestions"
        }

# Group User External Referencess

## Retrive or Create User External References [/users/{userId}/externalReferences]

+ Parameters
    + userId (required, long) ... The user id of this user's external reference

### Retrive User External References [GET]

+ Headers

          X-Expand-Results: false/true

+ Response 200 (application/json)

        {
            externalReferences: [
                {
                    "userId": "1231234412455",
                    "externalReferenceId": "231231441515",
                    "externalReferenceType": "Facebook",
                    "externalReferenceValue": "12312415534343242356",
                    "status": "ACTIVE"
                    "dateCreated": "2013-09-01"
                },
                {
                    "userId": "1231234412455",
                    "externalReferenceId": "213142515234",
                    "externalReferenceType": "Twitter",
                    "externalReferenceValue": "asdkljlkajlkq6asdfasdf",
                    "status": "ACTIVE"
                    "dateCreated": "2013-09-01"
                }
            ]
        }

### Create a User External Reference [POST]

+ Request (application/json)

        {
            "externalReferenceType": "Facebook",
            "externalReferenceValue": "12312415534343242356",
            "status": "ACTIVE"
        }

+ Response 201 (application/json)

        {
            "userExternalReferencesUri": "/users/1231234412455/externalReferences/213142515234"
        }

## Get or change a specific User External Reference [/users/{userId}/externalReferences/{externalReferenceId}]
+ Parameters
    + userId (required, long) ... The user id of this user's external reference
    + externalReferenceId (required, long) ... The external reference id

### Get a specific User External Reference [GET]

+ Response 200 (application/json)

        {
            "userId": "1231234412455",
            "externalReferenceId": "213142515234",
            "externalReferenceType": "Twitter",
            "externalReferenceValue": "asdkljlkajlkq6asdfasdf",
            "status": "ACTIVE"
            "dateCreated": "2013-09-01"
        }

### Change a specific User External Reference [PUT]

+ Request (application/json)

        {
            "externalReferenceType": "Facebook",
            "externalReferenceValue": "12312415534343242356",
            "status": "ACTIVE"
        }

+ Response 200 (application/json)

        {
            "userExternalReferencesUri": "/users/1231234412455/externalReferences/213142515234"
        }

### Parially change a specific User External Reference [PATCH]

+ Request (application/json)

        {
            "externalReferenceValue": "123124155",
            "status": "ACTIVE"
        }

+ Response 200 (application/json)

        {
            "userExternalReferencesUri": "/users/1231234412455/externalReferences/213142515234"
        }