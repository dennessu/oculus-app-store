
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

        GetTokenInfo: {
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

        PutUser: {
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

        PostProfile: {
            Options: {
                path: "/users/{userId}/profiles",
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

        PutProfile: {
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
            namespace: "/rest"
        },

        GetShippingInfosByUserId:{
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
            namespace: "/rest"
        },

        GetOffers: {
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

        GetItemById: {
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
            namespace: "/rest"
        },

        Send: {
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
            namespace: "/rest"
        },

        GetEntitlementsByUserId: {
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
            namespace: "/rest"
        },

        GetPrimaryCartByUserId: {
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
            namespace: "/rest"
        },

        GetOrdersByUserId: {
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
            namespace: "/rest"
        },

        GetPaymentInstrumentsByUserId: {
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