module.exports = {

    Identity: {
        /* [POST]  Authenticate ----------------------------------------------------------- */
        PostAuthenticate: {
            Method: 'POST',
            Path: '/v1/oauth2/authorize',
            ResponseItem: 'Succeed',
            Items: {

                'Succeed': {
                    statusCode: 302,
                    headers: {
                        location: 'http://localhost:3100/callback/login?code=1234'
                    },
                    data: ""
                },

                'SucceedCSR': {
                    statusCode: 302,
                    headers: {
                        location: 'http://csr.oculusvr-demo.com:3000/callback/login?code=1234'
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
            Path: '/v1/oauth2/token',
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
            Path: '/v1/oauth2/tokeninfo',
            ResponseItem: 'Succeed',
            Items: {

                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data: {
                        "sub": {
                            id:"728917210477568936",
                            href: "#"
                        },
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
            Path: '/v1/Users',
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

        PutUsers: {
            Method: 'PUT',
            Path: '/v1/users/:userId',
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
            Path: '/v1/Users/:userId',
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
            Path: '/v1/Users/:userId/Profiles',
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
            Path: '/v1/users/:userId/profiles',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data: {
                        results:[
                            {
                                "createdTime": "2014-02-28T07:44:32Z",
                                "resourceAge": 0,
                                "type": "PAYIN",
                                "region": "en_US",
                                "firstName": "tom",
                                "middleName": "",
                                "lastName": "slick",
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
                        ]
                    }
                },

                'Failed': {
                    statusCode: 404,
                    headers: null,
                    data: "Error"
                }
            }
        },

        PutProfiles: {
            Method: 'PUT',
            Path: '/v1/users/:userId/profiles/:profileId',
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
                        "firstName": "tom",
                        "middleName": "",
                        "lastName": "slick",
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
        },

        GetOptIns: {
            Method: 'GET',
            Path: '/v1/users/:userId/opt-ins',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data: {
                        results: [
                            {
                                "user": {
                                    "href": "https://xxx.xxx.xxx",
                                    "id": "728917210477568936"
                                },
                                type: "promotion"
                            },
                            {
                                "user": {
                                    "href": "https://xxx.xxx.xxx",
                                    "id": "728917210477568936"
                                },
                                type: "newsweekly 2"
                            }
                        ]

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
            Path: '/v1/offers',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data: {
                        "self" : {
                            "href" : "http://api.oculusvr-demo.com:8081/v1/offers?status=Released",
                            "id" : ""
                        },
                        "results": [
                            {
                                "createdTime": "2014-05-01T00:00:00Z",
                                "updatedTime": "2014-05-01T00:00:00Z",
                                "rev": "2",
                                "categories": [],
                                "environment": null,
                                "self": {
                                    "href": "http://api.oculusvr-demo.com:8081/v1/offers/2B55FF387CDF",
                                    "id": "2B55FF387CDF"
                                },
                                "currentRevision": {
                                    "href": "http://api.oculusvr-demo.com:8081/v1/offer-revisions/6B55FFF0FC9F",
                                    "id": "6B55FFF0FC9F"
                                },
                                "publisher": {
                                    "href": "http://api.oculusvr-demo.com:8081/v1/users/6B54FF383C9F",
                                    "id": "6B54FF383C9F"
                                },
                                "isPublished": true,
                                "iapHostItem": null,
                                "revisions": {
                                    "href": "http://api.oculusvr-demo.com:8081/v1/offer-revisions?offerId=2B55FF387CDF",
                                    "id": null
                                }
                            },
                            {
                                "createdTime": "2014-05-01T00:00:00Z",
                                "updatedTime": "2014-05-01T00:00:00Z",
                                "rev": "2",
                                "categories": [],
                                "environment": null,
                                "self": {
                                    "href": "http://api.oculusvr-demo.com:8081/v1/offers/6BD4FF347CDF",
                                    "id": "6BD4FF347CDF"
                                },
                                "currentRevision": {
                                    "href": "http://api.oculusvr-demo.com:8081/v1/offer-revisions/6B55FFF07CDF",
                                    "id": "6B55FFF07CDF"
                                },
                                "publisher": {
                                    "href": "http://api.oculusvr-demo.com:8081/v1/users/6B54FF383C9F",
                                    "id": "6B54FF383C9F"
                                },
                                "isPublished": true,
                                "iapHostItem": null,
                                "revisions": {
                                    "href": "http://api.oculusvr-demo.com:8081/v1/offer-revisions?offerId=6BD4FF347CDF",
                                    "id": null
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
            Path: '/v1/offers/:offerId',
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
                            },
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
        },

        GetOfferRevisionsById:{
            Method: 'GET',
            Path: '/v1/offer-revisions/:revisionsId',
            ResponseItem: 'Succeed',
            Items: {
                'SucceedTiered': {
                    statusCode: 200,
                    headers: null,
                    data: {
                        "createdTime": "2014-05-01T00:00:00Z",
                        "updatedTime": "2014-05-01T00:00:00Z",
                        "rev": "2",
                        "status": "APPROVED",
                        "futureProperties": null,
                        "price": {
                            "priceType": "TIERED",
                            "priceTier": {
                                "href": "http://api.oculusvr-demo.com:8081/v1/price-tiers/6B55FFB03C9F",
                                "id": "6B55FFB03C9F"
                            },
                            "prices": null
                        },
                        "restrictions": {
                            "limitPerCustomer": 1,
                            "limitPerOrder": 1,
                            "preconditionItems": [],
                            "exclusionItems": []
                        },
                        "subOffers": [],
                        "items": [
                            {
                                "quantity": 1,
                                "item": {
                                    "href": "http://api.oculusvr-demo.com:8081/v1/items/2B55FFB0FC9F",
                                    "id": "2B55FFB0FC9F"
                                }
                            }
                        ],
                        "eligibleCountries": [
                            {
                                "href": "http://api.oculusvr-demo.com:8081/v1/countries/US",
                                "id": "US"
                            }
                        ],
                        "eventActions": {
                            "PURCHASE": [
                                {
                                    "type": "GRANT_ENTITLEMENT",
                                    "properties": null,
                                    "itemId": null,
                                    "entitlementDefinition": {
                                        "href": "http://api.oculusvr-demo.com:8081/v1/entitlement-definitions/2B55FFB4FC9F",
                                        "id": "2B55FFB4FC9F"
                                    }
                                },
                                {
                                    "type": "GRANT_ENTITLEMENT",
                                    "properties": null,
                                    "itemId": null,
                                    "entitlementDefinition": {
                                        "href": "http://api.oculusvr-demo.com:8081/v1/entitlement-definitions/2B55FFB4FC9F",
                                        "id": "2B55FFB4FC9F"
                                    }
                                }
                            ]
                        },
                        "startTime": null,
                        "endTIme": null,
                        "locales": {
                            "en_US": {
                                "name": "Above + Below 2",
                                "revisionNotes": "",
                                "longDescription": "<p>\r\n   Above + Below 2 is a Rift game, and it is played only with the Rift. Tilt your head to the side to turn, and look up or down to fly in these directions.\r\n</p>\r\n<p>\r\n\t Additionally it can be played as a traditional game (switch with ESC), and then it is played via mouse and keyboard, where the mouse steers the looking direction and ascend/descend and the keys A/D are used to turn.\r\n</p>\r\n<p>\r\n\t In the manu screen, look at the buttons and either nod with the head to activate them or click left MB.\r\n</p>\r\n<p>\r\n\t The main goal is to reach the angels/demons to the next heaven/hell by flying towards them and avoiding the enemies. Dodge the planes, look at the pigeons/crows to scare them away and look the enemy demons/angels into the eyes to vanquish them.\r\n</p>\r\n<p>\r\n\t Pickup potions to maintain travel speed at the maximum and to replendish power after an enemy attack, which results in blurry vision.\r\n</p>",
                                "shortDescription": "Above + Below 2 is a metaphor on the human condition. It gives the player the choice between good and bad, and this choice is visualized as as the ascend to heaven or the descend to hell.",
                                "images": {
                                    "thumb": {
                                        "id": "1396368074775",
                                        "href": "http://static.oculusvr.com/shareuploads/apps/1396892603314wh1k0uow29/screens/1396367932219tnboxswcdi_1396368074775.png",
                                        "size": 47692,
                                        "x": 280,
                                        "y": 163
                                    },
                                    "main": {
                                        "id": "1396368070031",
                                        "href": "http://static.oculusvr.com/shareuploads/apps/1396892603314wh1k0uow29/screens/1396367932219tnboxswcdi_1396368070031.jpg",
                                        "size": 48617,
                                        "x": 700,
                                        "y": 394
                                    },
                                    "general": [
                                        {
                                            "id": "1396368075926",
                                            "href": "http://static.oculusvr.com/shareuploads/apps/1396892603314wh1k0uow29/screens/1396367932219tnboxswcdi_1396368075926.jpg",
                                            "size": 44416,
                                            "x": 700,
                                            "y": 394
                                        }
                                    ]
                                },
                                "videos": [
                                    {
                                        "service": "YOUTUBE",
                                        "id": "5SuHFPd5UKU",
                                        "title": "Above + Below 2 Trailer"
                                    }
                                ],
                                "items": null
                            }
                        },
                        "self": {
                            "href": "http://api.oculusvr-demo.com:8081/v1/offer-revisions/6B55FFF0FC9F",
                            "id": "6B55FFF0FC9F"
                        },
                        "publisher": {
                            "href": "http://api.oculusvr-demo.com:8081/v1/users/6B54FF383C9F",
                            "id": "6B54FF383C9F"
                        },
                        "offer": {
                            "href": "http://api.oculusvr-demo.com:8081/v1/offers/2B55FF387CDF",
                            "id": "2B55FF387CDF"
                        }
                    }
                },

                'Succeed':{
                    statusCode: 200,
                    headers: null,
                    data:{"createdTime": "2014-04-28T00:00:00Z",
                        "updatedTime": "2014-04-28T00:00:00Z",
                        "rev": "2",
                        "status": "APPROVED",
                        "futureProperties": {},
                        "price": {
                            "priceType": "CUSTOM",
                            "priceTier": null,
                            "prices": {
                                "JPY": 1000,
                                "USD": 10
                            }
                        },
                        "restrictions": {
                            "limitPerCustomer": 10,
                            "limitPerOrder": 2,
                            "preconditionItems": [
                                {
                                    "href": "http://api.oculusvr-demo.com:8081/v1/items/000000001230",
                                    "id": "000000001230"
                                }
                            ],
                            "exclusionItems": [
                                {
                                    "href": "http://api.oculusvr-demo.com:8081/v1/items/000000000120",
                                    "id": "000000000120"
                                }
                            ]
                        },
                        "subOffers": [],
                        "items": [
                            {
                                "quantity": 1,
                                "item": {
                                    "href": "http://api.oculusvr-demo.com:8081/v1/items/6B55FF30BC9F",
                                    "id": "6B55FF30BC9F"
                                }
                            }
                        ],
                        "eligibleCountries": [
                            {
                                "href": "http://api.oculusvr-demo.com:8081/v1/countries/US",
                                "id": "US"
                            },
                            {
                                "href": "http://api.oculusvr-demo.com:8081/v1/countries/CN",
                                "id": "CN"
                            }
                        ],
                        "eventActions": {
                            "PURCHASE": [
                                {
                                    "type": "GRANT_ENTITLEMENT",
                                    "properties": null,
                                    "itemId": null,
                                    "entitlementDefinition": {
                                        "href": "http://api.oculusvr-demo.com:8081/v1/entitlement-definitions/6B55FF34BC9F",
                                        "id": "6B55FF34BC9F"
                                    }
                                },
                                {
                                    "type": "GRANT_ENTITLEMENT",
                                    "properties": null,
                                    "itemId": null,
                                    "entitlementDefinition": {
                                        "href": "http://api.oculusvr-demo.com:8081/v1/entitlement-definitions/6B55FF34BC9F",
                                        "id": "6B55FF34BC9F"
                                    }
                                }
                            ]
                        },
                        "startTime": null,
                        "endTIme": null,
                        "locales": {
                            "en_US": {
                                "name": "testOffer_CartCheckout_Digital1",
                                "revisionNotes": null,
                                "longDescription": null,
                                "shortDescription": null,
                                "images": null,
                                "videos": null,
                                "items": null
                            }
                        },
                        "self": {
                            "href": "http://api.oculusvr-demo.com:8081/v1/offer-revisions/6B55FF38BC9F",
                            "id": "6B55FF38BC9F"
                        },
                        "publisher": {
                            "href": "http://api.oculusvr-demo.com:8081/v1/users/6B55FFB8BC9F",
                            "id": "6B55FFB8BC9F"
                        },
                        "offer": {
                            "href": "http://api.oculusvr-demo.com:8081/v1/offers/6B54FF34BCDF",
                            "id": "6BD4FF347CDF"
                        }
                    }
                },

                'Failed': {
                    statusCode: 400,
                    headers: null,
                    data: null
                }
            }
        },

        GetPriceTiersById:{
            Method: 'GET',
            Path: '/v1/price-tiers/:id',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed':{
                    statusCode: 200,
                    headers: null,
                    data: {
                        "createdTime": "2014-04-28T00:00:00Z",
                        "updatedTime": "2014-05-01T00:00:00Z",
                        "rev": "3",
                        "prices": {
                            "JPY": 1000,
                            "USD": 9.99
                        },
                        "locales": {
                            "en_US": {
                                "name": "first price tier",
                                "description": "first price tier"
                            }
                        },
                        "self": {
                            "href": "http://api.oculusvr-demo.com:8081/v1/price-tiers/6B55FFB03C9F",
                            "id": "6B55FFB03C9F"
                        }
                    }
                },

                'Failed': {
                    statusCode: 400,
                    headers: null,
                    data: null
                }
            }
        },

        GetItemById: {
            Method: 'GET',
            Path: '/v1/items/:itemId',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data: {
                        name: "APP Name",
                        properties:{
                            downloadLink: "http://www.google.com"
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
            Path: '/v1/users/:userId/carts/primary',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 302,
                    headers: {
                        location: "http://localhost:8000/v1/users/10000/carts/21232"
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
            Path: '/v1/users/:userId/carts/:cardId',
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
            Path: '/v1/users/:userId/carts/:cardId',
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
            Path: '/v1/users/:userId/carts/:cardId/merge',
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
            Path: '/v1/users/:userId/ship-to-info',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data:{
                            results: [
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
                        }
                },

                'Failed': {
                    statusCode: 404,
                    headers: null,
                    data: null
                }
            }
        },
        GetShippingInfoById: {
            Method: 'GET',
            Path: '/v1/users/:userId/ship-to-info/:shippingAddressId',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data: {
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
                    }
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
            Path: '/v1/users/:userId/ship-to-info',
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
            Path: '/v1/orders',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data:
                        {
                            results: [
                                {
                                    "self": {
                                        "href": "https://xxx.xxx.xxx",
                                        "id": "0000-0027-8528"
                                    },
                                    "user": {
                                        "href": "https://xxx.xxx.xxx",
                                        "id": "000002040040"
                                    },
                                    "trackingUuid": "56a7c1c6-45c6-429b-8790-f352b1e27767",
                                    "type": "PAY_IN",
                                    "status": "FAILED",
                                    "country": "US",
                                    "currency": "USD",
                                    "tentative": false,
                                    "paymentInstruments": [
                                        {
                                            "href": "https://xxx.xxx.xxx",
                                            "id": "000002140000"
                                        }
                                    ],
                                    "discounts": [],
                                    "orderItems": [
                                        {
                                            "type": "DIGITAL",
                                            "offer": {
                                                "href": "https://xxx.xxx.xxx",
                                                "id": "0000002C0040"
                                            },
                                            "quantity": 2,
                                            "unitPrice": 9.99,
                                            "totalAmount": 18.98,
                                            "totalDiscount": 0,
                                            "totalTax": 0,
                                            "createdTime": "2014-03-24T08:02:45Z",
                                            "createdBy": "dev",
                                            "updatedTime": "2014-03-24T08:02:45Z",
                                            "updatedBy": "dev",
                                            "isTaxExempted": false,
                                            "honoredTime": "2014-03-24T08:02:39Z"
                                        },
                                        {
                                            "type": "DIGITAL",
                                            "offer": {
                                                "href": "https://xxx.xxx.xxx",
                                                "id": "0000002C0040"
                                            },
                                            "quantity": 2,
                                            "unitPrice": 9.99,
                                            "totalAmount": 9.99,
                                            "totalDiscount": 0,
                                            "totalTax": 0,
                                            "createdTime": "2014-03-24T08:02:45Z",
                                            "createdBy": "dev",
                                            "updatedTime": "2014-03-24T08:02:45Z",
                                            "updatedBy": "dev",
                                            "isTaxExempted": false,
                                            "honoredTime": "2014-03-24T08:02:39Z"
                                        }
                                    ],
                                    "createdTime": "2014-03-24T08:02:45Z",
                                    "createdBy": "dev",
                                    "updatedTime": "2014-03-25T07:17:33Z",
                                    "updatedBy": "dev",
                                    "totalAmount": 28.97,
                                    "totalTax": 0,
                                    "isTaxInclusive": false,
                                    "totalDiscount": 0,
                                    "totalShippingFee": 0,
                                    "totalShippingFeeDiscount": 0,
                                    "honoredTime": "2014-03-24T08:02:39Z"
                                },
                                {
                                    "self": {
                                        "href": "https://xxx.xxx.xxx",
                                        "id": "2222-0027-8528"
                                    },
                                    "user": {
                                        "href": "https://xxx.xxx.xxx",
                                        "id": "000002040040"
                                    },
                                    "trackingUuid": "56a7c1c6-45c6-429b-8790-f352b1e27767",
                                    "type": "PAY_IN",
                                    "status": "FAILED",
                                    "country": "US",
                                    "currency": "USD",
                                    "tentative": false,
                                    "paymentInstruments": [
                                        {
                                            "href": "https://xxx.xxx.xxx",
                                            "id": "000002140000"
                                        }
                                    ],
                                    "discounts": [],
                                    "orderItems": [
                                        {
                                            "type": "PHYSICAL",
                                            "offer": {
                                                "href": "https://xxx.xxx.xxx",
                                                "id": "0000002C0040"
                                            },
                                            "quantity": 1,
                                            "unitPrice": 9.99,
                                            "totalAmount": 9.99,
                                            "totalDiscount": 0,
                                            "totalTax": 0,
                                            "createdTime": "2014-03-24T08:02:45Z",
                                            "createdBy": "dev",
                                            "updatedTime": "2014-03-24T08:02:45Z",
                                            "updatedBy": "dev",
                                            "isTaxExempted": false,
                                            "honoredTime": "2014-03-24T08:02:39Z"
                                        }
                                    ],
                                    "createdTime": "2014-03-24T08:02:45Z",
                                    "createdBy": "dev",
                                    "updatedTime": "2014-03-25T07:17:33Z",
                                    "updatedBy": "dev",
                                    "totalAmount": 9.99,
                                    "totalTax": 0,
                                    "isTaxInclusive": false,
                                    "totalDiscount": 0,
                                    "totalShippingFee": 0,
                                    "totalShippingFeeDiscount": 0,
                                    "honoredTime": "2014-03-24T08:02:39Z"
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

        GetOrdersById: {
            Method: 'GET',
            Path: '/v1/orders/:orderId',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data: {
                        "self": {
                            "href": "https://xxx.xxx.xxx",
                            "id": "2222-0027-8528"
                        },
                        "user": {
                            "href": "https://xxx.xxx.xxx",
                            "id": "000002040040"
                        },
                        "trackingUuid": "56a7c1c6-45c6-429b-8790-f352b1e27767",
                        "type": "PAY_IN",
                        "status": "FAILED",
                        "country": "US",
                        "currency": "USD",
                        "tentative": false,
                        "shippingAddressId": 123,
                        "paymentInstruments": [
                            {
                                "href": "https://xxx.xxx.xxx",
                                "id": "000002140000"
                            }
                        ],
                        "discounts": [],
                        "orderItems": [
                            {
                                "type": "PHYSICAL",
                                "offer": {
                                    "href": "https://xxx.xxx.xxx",
                                    "id": "0000002C0040"
                                },
                                "quantity": 1,
                                "unitPrice": 9.99,
                                "totalAmount": 9.99,
                                "totalDiscount": 0,
                                "totalTax": 0,
                                "createdTime": "2014-03-24T08:02:45Z",
                                "createdBy": "dev",
                                "updatedTime": "2014-03-24T08:02:45Z",
                                "updatedBy": "dev",
                                "isTaxExempted": false,
                                "honoredTime": "2014-03-24T08:02:39Z"
                            },
                            {
                                "type": "DIGITAL",
                                "offer": {
                                    "href": "https://xxx.xxx.xxx",
                                    "id": "0000002C0040"
                                },
                                "quantity": 2,
                                "unitPrice": 9.99,
                                "totalAmount": 19.98,
                                "totalDiscount": 0,
                                "totalTax": 0,
                                "createdTime": "2014-03-24T08:02:45Z",
                                "createdBy": "dev",
                                "updatedTime": "2014-03-24T08:02:45Z",
                                "updatedBy": "dev",
                                "isTaxExempted": false,
                                "honoredTime": "2014-03-24T08:02:39Z"
                            }
                        ],
                        "createdTime": "2014-03-24T08:02:45Z",
                        "createdBy": "dev",
                        "updatedTime": "2014-03-25T07:17:33Z",
                        "updatedBy": "dev",
                        "totalAmount": 28.97,
                        "totalTax": 0,
                        "isTaxInclusive": false,
                        "totalDiscount": 1.00,
                        "totalShippingFee": 0,
                        "totalShippingFeeDiscount": 0,
                        "honoredTime": "2014-03-24T08:02:39Z"
                    }
                },

                'SucceedDigital': {
                    statusCode: 200,
                    headers: null,
                    data:
                    {
                        "self": {
                            "href": "https://xxx.xxx.xxx",
                            "id": "2222-0027-8528"
                        },
                        "user": {
                            "href": "https://xxx.xxx.xxx",
                            "id": "000002040040"
                        },
                        "trackingUuid": "56a7c1c6-45c6-429b-8790-f352b1e27767",
                        "type": "PAY_IN",
                        "status": "FAILED",
                        "country": "US",
                        "currency": "USD",
                        "tentative": false,
                        "paymentInstruments": [
                            {
                                "href": "https://xxx.xxx.xxx",
                                "id": "000002140000"
                            }
                        ],
                        "discounts": [],
                        "orderItems": [
                            {
                                "type": "DIGITAL",
                                "offer": {
                                    "href": "https://xxx.xxx.xxx",
                                    "id": "0000002C0040"
                                },
                                "quantity": 1,
                                "unitPrice": 9.99,
                                "totalAmount": 9.99,
                                "totalDiscount": 0,
                                "totalTax": 0,
                                "createdTime": "2014-03-24T08:02:45Z",
                                "createdBy": "dev",
                                "updatedTime": "2014-03-24T08:02:45Z",
                                "updatedBy": "dev",
                                "isTaxExempted": false,
                                "honoredTime": "2014-03-24T08:02:39Z"
                            },
                            {
                                "type": "DIGITAL",
                                "offer": {
                                    "href": "https://xxx.xxx.xxx",
                                    "id": "0000002C0040"
                                },
                                "quantity": 1,
                                "unitPrice": 9.99,
                                "totalAmount": 9.99,
                                "totalDiscount": 0,
                                "totalTax": 0,
                                "createdTime": "2014-03-24T08:02:45Z",
                                "createdBy": "dev",
                                "updatedTime": "2014-03-24T08:02:45Z",
                                "updatedBy": "dev",
                                "isTaxExempted": false,
                                "honoredTime": "2014-03-24T08:02:39Z"
                            }
                        ],
                        "createdTime": "2014-03-24T08:02:45Z",
                        "createdBy": "dev",
                        "updatedTime": "2014-03-25T07:17:33Z",
                        "updatedBy": "dev",
                        "totalAmount": 9.99,
                        "totalTax": 0,
                        "isTaxInclusive": false,
                        "totalDiscount": 0,
                        "totalShippingFee": 0,
                        "totalShippingFeeDiscount": 0,
                        "honoredTime": "2014-03-24T08:02:39Z"
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
            Path: '/v1/orders',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data: {
                        "self": {
                            "href": "https://xxx.xxx.xxx",
                            "id": "2222-0027-8528"
                        },
                        "user": {
                            "href": "https://xxx.xxx.xxx",
                            "id": "000002040040"
                        },
                        "trackingUuid": "56a7c1c6-45c6-429b-8790-f352b1e27767",
                        "type": "PAY_IN",
                        "status": "FAILED",
                        "country": "US",
                        "currency": "USD",
                        "tentative": false,
                        "paymentInstruments": [
                            {
                                "href": "https://xxx.xxx.xxx",
                                "id": "000002140000"
                            }
                        ],
                        "discounts": [],
                        "orderItems": [
                            {
                                "type": "PHYSICAL",
                                "offer": {
                                    "href": "https://xxx.xxx.xxx",
                                    "id": "0000002C0040"
                                },
                                "quantity": 1,
                                "unitPrice": 9.99,
                                "totalAmount": 9.99,
                                "totalDiscount": 0,
                                "totalTax": 0,
                                "createdTime": "2014-03-24T08:02:45Z",
                                "createdBy": "dev",
                                "updatedTime": "2014-03-24T08:02:45Z",
                                "updatedBy": "dev",
                                "isTaxExempted": false,
                                "honoredTime": "2014-03-24T08:02:39Z"
                            },
                            {
                                "type": "DIGITAL",
                                "offer": {
                                    "href": "https://xxx.xxx.xxx",
                                    "id": "0000002C0040"
                                },
                                "quantity": 1,
                                "unitPrice": 9.99,
                                "totalAmount": 9.99,
                                "totalDiscount": 0,
                                "totalTax": 0,
                                "createdTime": "2014-03-24T08:02:45Z",
                                "createdBy": "dev",
                                "updatedTime": "2014-03-24T08:02:45Z",
                                "updatedBy": "dev",
                                "isTaxExempted": false,
                                "honoredTime": "2014-03-24T08:02:39Z"
                            }
                        ],
                        "createdTime": "2014-03-24T08:02:45Z",
                        "createdBy": "dev",
                        "updatedTime": "2014-03-25T07:17:33Z",
                        "updatedBy": "dev",
                        "totalAmount": 9.99,
                        "totalTax": 0,
                        "isTaxInclusive": false,
                        "totalDiscount": 0,
                        "totalShippingFee": 0,
                        "totalShippingFeeDiscount": 0,
                        "honoredTime": "2014-03-24T08:02:39Z"
                    }
                },

                'SucceedDigital': {
                    statusCode: 200,
                    headers: null,
                    data:
                    {
                        "self": {
                            "href": "https://xxx.xxx.xxx",
                            "id": "2222-0027-8528"
                        },
                        "user": {
                            "href": "https://xxx.xxx.xxx",
                            "id": "000002040040"
                        },
                        "trackingUuid": "56a7c1c6-45c6-429b-8790-f352b1e27767",
                        "type": "PAY_IN",
                        "status": "FAILED",
                        "country": "US",
                        "currency": "USD",
                        "tentative": false,
                        "paymentInstruments": [
                            {
                                "href": "https://xxx.xxx.xxx",
                                "id": "000002140000"
                            }
                        ],
                        "discounts": [],
                        "orderItems": [
                            {
                                "type": "DIGITAL",
                                "offer": {
                                    "href": "https://xxx.xxx.xxx",
                                    "id": "0000002C0040"
                                },
                                "quantity": 1,
                                "unitPrice": 9.99,
                                "totalAmount": 9.99,
                                "totalDiscount": 0,
                                "totalTax": 0,
                                "createdTime": "2014-03-24T08:02:45Z",
                                "createdBy": "dev",
                                "updatedTime": "2014-03-24T08:02:45Z",
                                "updatedBy": "dev",
                                "isTaxExempted": false,
                                "honoredTime": "2014-03-24T08:02:39Z"
                            },
                            {
                                "type": "DIGITAL",
                                "offer": {
                                    "href": "https://xxx.xxx.xxx",
                                    "id": "0000002C0040"
                                },
                                "quantity": 1,
                                "unitPrice": 9.99,
                                "totalAmount": 9.99,
                                "totalDiscount": 0,
                                "totalTax": 0,
                                "createdTime": "2014-03-24T08:02:45Z",
                                "createdBy": "dev",
                                "updatedTime": "2014-03-24T08:02:45Z",
                                "updatedBy": "dev",
                                "isTaxExempted": false,
                                "honoredTime": "2014-03-24T08:02:39Z"
                            }
                        ],
                        "createdTime": "2014-03-24T08:02:45Z",
                        "createdBy": "dev",
                        "updatedTime": "2014-03-25T07:17:33Z",
                        "updatedBy": "dev",
                        "totalAmount": 9.99,
                        "totalTax": 0,
                        "isTaxInclusive": false,
                        "totalDiscount": 0,
                        "totalShippingFee": 0,
                        "totalShippingFeeDiscount": 0,
                        "honoredTime": "2014-03-24T08:02:39Z"
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
            Path: '/v1/orders/:orderId',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data: {
                        "self": {
                            "href": "https://xxx.xxx.xxx",
                            "id": "2222-0027-8528"
                        },
                        "user": {
                            "href": "https://xxx.xxx.xxx",
                            "id": "000002040040"
                        },
                        "trackingUuid": "56a7c1c6-45c6-429b-8790-f352b1e27767",
                        "type": "PAY_IN",
                        "status": "FAILED",
                        "country": "US",
                        "currency": "USD",
                        "tentative": false,
                        "paymentInstruments": [
                            {
                                "href": "https://xxx.xxx.xxx",
                                "id": "000002140000"
                            }
                        ],
                        "discounts": [],
                        "orderItems": [
                            {
                                "type": "PHYSICAL",
                                "offer": {
                                    "href": "https://xxx.xxx.xxx",
                                    "id": "0000002C0040"
                                },
                                "quantity": 1,
                                "unitPrice": 9.99,
                                "totalAmount": 9.99,
                                "totalDiscount": 0,
                                "totalTax": 0,
                                "createdTime": "2014-03-24T08:02:45Z",
                                "createdBy": "dev",
                                "updatedTime": "2014-03-24T08:02:45Z",
                                "updatedBy": "dev",
                                "isTaxExempted": false,
                                "honoredTime": "2014-03-24T08:02:39Z"
                            },
                            {
                                "type": "DIGITAL",
                                "offer": {
                                    "href": "https://xxx.xxx.xxx",
                                    "id": "0000002C0040"
                                },
                                "quantity": 1,
                                "unitPrice": 9.99,
                                "totalAmount": 9.99,
                                "totalDiscount": 0,
                                "totalTax": 0,
                                "createdTime": "2014-03-24T08:02:45Z",
                                "createdBy": "dev",
                                "updatedTime": "2014-03-24T08:02:45Z",
                                "updatedBy": "dev",
                                "isTaxExempted": false,
                                "honoredTime": "2014-03-24T08:02:39Z"
                            }
                        ],
                        "createdTime": "2014-03-24T08:02:45Z",
                        "createdBy": "dev",
                        "updatedTime": "2014-03-25T07:17:33Z",
                        "updatedBy": "dev",
                        "totalAmount": 9.99,
                        "totalTax": 0,
                        "isTaxInclusive": false,
                        "totalDiscount": 0,
                        "totalShippingFee": 0,
                        "totalShippingFeeDiscount": 0,
                        "honoredTime": "2014-03-24T08:02:39Z"
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
            Path: '/v1/users/:userId/payment-instruments', //'/v1/users/:userId/payment-instruments/search',
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
                                "accountName": "Tom Slick",
                                "accountNum": "4111111111111111",
                                "isValidated": "false",
                                "isDefault": "true",
                                "type": {
                                    "href": "http://api.wan-san.com/v1/payment-instrument-types/CREDITCARD",
                                    "id": "CREDITCARD"
                                },
                                "creditCardRequest": {
                                    "creditCardType": "VISA",
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
                                "accountName": "Tom 2 Slick",
                                "accountNum": "4111111111155555",
                                "isValidated": "false",
                                "isDefault": "false",
                                "type": {
                                    "href": "http://api.wan-san.com/v1/payment-instrument-types/CREDITCARD",
                                    "id": "CREDITCARD"
                                },
                                "creditCardRequest": {
                                    "creditCardType": "MaterCard",
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
            Path: '/v1/users/:userId/payment-instruments/:paymentInstrumentId',
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
                            "creditCardType": "VISA",
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
            Path: '/v1/users/:userId/payment-instruments',
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
            Path: '/v1/users/:userId/payment-instruments ',
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
            Method: 'GET', // TODO: DELETE
            Path: '/v1/users/:userId/payment-instruments/:paymentInstrumentId',
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
    },

    Entitlement:{
        GetEntitlements: {
            Method: 'GET',
            Path: '/v1/users/:userId/entitlements', //'/v1/users/:userId/payment-instruments/search',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data:{
                        "results": [
                            {
                                "self": {
                                    "href": "http://api.wan-san.com/v1/entitlements/00200B050000",
                                    "id": "00200B050000"
                                },
                                "user": {
                                    "href": "http://api.wan-san.com/v1/users/000001234444",
                                    "id": "000001234444"
                                },
                                "developer": {
                                    "href": "http://api.wan-san.com/v1/users/000000001234",
                                    "id": "000000001234"
                                },
                                "status": "ACTIVE",
                                "type": "DEVELOPER",
                                "group": "",
                                "tag": "",
                                "grantTime": "2014-03-25T08:47:46Z",
                                "consumable": false,
                                "useCount": 0,
                                "managedLifecycle": true
                            },

                            {
                                "self": {
                                    "href": "http://api.wan-san.com/v1/entitlements/00200B050000",
                                    "id": "2222"
                                },
                                "user": {
                                    "href": "http://api.wan-san.com/v1/users/000001234444",
                                    "id": "2222"
                                },
                                "developer": {
                                    "href": "http://api.wan-san.com/v1/users/000000001234",
                                    "id": "222"
                                },
                                "status": "ACTIVE",
                                "type": "Download",
                                "group": "",
                                "tag": "",
                                "grantTime": "2014-03-25T08:47:46Z",
                                "consumable": false,
                                "useCount": 0,
                                "managedLifecycle": true
                            }
                        ],
                        "next": {
                            "href": "END"
                        }
                    }
                },

                'SucceedNotExists': {
                    statusCode: 200,
                    headers: null,
                    data:{
                        "results": [],
                        "next": {
                            "href": "END"
                        }
                    }
                },

                'Failed': {
                    statusCode: 404,
                    headers: null,
                    data: null
                }
            }
        },

        PostEntitlement: {
            Method: 'POST',
            Path: '/v1/users/:userId/entitlements', //?developerId=1234',
            ResponseItem: 'Succeed',
            Items: {
                'Succeed': {
                    statusCode: 200,
                    headers: null,
                    data:
                    {
                        "self": {
                            "href": "http://api.wan-san.com/v1/entitlements/00200B050000",
                            "id": "00200B050000"
                        },
                        "user": {
                            "href": "https://data.oculusvr.com/v1/users/123",
                            "id": 1234444
                        },
                        "developer": {
                            "href": "https://data.oculusvr.com/v1/users/123",
                            "id": 1234
                        },
                        "type":"developer"
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