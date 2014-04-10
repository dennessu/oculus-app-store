FORMAT: 1A
HOST: http://www.wan-san.com

# User API
User API defines a set of APIs to operate on users and their directly related resources.
TODO: upate all datetime format to fit the standard.

| Resource Name     | Description
|-------------------|-------------
| users             | The core properties of the user. All users, even anonymous users, will have a corresponding user resource.
| profiles          | The personal data of the user. One user can have one user profile per user profile type.
| device-profiles   | The device and related information of the devices owned by the user. One user can have multiple device profiles.
| tos-acceptances   | The term of service (TOS) accepted by the user. 
| opt-ins           | The opts turned on or off by the user. This is a quick property bag used to track such options attached on the user. 
| federations       | The external authentication keys ofr the federated sign-in.
| devices           | The devices which are referenced by the users in the system.

## Group Users
The API operating on the core user data. 

###### User Fields

| Field Name  | Field Type | Metadata     | Description 
|-------------|------------|--------------|-------------
| id         | string     | out          | The id of the user. <br/> The id is a 64-bit positive integer (so actually 63 bits). The id is a string instead of a number because the number range of javascript is 53 bits.
| userName    | string     | in, out      | The user name of the user. <br/> The user name is an email address. It must be unique in the system.
| password    | string     | in           | The password of the user. <br/> The password will not appear in GET and is not allowed to update in PUT.
| status      | enum       | out          | The status of the user. <br/> The status of a new user is always 'ACTIVE'.
| createdTime | datetime   | out          | The date time when the user is created. <br/> The value is auto-generated.
| updatedTime | datetime   | out          | The date time when the user is updated. <br/> The value is auto-generated.

TODO: where do we track whether the email address is validated for the user? Or do we want the user name to be an email address? Or just some unique nick name?

###### Enums

+ User Status
    - `ACTIVE`
    - `SUSPENDED`
    - `BANNED`

###### Example
<a name="GetUserResponse"></a>The full representation of the users resource in GET is:
    
            {
                "self": {
                    "href": "https://oculusvr.com/apis/v1/users/1234567891234000",
                    "id": "1234567891234000",
                },
                "userName": "abc777@abc.com",
                "status": "ACTIVE",
                "profile": {
                    "href": "https://oculusvr.com/apis/v1/users/123456789123400/profiles",
                    "id": "" // TBD
                },
                "orders": {
                    "href": "https://oculusvr.com/apis/v1/orders?user=123456789123400",
                    "id": ""
                },
                // other links...
                "createdTime": "2014-02-01T02:00:03.123Z",
                "updatedTime": "2014-02-04T03:00:04.123Z"
            }

### POST /users
Create a new user. 
The response will be [the full representation of the users resource in GET.](#GetUserResponse)

+ Request (application/json)

    + Body

            {
                "userName": "abc777@abc.com",
                "password": "Welcome123",
                "status": "ACTIVE"
            }

            
+ Response 200

            The full representation of the user resource in GET.
    
### GET /users?userName={userName}&userNamePrefix={userNamePrefix}
Find user by userName. Note that we only provide finding user by user name for now. User iteration is not used in the scenarios we are considering now and has potential security issues.
The users query supports maxinum 20 items per page. Use the URI in "next" to get additional content. Note: in case of insertion happening during traversal, the iteration can skip some elements. This is usually not a big issue as this API is mainly used in find friends.
The response will be a collection of [the full representation of the users resource in GET.](#GetUserResponse)
(TODO: do we need a better API for find friends and/or CSR search?)

+ Parameters

    + userName (optional, string) ... The target user name. This is an exact match.
    + userNamePrefix (optional, string) ... The prefix of the target user name. 
        For example, if 'abc' is specified as the prefix, all users starting with 'abc' will be listed.

+ Response 200

    + Body

            {
                "href": "/users?userNamePrefix=abc",
                "items: [
                    {
                        "self": {
                            "href": "https://oculusvr.com/apis/v1/users/1234567891234000",
                            "id": "1234567891234000",
                        },
                        "userName": "abc777@abc.com",
                        "status": "ACTIVE",
                        "profile": {
                            "href": "https://oculusvr.com/apis/v1/users/123456789123400/profiles",
                            "id": "" // TBD
                        },
                        "orders": {
                            "href": "https://oculusvr.com/apis/v1/orders?user=123456789123400",
                            "id": ""
                        },
                        // other links...
                        "createdTime": "2014-02-01T02:00:03.123Z",
                        "updatedTime": "2014-02-04T03:00:04.123Z"
                    },
                    {
                        "self": {
                            "href": "https://oculusvr.com/apis/v1/users/1234567891234000",
                            "id": "1234567891234000",
                        },
                        "userName": "abc778@abc.com",
                        "status": "ACTIVE",
                        "profile": {
                            "href": "https://oculusvr.com/apis/v1/users/123456789123400/profiles",
                            "id": "" // TBD
                        },
                        "orders": {
                            "href": "https://oculusvr.com/apis/v1/orders?user=123456789123400",
                            "id": ""
                        },
                        // other links...
                        "createdTime": "2014-02-01T02:00:03.123Z",
                        "updatedTime": "2014-02-04T03:00:04.123Z"
                    },
                    // ...
                ],
                "next": "/users?userNamePrefix=abc&start=20&count=20"
            }
    

### /users/{id}

#### GET

Return a user information given by id (eg: userId, note: userName is unique but it isn't the id to query).
The response will be [the full representation of the users resource in GET.](#GetUserResponse)

+ URI Parameters

    | Name | Type     | Metadata | Description
    |------|----------|----------|-------------
    | id  | `string` | required | The target user id.

+ Response 200

            The full representation of the users resource in GET.
    
#### PUT
Update an existing user identified by the unique id. It must be full update.
The response will be [the full representation of the users resource in GET.](#GetUserResponse)

Only username and status can be updated. 
TODO: do we want to allow updating userName? That sounds like a flow, if the username is an email address. We need to have it re-validated.

+ URI Parameters

    | Name | Type     | Metadata | Description
    |------|----------|----------|-------------
    | id  | `string` | required | The target user id.

+ Request (application/json)

    + Body

            {
                "userName": "abc777@abc.com",
                "status": "ACTIVE"
            }
            
+ Response 200

            The full representation of the users resource in GET.


### POST /users/{id}/update-password?oldPassword={oldPassword}&newPassword={newPassword}
Update an existing user's password.
TODO: how to fit it to REST API principals?
TODO: how about reset password? That should be a flow API. Shall we be layering such UI flow APIs above core APIs?

+ URI Parameters

    | Name | Type     | Metadata | Description
    |------|----------|----------|-------------
    | id  | `string` | required | The target user id.

+ Query Parameters

    | Name         | Type     | Metadata | Description
    |--------------|----------|----------|-------------
    | oldPassword  | `string` | required | The old password of the user.
    | newPassword  | `string` | required | The new password of the user.

+ Response 200

### POST /users/{id}/reset-password
Kick off the reset password flow. The response will be a set of state and the flow can be driven by the events post back to this same flow URI.
TODO: to define flow APIs.

+ Response 200

## Group User Profile
The API operating on the user profiles. 
We model the core user resource to be extremely lightweight. That is just an identity of the user.
However in order to be a part of different subsystem, additional user information will be necessary.
A user can be created without a user profile sometimes. For example, anonymous users from mobile phone. When a user profile is created, we want to make sure it is a complete one.
A user can have multiple user profiles with different profile types. This is an extension point reserved for future scenarios. 

TODO: user groups

###### User Profile Fields

| Field Name  | Field Type | Metadata     | Description 
|-------------|------------|--------------|-------------
| id         | string     | out          | The id of the user profile. <br/> The id is a 64-bit positive integer (so actually 63 bits). The id is a string instead of a number because the number range of javascript is 53 bits.
| user        | string     | in, out      | The user referenced by the user profile.
| type        | string     | in, out      | The user profile type.
| region      | string     | in, out      | The user's region. The 2 character ISO 3166 country code.
| locale      | string     | in, out      | The user's locale. Consists of 2 character ISO 639 language code and 2 character ISO 3166 country code.
| dob         | datetime   | in, out      | The user's date of birth.
| firstName   | string     | in, out      | The user's first name.
| middleName  | string     | in, out      | The user's middle name.
| lastName    | string     | in, out      | The user's last name.
| createdTime | datetime   | out          | The date time when the user profile is created. <br/> The value is auto-generated.
| updatedTime | datetime   | out          | The date time when the user profile is updated. <br/> The value is auto-generated.

###### Example
<a name="GetUserProfileResponse"></a>The full representation of the user profiles resource in GET is:

            {
                "self": {
                    "href": "https://apis.oculusvr.com/v1/users/1234567891234000/profile/9876655436364000",
                    "id": "9876655436364000"
                },
                "user": {
                    "href": "https://oculusvr.com/apis/v1/users/1234567891234000",
                    "id": "1234567891234000"
                },
                "region": "US",
                "locale": "en_US",
                "dob": "1988-01-01",
                "firstName": "Kevin",
                "middleName": "",
                "lastName": "Goo",
                "createdTime": "2014-02-01T02:00:03.123Z",
                "updatedTime": "2014-02-01T02:00:03.123Z",
            }



### POST /users/{id}/profiles
Create a user profile.
The response will be [the full representation of the user profiles resource in GET.](#GetUserProfileResponse)

+ URI Parameters

    | Name | Type     | Metadata | Description
    |------|----------|----------|-------------
    | id  | `string` | required | The target user id.

+ Request(application/json)

    + Body

            {
                "user": {
                    "href": "https://oculusvr.com/apis/v1/users/1234567891234000",
                    "id": "1234567891234000"
                },
                "type": "CUSTOMER",
                "region": "US",
                "locale": "en_US",
                "dob": "1988-02-01T02:00:03.123Z",
                "firstName": "Kevin",
                "middleName": "",
                "lastName": "Goo",
                "companyName": "",
            }
            
+ Response 200

            The full representation of the user profile resource in GET.
    
### GET /users/{id}/profiles?type={type}
Get all user profiles of the user.
The response will be a collection of [the full representation of the user profiles resource in GET.](#GetUserProfileResponse)

+ URI Parameters

    | Name | Type     | Metadata | Description
    |------|----------|----------|-------------
    | id  | `string` | required | The target user id.

+ Query Parameters

    | Name | Type     | Metadata | Description
    |------|----------|----------|-------------
    | type | `string` | optional | The target user profile type. 

+ Response 200

            {
                "self": {
                    "href": "https://apis.oculusvr.com/v1/users/1234567891234000/profiles",
                    "id": ""
                }
                "items": [
                    {
                        "self": {
                            "href": "https://apis.oculusvr.com/v1/users/1234567891234000/profiles/9876655436364000",
                            "id": "9876655436364000"
                        },
                        "user": {
                            "href": "https://oculusvr.com/apis/v1/users/1234567891234000",
                            "id": "1234567891234000"
                        },
                        "type": "CUSTOMER",
                        "region": "US",
                        "locale": "en_US",
                        "dob": "1988-02-01T02:00:03.123Z",
                        "firstName": "Kevin",
                        "middleName": "",
                        "lastName": "Goo",
                        "companyName": "",
                        "createdTime": "2014-02-01T02:00:03.123Z",
                        "updatedTime": "2014-02-01T02:00:03.123Z",
                    },
                    {
                        "self": {
                            "href": "https://apis.oculusvr.com/v1/users/1234567891234000/profiles/98766554363641000",
                            "id": "98766554363641000"
                        },
                        "user": {
                            "href": "https://oculusvr.com/apis/v1/users/1234567891234000",
                            "id": "1234567891234000"
                        },
                        "type": "CUSTOMER",
                        "region": "US",
                        "locale": "en_US",
                        "dob": "1988-02-01T02:00:03.123Z",
                        "firstName": "Kevin",
                        "middleName": "",
                        "lastName": "Goo",
                        "companyName": "",
                        "createdTime": "2014-02-01T02:00:03.123Z",
                        "updatedTime": "2014-02-01T02:00:03.123Z",
                    },
                    // ...
                ]
            }


### /users/{key1}/profiles/{key2}
#### GET
Get a specific user profile.

+ URI Parameters

    | Name | Type     | Metadata | Description
    |------|----------|----------|-------------
    | key1 | `string` | required | The target user id.
    | key2 | `string` | required | The target user profile id.

+ Response 200

            The full representation of the user profile resource in GET.

#### PUT
Update a specific user profile.

+ URI Parameters

    | Name | Type     | Metadata | Description
    |------|----------|----------|-------------
    | key1 | `string` | required | The target user id.
    | key2 | `string` | required | The target user profile id.

+ Request(application/json)

    + Body

            The full representation of the user profile resource in GET without href and output parameters.

+ Response 200

            The full representation of the user profile resource in GET.

#### DELETE
Delete a user profile.

+ Response 200


## Group User Device Profile
The API operating on the user device profiles. The user device profile is about user and device. It contains information on both device and user. For example, when the user last used the device.
One user can have multiple user device profiles. This is not restricted by type.

###### User Device Profile Fields

| Field Name   | Field Type | Metadata     | Description 
|--------------|------------|--------------|-------------
| id          | string     | out          | The id of the user device profile. <br/> The id is a 64-bit positive integer (so actually 63 bits). The id is a string instead of a number because the number range of javascript is 53 bits.
| user         | string     | in, out      | The user referenced by the user device profile.
| type         | string     | in, out      | The user device profile type.
| device       | string     | in, out      | The reference to the device resource.
| lastUsedDate | datetime   | in, out      | The last used date of the device by the user.
| createdTime  | datetime   | out          | The date time when the user device profile is created. <br/> The value is auto-generated.
| updatedTime  | datetime   | out          | The date time when the user device profile is updated. <br/> The value is auto-generated.

This is to be extended.

###### Example
<a name="GetUserDeviceProfileResponse"></a>The full representation of the user device profiles resource in GET is:

            {
                "self": {
                    "href": "https://apis.oculusvr.com/v1/users/1234567891234000/device-profiles/9876655436364000",
                    "id": "9876655436364000"
                },
                "user": {
                    "href": "https://oculusvr.com/apis/v1/users/1234567891234000",
                    "id": "1234567891234000"
                },
                "type": "OCULUS",
                "device" {
                    "href": "https://oculusvr.com/apis/v1/devices/12345693940134000",
                    "id": "12345693940134000"
                },
                "lastUsedDate": "2014-02-01T02:00:03.123Z",
                "createdTime": "2014-02-01T02:00:03.123Z",
                "updatedTime": "2014-02-01T02:00:03.123Z",
            }

### POST /users/{id}/device-profiles
Create a user device profile.
The response will be [the full representation of the user device profiles resource in GET.](#GetUserDeviceProfileResponse)

+ URI Parameters

    | Name | Type     | Metadata | Description
    |------|----------|----------|-------------
    | id  | `string` | required | The target user id.

+ Request(application/json)

    + Body

            {
                "user": {
                    "href": "https://oculusvr.com/apis/v1/users/1234567891234000",
                    "id": "1234567891234000"
                },
                "type": "OCULUS",
                "device" {
                     "href": "https://oculusvr.com/apis/v1/devices/12345693940134000",
                     "id": "12345693940134000"
                 },
            }
            
+ Response 200

            The full representation of the user profile resource in GET.

### GET /users/{id}/device-profiles?type={type}
Get all user profiles of the user.
The response will be a collection of [the full representation of the user device profiles resource in GET.](#GetUserDeviceProfileResponse)

+ URI Parameters

    | Name | Type     | Metadata | Description
    |------|----------|----------|-------------
    | id  | `string` | required | The target user id.

+ Query Parameters

    | Name | Type     | Metadata | Description
    |------|----------|----------|-------------
    | type | `string` | optional | The target user device profile type. 

+ Response 200

             {
                "self": {
                    "href": "https://apis.oculusvr.com/v1/users/1234567891234000/device-profiles",
                    "id": ""
                },
                "items": [
                    {
                        "self": {
                            "href": "https://apis.oculusvr.com/v1/users/1234567891234000/device-profiles/9876655436364000",
                            "id": "9876655436364000"
                        },
                        "user": {
                            "href": "https://oculusvr.com/apis/v1/users/1234567891234000",
                            "id": "1234567891234000"
                        },
                        "type": "OCULUS",
                        "device" {
                            "href": "https://oculusvr.com/apis/v1/devices/12345693940134000",
                            "id": "12345693940134000"
                        },
                        "createdTime": "2014-02-01T02:00:03.123Z",
                        "updatedTime": "2014-02-01T02:00:03.123Z"
                    },
                    {
                        "self": {
                            "href": "https://apis.oculusvr.com/v1/users/1234567891234000/device-profiles/9876655436365000",
                            "id": "9876655436365000"
                        },
                        "user": {
                            "href": "https://oculusvr.com/apis/v1/users/1234567891234000",
                            "id": "1234567891234000"
                        },
                        "type": "OCULUS",
                        "device" {
                            "href": "https://oculusvr.com/apis/v1/devices/12345693940134000",
                            "id": "12345693940134000"
                        },
                        "createdTime": "2014-02-01T02:00:03.123Z",
                        "updatedTime": "2014-02-04"
                    },
                    // ...
                ]
            }


### /users/{key1}/device-profiles/{key2}
#### GET
Get a specific user device profile.

+ URI Parameters

    | Name | Type     | Metadata | Description
    |------|----------|----------|-------------
    | key1 | `string` | required | The target user id.
    | key2 | `string` | required | The target user device profile id.

+ Response 200

            The full representation of the user device profile resource in GET.

#### PUT
Update a specific user device profile.

+ URI Parameters

    | Name | Type     | Metadata | Description
    |------|----------|----------|-------------
    | key1 | `string` | required | The target user id.
    | key2 | `string` | required | The target user device profile id.

+ Request(application/json)

    + Body

            The full representation of the user device profile resource in GET without href and output parameters.

+ Response 200

            The full representation of the user device profile resource in GET.

#### DELETE
Delete a user device profile.
(TODO: is this API necessary? Maybe with restricted access.)

+ Response 200


## Group Tos-Acceptance
The API operating on the user term-of-service acceptance.

###### Tos Acceptance Fields

| Field Name   | Field Type | Metadata     | Description 
|--------------|------------|--------------|-------------
| id          | string     | out          | The id of the tos acceptance. <br/> The id is a 64-bit positive integer (so actually 63 bits). The id is a string instead of a number because the number range of javascript is 53 bits.
| user         | string     | in, out      | The user referenced by the user device profile.
| tos          | string     | in, out      | The tos accepted. The tos id includes the version.
| dateAccepted | datetime   | in, out      | The date when the user accepted the TOS. (TODO: duplicate with createdTime?)
| createdTime  | datetime   | out          | The date time when the tos-acceptance is created. <br/> The value is auto-generated.

We omit the update related fields since this is not updatable.

###### Example
<a name="GetTosAcceptanceResponse"></a>The full representation of the user tos acceptance resource in GET is:

            {
                "self": {
                    "href": "https://apis.oculusvr.com/v1/users/1234567891234000/tos-acceptances/9876655436364000",
                    "id": "9876655436364000"
                },
                "user": {
                    "href": "https://oculusvr.com/apis/v1/users/1234567891234000",
                    "id": "1234567891234000"
                },
                "tos": "/tos/12345693940134000",
                "dateAccepted": "2014-02-01T02:00:03.123Z",
                "createdTime": "2014-02-01T02:00:03.123Z",
            }

### POST /users/{id}/tos-acceptances
Create a user tos acceptance.
The response will be [the full representation of the user tos acceptance resource in GET.](#GetTosAcceptanceResponse)

+ URI Parameters

    | Name | Type     | Metadata | Description
    |------|----------|----------|-------------
    | id  | `string` | required | The target user id.

+ Request (application/json)

    + Body

            {
                "user": {
                    "href": "https://oculusvr.com/apis/v1/users/1234567891234000",
                    "id": "1234567891234000"
                },
                "tos": "/tos/12345693940134000",
                "dateAccepted": "2014-02-01T02:00:03.123Z",
            }
            
+ Response 200

            The full representation of the tos acceptance resource in GET.

### GET /users/{id}/tos-acceptances?tos={tos}
Get tos acceptance of the user.
The response will be a collection of [the full representation of the user tos acceptance resource in GET.](#GetTosAcceptanceResponse)

+ URI Parameters

    | Name | Type     | Metadata | Description
    |------|----------|----------|-------------
    | id  | `string` | required | The target user id.

+ Query Parameters

    | Name | Type     | Metadata | Description
    |------|----------|----------|-------------
    | tos  | `string` | optional | The target user tos type. 

+ Response 200

            {
                "self": {
                    "href": "https://apis.oculusvr.com/v1/users/1234567891234000/tos-acceptances",
                    "id": ""
                },
                "items: [
                    {
                        "self": {
                            "href": "https://apis.oculusvr.com/v1/users/1234567891234000/tos-acceptances/9876655436364000",
                            "id": "9876655436364000"
                        },
                        "user": {
                            "href": "https://oculusvr.com/apis/v1/users/1234567891234000",
                            "id": "1234567891234000"
                        },
                        "tos": "/tos/12345693940134000",
                        "createdTime": "2014-02-01T02:00:03.123Z"
                    },
                    {
                        "self": {
                            "href": "https://apis.oculusvr.com/v1/users/1234567891234000/tos-acceptances/9876655436365000",
                            "id": "9876655436365000"
                        },
                        "user": {
                            "href": "https://oculusvr.com/apis/v1/users/1234567891234000",
                            "id": "1234567891234000"
                        },
                        "tos": "/tos/12345693940134000",
                        "createdTime": "2014-02-01T02:00:03.123Z"
                    },
                    // ...
                ]
            }


## Group OptIns
The API operating on the user opt-ins. 

###### OptIns Fields

| Field Name   | Field Type | Metadata     | Description 
|--------------|------------|--------------|-------------
| id          | string     | out          | The id of the opt-ins. <br/> The id is a 64-bit positive integer (so actually 63 bits). The id is a string instead of a number because the number range of javascript is 53 bits.
| user         | string     | in, out      | The user referenced by the opt-ins.
| type         | string     | in, out      | The opt-in type opt-in by user.
| createdTime  | datetime   | out          | The date time when the opt-in is created. <br/> The value is auto-generated.

We omit the update related fields since this is not updatable.

###### Example
<a name="GetOptInsResponse"></a>The full representation of the user opt-ins resource in GET is:

            {
                "self": {
                    "href": "https://apis.oculusvr.com/v1/users/1234567891234000/opt-ins/9876655436364000",
                    "id": "9876655436364000"
                },
                "user": {
                    "href": "https://oculusvr.com/apis/v1/users/1234567891234000",
                    "id": "1234567891234000"
                },
                "type": "OVR-BusinessEmail",
                "createdTime": "2014-02-01T02:00:03.123Z"
            }

TODO: define the types? Or just accept any type? have a convention on the usage like prefixing by client ID?


### POST /users/{id}/opt-ins
Create a user opt-in.
The response will be [the full representation of the user opt-in resource in GET.](#GetOptInsResponse)

+ URI Parameters

    | Name | Type     | Metadata | Description
    |------|----------|----------|-------------
    | id  | `string` | required | The target user id.

+ Request (application/json)

    + Body

            {
                "user": {
                    "href": "https://oculusvr.com/apis/v1/users/1234567891234000",
                    "id": "1234567891234000"
                },
                "tos": "/tos/12345693940134000",
                "dateAccepted": "2014-02-01T02:00:03.123Z",
            }
            
+ Response 200

            The full representation of the tos acceptance resource in GET.

### GET /users/{id}/opt-ins?type={type}
Get opt-ins of the user.
The response will be a collection of [the full representation of the user opt-in resource in GET.](#GetOptInsResponse)

+ URI Parameters

    | Name | Type     | Metadata | Description
    |------|----------|----------|-------------
    | id  | `string` | required | The target user id.

+ Query Parameters

    | Name | Type     | Metadata | Description
    |------|----------|----------|-------------
    | type | `string` | optional | The target user opt-in type. 

+ Response 200

            {
                "self": {
                    "href": "https://apis.oculusvr.com/v1/users/1234567891234000/opt-ins",
                    "id": ""
                },
                "items: [
                    {
                        "self": {
                            "href": "https://apis.oculusvr.com/v1/users/1234567891234000/opt-ins/9876655436364000",
                            "id": "9876655436364000"
                        },
                        "user": {
                            "href": "https://oculusvr.com/apis/v1/users/1234567891234000",
                            "id": "1234567891234000"
                        },
                        "type": "OVR-BusinessEmail",
                        "createdTime": "2014-02-01T02:00:03.123Z"
                    },
                    {
                        "self": {
                            "href": "https://apis.oculusvr.com/v1/users/1234567891234000/opt-ins/9876655436366000",
                            "id": "9876655436366000"
                        },
                        "user": {
                            "href": "https://oculusvr.com/apis/v1/users/1234567891234000",
                            "id": "1234567891234000"
                        },
                        "type": "OVR-BusinessEmail",
                        "createdTime": "2014-02-01T02:00:03.123Z"
                    },
                    // ...
                ]
            }

### DELETE /users/{key1}/opt-ins/{key2}
Delete the opt-in (opt-out).

+ URI Parameters

    | Name | Type     | Metadata | Description
    |------|----------|----------|-------------
    | key1 | `string` | required | The target user id.
    | key2 | `string` | required | The target user opt-in id.

+ Response 200


## Group Federation References
The API operating on the user federated sign-in references. 
Each federation entry contains the information for matching the user using a specific external sign-in type. For example, facebook sign-in, google sign-in, etc.
Now we restrict that each user can only have 1 federatated reference for each type.

TODO: do we want to put a property bag the authentication keys for the user?

###### User Federation References Fields

| Field Name  | Field Type | Metadata     | Description 
|-------------|------------|--------------|-------------
| id         | string     | out          | The id of the user federation. <br/> The id is a 64-bit positive integer (so actually 63 bits). The id is a string instead of a number because the number range of javascript is 53 bits.
| user        | string     | in, out      | The user referenced by the user federation.
| type        | string     | in, out      | The user federation type. For example, facebook. This type determines the meaning of the value.
| value       | string     | in, out      | The user federation value. The value is used to match the user id from external authentication system.
| createdTime | datetime   | out          | The date time when the federation is created. <br/> The value is auto-generated.
| updatedTime | datetime   | out          | The date time when the federation is updated. <br/> The value is auto-generated.

###### Example
<a name="GetUserFederationResponse"></a>The full representation of the user federation resource in GET is:
    
            {
                "self": {
                    "href": "https://apis.oculusvr.com/v1/users/1234567891234000/federations/9876655436364000",
                    "id": "9876655436364000"
                },
                "user": {
                    "href": "https://oculusvr.com/apis/v1/users/1234567891234000",
                    "id": "1234567891234000"
                },
                "type": "facebook",
                "value": "192255450876602",
                "createdTime": "2014-02-01T02:00:03.123Z",
                "updatedTime": "2014-02-01T02:00:03.123Z"
            }

### POST /users/{id}/federations
Create a user federation.
The response will be [the full representation of the user federations resource in GET.](#GetUserFederationResponse)

+ URI Parameters

    | Name | Type     | Metadata | Description
    |------|----------|----------|-------------
    | id  | `string` | required | The target user id.

+ Request(application/json)

    + Body

            {
                "user": {
                    "href": "https://oculusvr.com/apis/v1/users/1234567891234000",
                    "id": "1234567891234000"
                },
                "type": "facebook",
                "value": "192255450876602",
            }

+ Response 200

            The full representation of the user federations resource in GET.
    
### GET /users/{id}/federations?type={type}
Get all user federations of the user.
The response will be a collection of [the full representation of the user federations resource in GET.](#GetUserFederationResponse)

+ URI Parameters

    | Name | Type     | Metadata | Description
    |------|----------|----------|-------------
    | id  | `string` | required | The target user id.

+ Query Parameters

    | Name | Type     | Metadata | Description
    |------|----------|----------|-------------
    | type | `string` | optional | The target user federations type. 

+ Response 200

            {
                "self": {
                    "href": "https://apis.oculusvr.com/v1/users/1234567891234000/federations",
                    "id": ""
                },
                "items: [
                    {
                        "self": {
                            "href": "https://apis.oculusvr.com/v1/users/1234567891234000/federations/9876655436364000",
                            "id": "9876655436364000"
                        },
                        "user": {
                            "href": "https://oculusvr.com/apis/v1/users/1234567891234000",
                            "id": "1234567891234000"
                        },
                        "type": "facebook",
                        "value": "192255450876602",
                        "createdTime": "2014-02-01T02:00:03.123Z",
                        "updatedTime": "2014-02-01T02:00:03.123Z"
                    },
                    {
                        "self": {
                            "href": "https://apis.oculusvr.com/v1/users/1234567891234000/federations/9876655436365000",
                            "id": "9876655436365000"
                        },
                        "user": {
                            "href": "https://oculusvr.com/apis/v1/users/1234567891234000",
                            "id": "1234567891234000"
                        },
                        "type": "google",
                        "value": "8694839223",
                        "createdTime": "2014-02-01T02:00:03.123Z",
                        "updatedTime": "2014-02-01T02:00:03.123Z"
                    },
                    // ...
                ]
            }


### /users/{key1}/federations/{key2}
#### GET
Get a specific user federation.

+ URI Parameters

    | Name | Type     | Metadata | Description
    |------|----------|----------|-------------
    | key1 | `string` | required | The target user id.
    | key2 | `string` | required | The target user federations id.

+ Response 200

            The full representation of the user federations resource in GET.

#### PUT
Update a specific user federations.

+ URI Parameters

    | Name | Type     | Metadata | Description
    |------|----------|----------|-------------
    | key1 | `string` | required | The target user id.
    | key2 | `string` | required | The target user federation id.

+ Request(application/json)

    + Body

            The full representation of the user federations resource in GET without href and output parameters.

+ Response 200

            The full representation of the user federations resource in GET.

#### DELETE
Delete a user federation.

+ Response 200



## Group Misc


### /devices
TODO: incomplete

#### POST
Add a new device.

+ Response 200

#### GET
Return the devices based on search criteria.

+ Response 200

### /devices/{deviceId}

#### GET
Return a device given by deviceId.

+ Response 200

#### PUT
Update an existing device details.

+ Response 200

#### DELETE
Delete a device given by deviceId, deleting a device causes all the underlying entitlements to be disassociated.

+ Response 200


### GET /password-rules
Create password rules.
TODO: the detailed rule schema. We will start with a simple rule here.
TODO: how the description is localized so it can be used for rendering?
TODO: allow a nick-name for passwords and people can update instead of a generated id? It sounds more useful.

+ Request(application/json)

    + Body
    
            {
                "description": "Password is above 8 characters long, contains at least 1 uppercase letter and at least 1 number.",
                "minLength": "8",
                "maxLength": "100",
                "regexes": [
                    "[A-Z]",
                    "[0-9]",
                    "^[A-Z0-9$-/:-?{-~!"^_`\[\]]+$",
                ]
            }

+ Response 200 (application/json)

            {
                "self": {
                    "href": "https://apis.oculusvr.com/v1/users/password-rules/1234567890123193",
                    "id": "1234567890123193"
                },
                "description": "Password is above 8 characters long, contains at least 1 uppercase letter and at least 1 number.",
                "minLength": "8",
                "maxLength": "100",
                "regexes": [
                    "[A-Z]",
                    "[0-9]",
                    "^[A-Z0-9$-/:-?{-~!"^_`\[\]]+$",
                ]
            }

### /password-rules/{id}

#### GET
Get password rule.

+ URI Parameters

    | Name | Type     | Metadata | Description
    |------|----------|----------|-------------
    | id  | `string` | required | The target password rule id.

      
+ Response 200 (application/json)
        
            The full representation of password rules resource in GET.


#### PUT
Update password rule.

+ URI Parameters

    | Name | Type     | Metadata | Description
    |------|----------|----------|-------------
    | id  | `string` | required | The target password rule id.

+ Request (application/json)

    + Body

            The full representation of password rules resource in GET except href.

+ Response 200
        
            The full representation of password rules resource in GET.

### /tos
TODO

#### GET

+ Response 200

