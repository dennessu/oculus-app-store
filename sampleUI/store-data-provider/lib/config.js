
/*
    Arguments:
        Reserved Keywords: data, cb
*/

module.exports = {
    Identity: {
        Config: {
            name: "Idtntity",
            namespace: "/rest"
        },

        PostAuthorize: {
            Rules:{
              is_full_path: false
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
                is_full_path: false
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
                is_full_path: false
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
                is_full_path: false
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
            Rules:{
                is_full_path: false
            },
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
            Rules:{
                is_full_path: false
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
                is_full_path: false
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

        PostProfile: {
            Rules:{
                is_full_path: false
            },
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
            Rules:{
                is_full_path: false
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
                is_full_path: false
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
                is_full_path: false
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
                is_full_path: false
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
            namespace: "/rest"
        },

        GetShippingInfosByUserId:{
            Rules:{
                is_full_path: false
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
                is_full_path: false
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
                is_full_path: false
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
                is_full_path: false
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
                is_full_path: false
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
            namespace: "/rest"
        },

        GetOffers: {
            Rules:{
                is_full_path: false
            },
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
                is_full_path: false
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

        GetItemById: {
            Rules:{
                is_full_path: false
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
            namespace: "/rest"
        },

        Send: {
            Rules:{
                is_full_path: false
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
            namespace: "/rest"
        },

        GetEntitlementsByUserId: {
            Rules:{
                is_full_path: false
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
                is_full_path: false
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
            namespace: "/rest"
        },

        GetPrimaryCartByUserId: {
            Rules:{
                is_full_path: false
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
                is_full_path: false
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
                is_full_path: false
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
                is_full_path: false
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
            namespace: "/rest"
        },

        GetOrdersByUserId: {
            Rules:{
                is_full_path: false
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
                is_full_path: false
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
                is_full_path: false
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
                is_full_path: false
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
            namespace: "/rest"
        },

        GetPaymentInstrumentsByUserId: {
            Rules:{
                is_full_path: false
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
                is_full_path: false
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
                is_full_path: false
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
                is_full_path: false
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
                is_full_path: false
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