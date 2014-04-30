
/*
 Rules:{
    is_full_path: true  //When rest api return 302 in location url
 }

 Arguments: {
    data: 0,   //Reserved Keywords
    cb: 1      //Reserved Keywords
 }

*/

module.exports = {
    Identity: {
        Config: {
            name: "Idtntity",
            namespace: "/v1"
        },

        PostAuthorize: {
            Rules:{
            },
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

        GetTokenInfo: {
            Rules:{
            },
            Options: {
                path: "/oauth2/tokeninfo?access_token={accessToken}",
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            },
            Arguments: {
                accessToken: 0,
                cb: 1
            }
        },

        PostTokenInfoByCode: {
            Rules:{
            },
            Options: {
                path: "/oauth2/token",
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
            Rules:{
            },
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
        },

        PostUser: {
            Rules:{},
            Options: {
                path: "/users",
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

        PostChangeCredentials: {
            Rules:{},
            Options: {
                path: "/users/{userId}/change-credentials",
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
        },

        PutUser: {
            Rules:{
            },
            Options: {
                path: "/users/{userId}",
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                }
            },
            Arguments: {
                userId: 0,
                data: 1,
                cb: 2
            }
        },

        PostRestPassword: {
            Rules:{
            },
            Options: {
                path: "/users/{userId}/reset-password?newPassword={newPassword}",
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            },
            Arguments: {
                userId: 0,
                newPassword: 1,
                cb: 2
            }
        },

        PostPersonalInfo: {
            Rules:{
            },
            Options: {
                path: "/personalInfo",
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

        PutProfile: {
            Rules:{
            },
            Options: {
                path: "/users/{userId}/profiles/{profileId}",
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                }
            },
            Arguments: {
                userId: 0,
                profileId: 1,
                data: 2,
                cb: 3
            }
        },

        GetPayinProfilesByUserId: {
            Rules:{
            },
            Options: {
                path: "/users/{userId}/profiles?type=PAYIN",
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

        GetOptInsByUserId:{
            Rules:{
            },
            Options: {
                path: "/users/{userId}/opt-ins",
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

        PostOptIns: {
            Rules:{
                
            },
            Options: {
                path: "/users/{userId}/opt-ins",
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

    Billing:{
        Config: {
            name: "Billing",
            namespace: "/v1"
        },

        GetShippingInfosByUserId:{
            Rules:{
                
            },
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

        GetShippingInfoById:{
            Rules:{
                
            },
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

        PostShippingInfo:{
            Rules:{
                
            },
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
        },

        PutShippingInfo:{
            Rules:{
                
            },
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

        DeleteShippingInfo:{
            Rules:{
                
            },
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
        }
    },

    Catalog:{
        Config: {
            name: "Catalog",
            namespace: "/v1"
        },

        GetOffers: {
            Rules:{},
            Options: {
                path: "/offers?status=Released",
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            },
            Arguments: {
                cb: 0
            }
        },

        GetOfferById: {
            Rules:{
                
            },
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
        },

        GetOfferRevisionById: {
            Rules:{},
            Options: {
                path: "/offer-revisions/{revisionId}",
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            },
            Arguments: {
                revisionId: 0,
                cb: 1
            }
        },

        GetPriceTierById: {
            Rules:{},
            Options: {
                path: "/price-tiers/{priceTierId}",
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            },
            Arguments: {
                priceTierId: 0,
                cb: 1
            }
        },

        GetItemById: {
            Rules:{
                
            },
            Options: {
                path: "/items/{itemId}",
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            },
            Arguments: {
                itemId: 0,
                cb: 1
            }
        }
    },

    Emails:{
        Config: {
            name: "Emails",
            namespace: "/v1"
        },

        Send: {
            Rules:{
                
            },
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
        }
    },

    Entitlements: {
        Config: {
            name: "Entitlements",
            namespace: "/v1"
        },

        GetEntitlementsByUserId: {
            Rules:{
                
            },
            Options: {
                path: "/users/{userId}/entitlements",
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

        PostEntitlement: {
            Rules:{
                
            },
            Options: {
                path: "/entitlements",
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            },
            Arguments: {
                data: 0,
                cb: 1
            }
        }
    },

    Cart: {
        Config: {
            name: "Cart",
            namespace: "/v1"
        },

        GetPrimaryCartByUserId: {
            Rules:{
                
            },
            Options: {
                path: "/users/{userId}/carts/primary",
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

        GetCartByUrl: {
            Rules:{
                is_full_path: true
            },
            Options: {
                path: "{url}",
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            },
            Arguments: {
                url: 0,
                cb: 1
            }
        },

        GetCartById: {
            Rules:{
                
            },
            Options: {
                path: "/users/{userId}/carts/{cartId}",
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            },
            Arguments: {
                userId: 0,
                cartId: 1,
                cb: 2
            }
        },

        PutCart: {
            Rules:{
                
            },
            Options: {
                path: "/users/{userId}/carts/{cartId}",
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                }
            },
            Arguments: {
                userId: 0,
                cartId: 1,
                data: 2,
                cb: 3
            }
        },

        PostMergeCart: {
            Rules:{
                
            },
            Options: {
                path: "/users/{userId}/carts/{cartId}/merge",
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            },
            Arguments: {
                userId: 0,
                cartId: 1,
                data: 2,
                cb: 3
            }
        }
    },

    Order: {
        Config: {
            name: "Order",
            namespace: "/v1"
        },

        GetOrdersByUserId: {
            Rules:{
                
            },
            Options: {
                path: "/orders?userId={userId}",
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

        GetOrderById: {
            Rules:{
                
            },
            Options: {
                path: "/orders/{orderId}",
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            },
            Arguments: {
                orderId: 0,
                cb: 1
            }
        },

        PostOrder:  {
            Rules:{
                
            },
            Options: {
                path: "/orders",
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

        PutOrder: {
            Rules:{
                
            },
            Options: {
                path: "/orders/{orderId}",
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                }
            },
            Arguments: {
                orderId: 0,
                data: 1,
                cb: 2
            }
        }
    },

    Payment: {
        Config: {
            name: "Payment",
            namespace: "/v1"
        },

        GetPaymentInstrumentsByUserId: {
            Rules:{
                
            },
            Options: {
                path: "/users/{userId}/payment-instruments",
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

        GetPaymentInstrumentById: {
            Rules:{
                
            },
            Options: {
                path: "/users/{userId}/payment-instruments/{paymentId}",
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                }
            },
            Arguments: {
                userId: 0,
                paymentId: 1,
                cb: 2
            }
        },

        PostPaymentInstrument: {
            Rules:{
                
            },
            Options: {
                path: "/users/{userId}/payment-instruments",
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
        },

        PutPaymentInstrument: {
            Rules:{
                
            },
            Options: {
                path: "/users/{userId}/payment-instruments/{paymentId}",
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                }
            },
            Arguments: {
                userId: 0,
                paymentId: 1,
                data: 2,
                cb: 3
            }
        },

        DeltePaymentInstrument: {
            Rules:{
                
            },
            Options: {
                path: "/users/{userId}/payment-instruments/{paymentId}",
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            },
            Arguments: {
                userId: 0,
                paymentId: 1,
                cb: 2
            }
        }
    }
};