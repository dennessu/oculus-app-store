
/*
    Arguments:
        Reserved Keywords: data, cb
*/

module.exports = {
    Idtntity: {
        Config: {
            namespace: "/rest"
        },

        PostAuthorize: {
            Options: {
                path: "/oauth2/authorize",
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            },
            Arguments: {
                data: 0,
                cb: 1
            }
        },

        GetUserById: {
            Options: {
                path: "/users/{userId}",
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            },
            Arguments: {
                userId: 0,
                cb: 1
            }
        }
    },
    Billing:{
        Config: {
            namespace: "/rest"
        },

        GetShippingInfos:{
            Options: {
                path: "/users/{userId}/ship-to-info",
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            },
            Arguments: {
                userId: 0,
                cb: 1
            }
        },

        GetShippingInfosById:{
            Options: {
                path: "/users/{userId}/ship-to-info/{shippingId}",
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            },
            Arguments: {
                userId: 0,
                shippingId: 1,
                cb: 2
            }
        },

        PutShippingInfosById:{
            Options: {
                path: "/users/{userId}/ship-to-info/{shippingId}",
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                }
            },
            Arguments: {
                userId: 0,
                shippingId: 1,
                data: 2,
                cb: 3
            }
        },

        DeleteShippingInfoById:{
            Options: {
                path: "/users/{userId}/ship-to-info/{shippingId}",
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            },
            Arguments: {
                userId: 0,
                shippingId: 1,
                cb: 2
            }
        },

        PostShippingInfo:{
            Options: {
                path: "/users/{userId}/ship-to-info",
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            },
            Arguments: {
                userId: 0,
                data: 1,
                cb: 2
            }
        }
    },
    Catalog:{
        Config: {
            namespace: "/rest"
        },

        GetOffers:
        {
            Options: {
                path: "/offers?status=Released",
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            },
            Arguments: {
                userId: 0,
                cb: 1
            }
        },

        GetOfferById:
        {
            Options: {
                path: "/offers/{offerId}",
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            },
            Arguments: {
                offerId: 0,
                cb: 1
            }
        }
    },
    Emails:{
        Config: {
            namespace: "/rest"
        },

        Send:
        {
            Options: {
                path: "/emails",
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            },
            Arguments: {
                data: 0,
                cb: 1
            }
        },
    }


};