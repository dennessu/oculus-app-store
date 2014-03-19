module.exports = {

    Identity: {
        /* [POST]  Authenticate ----------------------------------------------------------- */
        PostAuthenticate: {
            Method: 'POST',
            Path: '/rest/Authenticate',
            ResponseItem: 'SucceedNormal',
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
    }

};