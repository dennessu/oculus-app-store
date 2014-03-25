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
                        location: 'http://localhost:3000/callback/login?code=1234'
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
                    data: "Error"
                }
            }
        },

        PostToken: {
            Method: 'POST',
            Path: '/rest/token',
            ResponseItem: 'Succeed',
            Items: {

                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data: {"access_token": "UGJCWmduajZLWjdMNTdXV0xVUk9qRDcxeHp3R1djY2w7QVQ",
                        "token_type": "Bearer",
                        "expires_in": 3599,
                        "refresh_token": null,
                        "id_token": "eyJhbGciOiJIUzI1NiIsImN0eSI6IkpXVCJ9.eyJpc3MiOiJodHRwOi8vMTAuMC4xLjEzMzo4MDgxL3Jlc3QvIiwic3ViIjoiNzI4OTE3MjEwNDc3NTY4OTM2IiwiYXVkIjoiY2xpZW50IiwiZXhwIjoxMzkzNTg3MjYzLCJpYXQiOjEzOTM1ODM2NjMsImF1dGhfdGltZSI6bnVsbCwibm9uY2UiOiIxMjMiLCJjX2hhc2giOm51bGwsImF0X2hhc2giOiI0X2JkOWN4MmtUVFFUeXFaSExmeFJRIn0.Sa0SMRTrhbs6HMtVTpBGjKe8ctEzNeP6o1eg2S_nhaQ"}
                },

                'Failed': {
                    statusCode: 404,
                    headers: null,
                    data: "Error"
                }
            }
        },

        GetTokenInfo: {
            Method: 'GET',
            Path: '/rest/tokeninfo',
            ResponseItem: 'Succeed',
            Items: {

                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data: {
                        "user_id": "728917210477568936",
                        "expire_in": 3252,
                        "scopes": "identity openid",
                        "client_id": "client"
                    }
                },

                'Failed': {
                    statusCode: 404,
                    headers: null,
                    data: "Error"
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
                    data: {"createdTime": "2014-02-27T09:15:26Z", "resourceAge": 0, "userName": "tom@wan-san.com", "status": "ACTIVE", "self": {"href": "https://xxx.xxx.xxx", "id": "12345"}}
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
        },

        GetProfiles: {
            Method: 'GET',
            Path: '/rest/users/:userId/profiles',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data: {
                        "createdTime": "2014-02-28T07:44:32Z",
                        "resourceAge": 0,
                        "type": "PAYIN",
                        "region": "en_US",
                        "firstName": "tom14",
                        "middleName": "",
                        "lastName": "tom14",
                        "dateOfBirth": "2010-02-24T09:45:27Z",
                        "locale": "en_US",
                        "self": {
                            "href": "https://xxx.xxx.xxx",
                            "id": "728917210477570936"
                        },
                        "user": {
                            "href": "https://xxx.xxx.xxx",
                            "id": "728917210477568936"
                        }
                    }
                },

                'Failed': {
                    statusCode: 404,
                    headers: null,
                    data: "Error"
                }
            }
        }
    },

    Catalog: {
        /* [Get]  Offers -------------------------------------------------------------- */
        GetOffers: {
            Method: 'GET',
            Path: '/rest/offers',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data: {
                        results: [
                            {
                                "revision": 1,
                                "name": "3D Parking 1",
                                "status": "DRAFT",
                                "createdTime": "2014-02-28T09:11:15Z",
                                "createdBy": "system internal",
                                "updatedTime": "2014-02-28T09:11:15Z",
                                "updatedBy": "system internal",
                                "ownerId": {
                                    "href": "http://api.oculusvr.com/v1/User/3234",
                                    "id": "3234"
                                },
                                "priceTier": null,
                                "prices": {
                                    "US": {
                                        "amount": 9.99,
                                        "currency": "USD"
                                    }
                                },
                                "subOffers": [],
                                "items": [
                                    {
                                        "itemId": {
                                            "href": "http://api.oculusvr.com/v1/Item/100000",
                                            "id": "100000"
                                        },
                                        "sku": null,
                                        "quantity": null
                                    }
                                ],
                                "restriction": null,
                                "categories": [
                                    {
                                        "href": "http://api.oculusvr.com/v1/Category/123",
                                        "id": "123"
                                    }
                                ],
                                "events": [
                                    {
                                        "name": "PURCHASE_EVENT",
                                        "actions": [
                                            {
                                                "type": "GRANT_DOWNLOAD_ENTITLEMENT",
                                                "properties": {
                                                    "duration": "3Month",
                                                    "tag": "item001_ANGRY.BIRD_ONLINE_ACCESS",
                                                    "type": "ONLINE_ACCESS",
                                                    "group": "Angry Bird"
                                                }
                                            }
                                        ]
                                    }
                                ],
                                "eligibleCountries": null,
                                "countryProperties": null,
                                "localeProperties": {
                                    "DEFAULT": {
                                        "description": "3D Parking Simulator is a VR driving simulator specialized for parking. Currently you can practice car parking in 5 small courses. You can see stereoscopic rear-view and door mirrors. Both left and right driving seats are supported (switch by 'C' key)."
                                    }
                                },
                                "properties": {
                                    "mainImage": "/images/p1.jpg"
                                },
                                "self": {
                                    "href": "http://api.oculusvr.com/v1/Offer/1393578675743",
                                    "id": 1
                                }
                            },
                            {
                                "revision": 1,
                                "name": "3D Parking 2",
                                "status": "DRAFT",
                                "createdTime": "2014-02-28T09:11:15Z",
                                "createdBy": "system internal",
                                "updatedTime": "2014-02-28T09:11:15Z",
                                "updatedBy": "system internal",
                                "ownerId": {
                                    "href": "http://api.oculusvr.com/v1/User/3234",
                                    "id": "3234"
                                },
                                "priceTier": null,
                                "prices": {
                                    "US": {
                                        "amount": 9.99,
                                        "currency": "USD"
                                    }
                                },
                                "subOffers": [],
                                "items": [
                                    {
                                        "itemId": {
                                            "href": "http://api.oculusvr.com/v1/Item/100000",
                                            "id": "100000"
                                        },
                                        "sku": null,
                                        "quantity": null
                                    }
                                ],
                                "restriction": null,
                                "categories": [
                                    {
                                        "href": "http://api.oculusvr.com/v1/Category/123",
                                        "id": "123"
                                    }
                                ],
                                "events": [
                                    {
                                        "name": "PURCHASE_EVENT",
                                        "actions": [
                                            {
                                                "type": "GRANT_DOWNLOAD_ENTITLEMENT",
                                                "properties": {
                                                    "duration": "3Month",
                                                    "tag": "item001_ANGRY.BIRD_ONLINE_ACCESS",
                                                    "type": "ONLINE_ACCESS",
                                                    "group": "Angry Bird"
                                                }
                                            }
                                        ]
                                    }
                                ],
                                "eligibleCountries": null,
                                "countryProperties": null,
                                "localeProperties": {
                                    "DEFAULT": {
                                        "description": "3D Parking Simulator is a VR driving simulator specialized for parking. Currently you can practice car parking in 5 small courses. You can see stereoscopic rear-view and door mirrors. Both left and right driving seats are supported (switch by 'C' key)."
                                    }
                                },
                                "properties": {
                                    "mainImage": "/images/p2.jpg"
                                },
                                "self": {
                                    "href": "http://api.oculusvr.com/v1/Offer/222222222",
                                    "id": 2
                                }
                            }
                        ]
                    }
                },

                'Failed': {
                    statusCode: 404,
                    headers: null,
                    data: null
                }
            }
        },

        GetOfferById: {
            Method: 'GET',
            Path: '/rest/offers/:offerId',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data: {
                        "revision": 1,
                        "name": "3D Parking 1",
                        "status": "DRAFT",
                        "createdTime": "2014-02-28T09:11:15Z",
                        "createdBy": "system internal",
                        "updatedTime": "2014-02-28T09:11:15Z",
                        "updatedBy": "system internal",
                        "ownerId": {
                            "href": "http://api.oculusvr.com/v1/User/3234",
                            "id": "3234"
                        },
                        "priceTier": null,
                        "prices": {
                            "US": {
                                "amount": 9.99,
                                "currency": "USD"
                            }
                        },
                        "subOffers": [],
                        "items": [
                            {
                                "itemId": {
                                    "href": "http://api.oculusvr.com/v1/Item/100000",
                                    "id": "100000"
                                },
                                "sku": null,
                                "quantity": null
                            }
                        ],
                        "restriction": null,
                        "categories": [
                            {
                                "href": "http://api.oculusvr.com/v1/Category/123",
                                "id": "123"
                            }
                        ],
                        "events": [
                            {
                                "name": "PURCHASE_EVENT",
                                "actions": [
                                    {
                                        "type": "GRANT_DOWNLOAD_ENTITLEMENT",
                                        "properties": {
                                            "duration": "3Month",
                                            "tag": "item001_ANGRY.BIRD_ONLINE_ACCESS",
                                            "type": "ONLINE_ACCESS",
                                            "group": "Angry Bird"
                                        }
                                    }
                                ]
                            }
                        ],
                        "eligibleCountries": null,
                        "countryProperties": null,
                        "localeProperties": {
                            "DEFAULT": {
                                "description": "3D Parking Simulator is a VR driving simulator specialized for parking. Currently you can practice car parking in 5 small courses. You can see stereoscopic rear-view and door mirrors. Both left and right driving seats are supported (switch by 'C' key)."
                            }
                        },
                        "properties": {
                            "mainImage": "/images/p1.jpg"
                        },
                        "self": {
                            "href": "http://api.oculusvr.com/v1/Offer/1393578675743",
                            "id": 1
                        }
                    }
                },

                'Failed': {
                    statusCode: 404,
                    headers: null,
                    data: null
                }
            }
        }
    },

    Cart:{
        GetPrimaryCart: {
            Method: 'GET',
            Path: '/rest/users/:userId/carts/primary',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 302,
                    headers: {
                        location: "http://localhost:8000/rest/users/10000/carts/21232"
                    },
                    data: null
                },

                'Failed': {
                    statusCode: 404,
                    headers: null,
                    data: null
                }
            }
        },

        GetCartById: {
            Method: 'GET',
            Path: '/rest/users/:userId/carts/:cardId',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data: {
                        "self": {
                            "href": "http://api.oculusvr.com/users/10000/carts/21232",
                            "id": "21232"
                        },
                        "user": {
                            "href": "http://api.oculusvr.com/users/10000",
                            "id": "10000"
                        },
                        "resourceAge": 6,
                        "createdTime": "2014-02-20T09:34:58Z",
                        "updatedTime": "2014-02-21T07:34:30Z",
                        "offers": [
                            {
                                "createdTime": "2014-02-21T07:34:30Z",
                                "updatedTime": "2014-02-21T07:34:30Z",
                                "offer": {
                                    "id": 1,
                                    "href": "http://api.oculusvr.com/offers/30011"
                                },
                                "quantity": 2,
                                "selected": false,
                                "self": {
                                    "href": "http://api.oculusvr.com/users/10000/carts/21232/offers/20001",
                                    "id": "20001"
                                }
                            },

                            {
                                "createdTime": "2014-02-21T07:34:30Z",
                                "updatedTime": "2014-02-21T07:34:30Z",
                                "offer": {
                                    "id": 2,
                                    "href": "http://api.oculusvr.com/offers/30011"
                                },
                                "quantity": 2,
                                "selected": true,
                                "self": {
                                    "href": "http://api.oculusvr.com/users/10000/carts/21232/offers/20001",
                                    "id": "20001"
                                }
                            }
                        ],
                        "coupons": []
                    },
                    'Failed': {
                    statusCode: 404,
                    headers: null,
                    data: null
                }
            }
        }
        },

        PutCart: {
            Method: 'PUT',
            Path: '/rest/users/:userId/carts/:cardId',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data: {
                        "self": {
                            "href": "http://api.oculusvr.com/users/10000/carts/21232",
                            "id": "21232"
                        },
                        "user": {
                            "href": "http://api.oculusvr.com/users/10000",
                            "id": "10000"
                        },
                        "resourceAge": 6,
                        "createdTime": "2014-02-20T09:34:58Z",
                        "updatedTime": "2014-02-21T07:34:30Z",
                        "offers": [
                            {
                                "createdTime": "2014-02-21T07:34:30Z",
                                "updatedTime": "2014-02-21T07:34:30Z",
                                "offer": {
                                    "id": 1,
                                    "href": "http://api.oculusvr.com/offers/30011"
                                },
                                "quantity": 2,
                                "selected": true,
                                "self": {
                                    "href": "http://api.oculusvr.com/users/10000/carts/21232/offers/20001",
                                    "id": "20001"
                                }
                            },

                            {
                                "createdTime": "2014-02-21T07:34:30Z",
                                "updatedTime": "2014-02-21T07:34:30Z",
                                "offer": {
                                    "id": 2,
                                    "href": "http://api.oculusvr.com/offers/30011"
                                },
                                "quantity": 2,
                                "selected": true,
                                "self": {
                                    "href": "http://api.oculusvr.com/users/10000/carts/21232/offers/20001",
                                    "id": "20001"
                                }
                            }
                        ],
                        "coupons": []
                    },
                    'Failed': {
                        statusCode: 404,
                        headers: null,
                        data: null
                    }
                }
            }
        },

        PostCartMerge: {
            Method: 'POST',
            Path: '/rest/users/:userId/carts/:cardId/merge',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data: {
                        "self": {
                            "href": "http://api.oculusvr.com/users/10000/carts/21232",
                            "id": "21232"
                        },
                        "user": {
                            "href": "http://api.oculusvr.com/users/10000",
                            "id": "10000"
                        },
                        "resourceAge": 6,
                        "createdTime": "2014-02-20T09:34:58Z",
                        "updatedTime": "2014-02-21T07:34:30Z",
                        "offers": [
                            {
                                "createdTime": "2014-02-21T07:34:30Z",
                                "updatedTime": "2014-02-21T07:34:30Z",
                                "offer": {
                                    "id": 1,
                                    "href": "http://api.oculusvr.com/offers/30011"
                                },
                                "quantity": 2,
                                "selected": true,
                                "self": {
                                    "href": "http://api.oculusvr.com/users/10000/carts/21232/offers/20001",
                                    "id": "20001"
                                }
                            },

                            {
                                "createdTime": "2014-02-21T07:34:30Z",
                                "updatedTime": "2014-02-21T07:34:30Z",
                                "offer": {
                                    "id": 2,
                                    "href": "http://api.oculusvr.com/offers/30011"
                                },
                                "quantity": 2,
                                "selected": true,
                                "self": {
                                    "href": "http://api.oculusvr.com/users/10000/carts/21232/offers/20001",
                                    "id": "20001"
                                }
                            }
                        ],
                        "coupons": []
                    },
                    'Failed': {
                        statusCode: 404,
                        headers: null,
                        data: null
                    }
                }
            }
        }
    },

    Billing:{
        GetShippingInfo: {
            Method: 'GET',
            Path: '/rest/users/:userId/ship-to-info',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data: [
                        {
                            "self": {
                                "href": "http://api.wan-san.com/ship-to-info/70953532335535",
                                "id": "11"
                            },
                            "user": {
                                "href": "http://api.wan-san.com/users/12345",
                                "id": "12345"
                            },
                            "street": "NO. 1001 Twin Dophin Dr",
                            "city": "Redwood City",
                            "state": "CA",
                            "postalCode": "96045",
                            "country": "US",
                            "firstName": "Steve 11",
                            "lastName": "Smith",
                            "phoneNumber": "207-655-2345"
                        },

                        {
                            "self": {
                                "href": "http://api.wan-san.com/ship-to-info/70953532335535",
                                "id": "22"
                            },
                            "user": {
                                "href": "http://api.wan-san.com/users/12345",
                                "id": "12345"
                            },
                            "street": "NO. 1002 Twin Dophin Dr",
                            "city": "Redwood City",
                            "state": "CA",
                            "postalCode": "96045",
                            "country": "US",
                            "firstName": "Steve 22",
                            "lastName": "Smith",
                            "phoneNumber": "207-655-2345"
                        }
                    ]

                },

                'Failed': {
                    statusCode: 404,
                    headers: null,
                    data: null
                }
            }
        },

        PostShippingInfo: {
            Method: 'POST',
            Path: '/rest/users/:userId/ship-to-info',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data:
                        {
                            "self": {
                                "href": "http://api.wan-san.com/ship-to-info/70953532335535",
                                "id": "11"
                            },
                            "user": {
                                "href": "http://api.wan-san.com/users/12345",
                                "id": "12345"
                            },
                            "street": "NO. 1000 Twin Dophin Dr",
                            "city": "Redwood City",
                            "state": "CA",
                            "postalCode": "96045",
                            "country": "US",
                            "firstName": "Steve 11",
                            "lastName": "Smith",
                            "phoneNumber": "207-655-2345"
                        }

                },

                'Failed': {
                    statusCode: 404,
                    headers: null,
                    data: null
                }
            }
        }
    },

    Order: {
        GetOrders: {
            Method: 'GET',
            Path: '/rest/orders',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data: [
                        {
                            "self" : {
                                "href" : "https://xxx.xxx.xxx",
                                "id" : "111"
                            },
                            "user" : {
                                "href" : "https://xxx.xxx.xxx",
                                "id" : "000100008040"
                            },
                            "trackingUuid" : "3a6f0c0e-2973-403b-96d7-64d29c5419dc",
                            "type" : "PAY_IN",
                            "country" : "US",
                            "currency" : "USD",
                            "tentative" : true,
                            "paymentInstruments" : [ {
                                "href" : "https://xxx.xxx.xxx",
                                "id" : "00000025C000"
                            } ],
                            "orderItems" : [ {
                                "offer" : {
                                    "href" : "https://xxx.xxx.xxx",
                                    "id" : "000002080040"
                                },
                                "quantity" : 2
                            } ]
                        },
                        {
                            "self" : {
                                "href" : "https://xxx.xxx.xxx",
                                "id" : "222"
                            },
                            "user" : {
                                "href" : "https://xxx.xxx.xxx",
                                "id" : "000100008040"
                            },
                            "trackingUuid" : "3a6f0c0e-2973-403b-96d7-64d29c5419dc",
                            "type" : "PAY_IN",
                            "country" : "US",
                            "currency" : "USD",
                            "tentative" : true,
                            "paymentInstruments" : [ {
                                "href" : "https://xxx.xxx.xxx",
                                "id" : "00000025C000"
                            } ],
                            "orderItems" : [ {
                                "offer" : {
                                    "href" : "https://xxx.xxx.xxx",
                                    "id" : "000002080040"
                                },
                                "quantity" : 2
                            } ]
                        }
                    ]
                },

                'Failed': {
                    statusCode: 404,
                    headers: null,
                    data: null
                }
            }
        },

        GetOrdersById: {
            Method: 'GET',
            Path: '/rest/orders/:orderId',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data: {
                        "self" : {
                            "href" : "https://xxx.xxx.xxx",
                            "id" : "111"
                        },
                        "user" : {
                            "href" : "https://xxx.xxx.xxx",
                            "id" : "000100008040"
                        },
                        "trackingUuid" : "3a6f0c0e-2973-403b-96d7-64d29c5419dc",
                        "type" : "PAY_IN",
                        "country" : "US",
                        "currency" : "USD",
                        "tentative" : true,
                        "paymentInstruments" : [ {
                            "href" : "https://xxx.xxx.xxx",
                            "id" : "00000025C000"
                        } ],
                        "orderItems" : [ {
                            "offer" : {
                                "href" : "https://xxx.xxx.xxx",
                                "id" : "000002080040"
                            },
                            "quantity" : 2
                        } ]
                    }
                },

                'Failed': {
                    statusCode: 404,
                    headers: null,
                    data: null
                }
            }
        },

        PostOrders: {
            Method: 'POST',
            Path: '/rest/orders',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data: {
                        "self" : {
                            "href" : "https://xxx.xxx.xxx",
                            "id" : "111"
                        },
                        "user" : {
                            "href" : "https://xxx.xxx.xxx",
                            "id" : "000100008040"
                        },
                        "trackingUuid" : "3a6f0c0e-2973-403b-96d7-64d29c5419dc",
                        "type" : "PAY_IN",
                        "country" : "US",
                        "currency" : "USD",
                        "tentative" : true,
                        "paymentInstruments" : [ {
                            "href" : "https://xxx.xxx.xxx",
                            "id" : "00000025C000"
                        } ],
                        "orderItems" : [ {
                            "offer" : {
                                "href" : "https://xxx.xxx.xxx",
                                "id" : "000002080040"
                            },
                            "quantity" : 2
                        } ]
                    }
                },

                'Failed': {
                    statusCode: 404,
                    headers: null,
                    data: null
                }
            }
        },

        PutOrders: {
            Method: 'PUT',
            Path: '/rest/orders/:orderId',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data: {
                        "self" : {
                            "href" : "https://xxx.xxx.xxx",
                            "id" : "111"
                        },
                        "user" : {
                            "href" : "https://xxx.xxx.xxx",
                            "id" : "000100008040"
                        },
                        "trackingUuid" : "3a6f0c0e-2973-403b-96d7-64d29c5419dc",
                        "type" : "PAY_IN",
                        "country" : "US",
                        "currency" : "USD",
                        "tentative" : true,
                        "paymentInstruments" : [ {
                            "href" : "https://xxx.xxx.xxx",
                            "id" : "00000025C000"
                        } ],
                        "orderItems" : [ {
                            "offer" : {
                                "href" : "https://xxx.xxx.xxx",
                                "id" : "000002080040"
                            },
                            "quantity" : 2
                        } ]
                    }
                },

                'Failed': {
                    statusCode: 404,
                    headers: null,
                    data: null
                }
            }
        }
    },

    PaymentInstruments:{
        GetPaymentInstruments: {
            Method: 'GET',
            Path: '/rest/users/:userId/payment-instruments', //'/rest/users/:userId/payment-instruments/search',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data:{
                        results:[
                            {
                                "self": {
                                    "href": "http://api.wan-san.com/v1/payment-instrument-types/CREDITCARD",
                                    "id": "111"
                                },
                                "accountName": "VISA 1234",
                                "accountNum": "4111111111111111",
                                "isValidated": "false",
                                "isDefault": "true",
                                "type": {
                                    "href": "http://api.wan-san.com/v1/payment-instrument-types/CREDITCARD",
                                    "id": "CREDITCARD"
                                },
                                "creditCardRequest": {
                                    "expireDate": "1999-11-27",
                                    "encryptedCvmCode": "111"
                                },
                                "address": {
                                    "addressLine1": "ThirdStreetFerriday",
                                    "city": "LA",
                                    "state": "CA",
                                    "country": "US",
                                    "postalCode": "12345"
                                },
                                "phone": {
                                    "type": "Home",
                                    "number": "12345678"
                                },
                                "trackingUuid": "f48d6fae-adec-43d0-a865-00a0c91e6bf7"
                            },
                            {
                                "self": {
                                    "href": "http://api.wan-san.com/v1/payment-instrument-types/CREDITCARD",
                                    "id": "222"
                                },
                                "accountName": "MaterCard 1234",
                                "accountNum": "4111111111111111",
                                "isValidated": "false",
                                "isDefault": "true",
                                "type": {
                                    "href": "http://api.wan-san.com/v1/payment-instrument-types/CREDITCARD",
                                    "id": "CREDITCARD"
                                },
                                "creditCardRequest": {
                                    "expireDate": "1999-11-27",
                                    "encryptedCvmCode": "111"
                                },
                                "address": {
                                    "addressLine1": "ThirdStreetFerriday",
                                    "city": "LA",
                                    "state": "CA",
                                    "country": "US",
                                    "postalCode": "12345"
                                },
                                "phone": {
                                    "type": "Home",
                                    "number": "12345678"
                                },
                                "trackingUuid": "f48d6fae-adec-43d0-a865-00a0c91e6bf7"
                            }
                        ]
                    }
                },

                'Failed': {
                    statusCode: 404,
                    headers: null,
                    data: null
                }
            }
        },

        GetPaymentInstrumentsById: {
            Method: 'GET',
            Path: '/rest/users/:userId/payment-instruments/:paymentInstrumentId',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data: {
                        "self": {
                            "href": "http://api.wan-san.com/v1/payment-instrument-types/CREDITCARD",
                            "id": "111"
                        },
                        "accountName": "David",
                        "accountNum": "4111111111111111",
                        "isValidated": "false",
                        "isDefault": "true",
                        "type": {
                            "href": "http://api.wan-san.com/v1/payment-instrument-types/CREDITCARD",
                            "id": "CREDITCARD"
                        },
                        "creditCardRequest": {
                            "expireDate": "1999-11-27",
                            "encryptedCvmCode": "111"
                        },
                        "address": {
                            "addressLine1": "ThirdStreetFerriday",
                            "city": "LA",
                            "state": "CA",
                            "country": "US",
                            "postalCode": "12345"
                        },
                        "phone": {
                            "type": "Home",
                            "number": "12345678"
                        },
                        "trackingUuid": "f48d6fae-adec-43d0-a865-00a0c91e6bf7"
                    }
                },

                'Failed': {
                    statusCode: 404,
                    headers: null,
                    data: null
                }
            }
        },

        PostPaymentInstruments: {
            Method: 'POST',
            Path: '/rest/users/:userId/payment-instruments ',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data: {
                        "self": {
                            "href": "http://api.wan-san.com/v1/payment-instrument-types/CREDITCARD",
                            "id": "111"
                        },
                        "accountName": "David",
                        "accountNum": "4111111111111111",
                        "isValidated": "false",
                        "isDefault": "true",
                        "type": {
                            "href": "http://api.wan-san.com/v1/payment-instrument-types/CREDITCARD",
                            "id": "CREDITCARD"
                        },
                        "creditCardRequest": {
                            "expireDate": "1999-11-27",
                            "encryptedCvmCode": "111"
                        },
                        "address": {
                            "addressLine1": "ThirdStreetFerriday",
                            "city": "LA",
                            "state": "CA",
                            "country": "US",
                            "postalCode": "12345"
                        },
                        "phone": {
                            "type": "Home",
                            "number": "12345678"
                        },
                        "trackingUuid": "f48d6fae-adec-43d0-a865-00a0c91e6bf7"
                    }
                },

                'Failed': {
                    statusCode: 404,
                    headers: null,
                    data: null
                }
            }
        },

        PutPaymentInstruments: {
            Method: 'PUT',
            Path: '/rest/users/:userId/payment-instruments ',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data: {
                        "self": {
                            "href": "http://api.wan-san.com/v1/payment-instrument-types/CREDITCARD",
                            "id": "111"
                        },
                        "accountName": "David",
                        "accountNum": "4111111111111111",
                        "isValidated": "false",
                        "isDefault": "true",
                        "type": {
                            "href": "http://api.wan-san.com/v1/payment-instrument-types/CREDITCARD",
                            "id": "CREDITCARD"
                        },
                        "creditCardRequest": {
                            "expireDate": "1999-11-27",
                            "encryptedCvmCode": "111"
                        },
                        "address": {
                            "addressLine1": "ThirdStreetFerriday",
                            "city": "LA",
                            "state": "CA",
                            "country": "US",
                            "postalCode": "12345"
                        },
                        "phone": {
                            "type": "Home",
                            "number": "12345678"
                        },
                        "trackingUuid": "f48d6fae-adec-43d0-a865-00a0c91e6bf7"
                    }
                },

                'Failed': {
                    statusCode: 404,
                    headers: null,
                    data: null
                }
            }
        },

        DeletePaymentInstrumentsById: {
            Method: 'DELETE',
            Path: '/rest/users/:userId/payment-instruments/:paymentInstrumentId',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data: null
                },

                'Failed': {
                    statusCode: 404,
                    headers: null,
                    data: null
                }
            }
        }
    }

};