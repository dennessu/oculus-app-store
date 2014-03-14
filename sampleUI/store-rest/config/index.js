module.exports = {

    Identity: {
        /* [POST]  Authenticate ----------------------------------------------------------- */
        PostAuthenticate: {
            Method: 'POST',
            Path: '/rest/Authenticate',
            ResponseItem: 'Succeed',
            Items: {

                'Succeed': {
                    statusCode: 302,
                    headers: {
                        location: 'http://localhost:3000/callback/login'
                    },
                    data: ""
                },

                'SucceedNormal': {
                    statusCode: 200,
                    headers: null,
                    // id_token: {"userId":"728578584957952168", "other": "tom@ws.com"}
                    data: {"access_token": "access-token", "id_token": "eyJ1c2VySWQiOiI3Mjg1Nzg1ODQ5NTc5NTIxNjgiLCAib3RoZXIiOiAidG9tQHdzLmNvbSJ9"}
                },

                'Failed': {
                    statusCode: 404,
                    headers: null,
                    data: null
                }
            }
        },

        /* [POST]  Users -------------------------------------------------------------- */
        PostUsers: {
            Method: 'POST',
            Path: '/rest/Users',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data: {"createdTime": "2014-02-27T09:15:26Z", "resourceAge": 0, "userName": "tom@wan-san.com", "status": "ACTIVE", "self": {"href": "https://xxx.xxx.xxx", "id": "728578584957952168"}}
                },

                'Failed': {
                    statusCode: 404,
                    headers: null,
                    data: ""
                }
            }
        },

        /* [Get]  Users -------------------------------------------------------------- */
        GetUsers: {
            Method: 'GET',
            Path: '/rest/Users/:userId',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data: {"createdTime": "2014-02-27T09:15:26Z", "resourceAge": 0, "userName": "tom@wan-san.com", "status": "ACTIVE", "self": {"href": "https://xxx.xxx.xxx", "id": "728578584957952168"}}
                },

                'Failed': {
                    statusCode: 404,
                    headers: null,
                    data: null
                }
            }
        },

        /* [POST]  Profiles ----------------------------------------------------------- */
        PostProfiles: {
            Method: 'POST',
            Path: '/rest/Users/:userId/Profiles',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data: {"createdTime": "2014-02-27T09:15:26Z", "resourceAge": 0, "type": "PAYIN", "region": "en_US", "firstName": "tom", "middleName": "", "lastName": "tom", "dateOfBirth": "2012-03-02T16:00:00Z", "locale": "en_US", "self": {"href": "https://xxx.xxx.xxx", "id": "728578584957954168"}, "user": {"href": "https://xxx.xxx.xxx", "id": "728578584957952168"}}
                },

                'Failed': {
                    statusCode: 404,
                    headers: null,
                    data: ""
                }
            }
        }
    }

};