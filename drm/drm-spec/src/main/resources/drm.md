FORMAT: 1A
HOST: http://www.wan-san.com

# DRM API
DRM API is a collection of APIs to control the access of digital contents

# Group Licenses
The DRM licenses API will generate a DRM License for a specific user

## Licenses Endpoint [/licenses]

### Create a DRM license for a specific user, machine and device [POST]

+ Request (application/json)

        POST /licenses
        {
            "user": {
                "href": "http://api.wan-san.com/v1/user/12312345",
                "id": "12312345"
            },
            "applicationId": "ShootingGame1",
            "machineHash": "asdfaskjlkajt",
            "deviceId": "deviceId1"
        }

+ Response 201 (application/json)

        {
            "self": {
                "href": "http://api.wan-san.com/v1/licenses/4451561",
                "id": "4451461"
            },
            "user": {
                "href": "http://api.wan-san.com/v1/user/12312345",
            },
            "applicationId": "ShootingGame1",
            "machineHash": "askljdflkajttqput",
            "deviceId": "deviceId1",
            "createdTime": "2014-02-12T13:49:32.000Z",
            "expiredTime": "2014-03-12T13:49:32.000Z"
        }


