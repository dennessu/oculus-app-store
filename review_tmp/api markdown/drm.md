FORMAT: 1A
HOST: http://www.wan-san.com

# DRM API
DRM API is a collection of APIs to control the access of digital contents

# Group Licenses
The DRM licenses API will generate a DRM License for a specific user

## Licenses Endpoint [/licenses{?userId,applicationId,machineHash,deviceId}]

+ Parameters
    + userId (required, string) ... The user id that requesting the DRM license
    + applicationId (required, string) ... The application id that requesting the DRM license
    + machineHash (required, string) ... The machine hash that requesting the DRM license
    + deviceId (required, string) ... The device id that requesting the DRM license

### Create a DRM license for a specific user, machine and device [POST]

+ Request (application/x-www-form-urlencoded)

        POST /licenses?userId=1231245&applicationId=ShootingGame1&machineHash=askljdflkajttqput&deviceId=deviceId1

+ Response 201 (application/json)

        {
            "userId": "1231245",
            "applicationId": "ShootingGame1",
            "machineHash": "askljdflkajttqput",
            "deviceId": "deviceId1",
            "expiredBy": "2015-01-01"
        }


