(function(){
    var Configs = {

        CookiesTimeout: 600 * 60 * 1000,
        Google_Captcha_PublicKey: "6LeKhO4SAAAAAL53gitVTB5ddevC59wE-6usFCnT",
        Feature: {
            Captcha: false,
            TFA: false
        },
        CookiesName: {
            Code: "code",
            AccessToken: "access_token",
            IdToken: "id_token",
            ConversationId: "cid",
            Event: "event",
            RedirectUrl: "redirect_url",

            UserId: "user_id",
            Username: "username",
            CustomerId: "customer_id"
        },
        QueryStrings: {
            Code: "code",
            IdToken: "id_token",
            AccessToken: "access_token",
            ConversationId: "cid",
            Event: "event",
            RedirectUrl: "redirect_url",

            LoginType: "login_type"   // [use login] code or token_idtoken
        },
        SaveQueryStrings: ["cid", "event", "id_token", "access_token", "redirect_url", "login_type"],

        SettingTypeEnum:{
            Cookie: 'cookie'
        },
        UrlConstants: {
            CatalogManageAPPsUrl: "",
            CatalogManageOffersUrl: "http://catalog.oculusvr-demo.com:8081"
        },
        Runtime: {
            LoginCallbackUrl: "{1}/callback/login",
            RegisterCallbackUrl: "{1}/callback/register",
            LogoutCallbackUrl: "{1}/callback/logout",
            LoginUrl: "{1}/rest/oauth2/authorize?client_id=client&response_type=code&redirect_uri={2}&scope=openid%20identity&nonce=123",
            LogoutUrl: "{1}/rest/oauth2/end-session?post_logout_redirect_uri={2}&id_token_hint={3}",
            RegisterUrl: "{1}/identity?redirect_url={2}#/register"
        },

        // hard code
        PaymentTypes: [
            {name: "Please choose", value: ""},
            {name: "Credit Card", value: "CREDITCARD"}
        ],
        CardTypes: {
            CreditCard: [
                {name: "Please choose", value: ""},
                {name: "VISA", value: "VISA"},
                {name: "MASTERCARD", value: "MASTERCARD"}
            ]
        },
        PaymentHolderTypes: [
            {name: "Please choose", value: ""},
            {name: "Parent", value: "Parent"}
        ],
        ShippingMethods: [
            {name: "Please choose", value: ""},
            {name: "Standard", value: "000000000001"},
            {name: "Economy", value: "000000000002"},
            {name: "Express", value: "000000000003"}
        ],
        Countries: [
            {name: "Please choose", value: ""},
            {name: "Canada", value: "CA"},
            {name: "China", value: "CN"},
            {name: "France", value: "FR"},
            {name: "Germany", value: "DE"},
            {name: "Singapore", value: "SG"},
            {name: "United States of America", value: "US"},
        ],

        // API Data field and other site post filed
        FieldNames: {
            AccessToken: "access_token",
            IdToken: "id_token",
            IdTokenUserId: "userId",
            Username: "userName",

            TokenInfoUser: "sub",
            ProfileFirstname: "firstName",
            ProfileLastname: "lastName",

            Results: "results"
        },

        /*
         Rules:{
         is_full_path: true  //When rest api return 302 in location url
         }
         Arguments: {
         options: 0,     //Reserved Keywords
         data: 1,        //Reserved Keywords
         cb: 2           //Reserved Keywords
         }
         */
        RestConfigs: {
            Identity: {
                Config: {
                    name: "Idtntity",
                    namespace: "/rest"
                },

                PostAuthorize: {
                    Rules:{},
                    Options: {
                        path: "/oauth2/authorize",
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded'
                        }
                    },
                    Arguments: {
                        options: 0,
                        data: 1,
                        cb: 2
                    }
                },

                GetTokenInfo: {
                    Rules:{},
                    Options: {
                        path: "/oauth2/tokeninfo?access_token={accessToken}",
                        method: 'GET',
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    },
                    Arguments: {
                        accessToken: 0,
                        options: 1,
                        cb: 2
                    }
                },

                PostTokenInfoByCode: {
                    Rules:{},
                    Options: {
                        path: "/oauth2/token",
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded'
                        }
                    },
                    Arguments: {
                        options: 0,
                        data: 1,
                        cb: 2
                    }
                },

                GetUserById: {
                    Rules:{},
                    Options: {
                        path: "/users/{userId}",
                        method: 'GET',
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    },
                    Arguments: {
                        userId: 0,
                        options: 1,
                        cb: 2
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
                        options: 0,
                        data: 1,
                        cb: 2
                    }
                },

                PutUser: {
                    Rules:{},
                    Options: {
                        path: "/users/{userId}",
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    },
                    Arguments: {
                        userId: 0,
                        options: 1,
                        data: 2,
                        cb: 3
                    }
                },

                PostRestPassword: {
                    Rules:{},
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
                        options: 2,
                        cb: 3
                    }
                },

                PostProfile: {
                    Rules:{},
                    Options: {
                        path: "/users/{userId}/profiles",
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    },
                    Arguments: {
                        userId: 0,
                        options: 1,
                        data: 2,
                        cb: 3
                    }
                },

                PutProfile: {
                    Rules:{},
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
                        options: 2,
                        data: 3,
                        cb: 4
                    }
                },

                GetPayinProfilesByUserId: {
                    Rules:{},
                    Options: {
                        path: "/users/{userId}/profiles?type=PAYIN",
                        method: 'GET',
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    },
                    Arguments: {
                        userId: 0,
                        options: 1,
                        cb: 2
                    }
                },

                GetOptInsByUserId:{
                    Rules:{},
                    Options: {
                        path: "/users/{userId}/opt-ins",
                        method: 'GET',
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    },
                    Arguments: {
                        userId: 0,
                        options: 1,
                        cb: 2
                    }
                },

                PostOptIns: {
                    Rules:{},
                    Options: {
                        path: "/users/{userId}/opt-ins",
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    },
                    Arguments: {
                        userId: 0,
                        options: 1,
                        data: 2,
                        cb: 3
                    }
                }
            },

            Billing:{
                Config: {
                    name: "Billing",
                    namespace: "/rest"
                },

                GetShippingInfosByUserId:{
                    Rules:{},
                    Options: {
                        path: "/users/{userId}/ship-to-info",
                        method: 'GET',
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    },
                    Arguments: {
                        userId: 0,
                        options: 1,
                        cb: 2
                    }
                },

                GetShippingInfoById:{
                    Rules:{},
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
                        options: 2,
                        cb: 3
                    }
                },

                PostShippingInfo:{
                    Rules:{},
                    Options: {
                        path: "/users/{userId}/ship-to-info",
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    },
                    Arguments: {
                        userId: 0,
                        options: 1,
                        data: 2,
                        cb: 3
                    }
                },

                PutShippingInfo:{
                    Rules:{},
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
                        options: 2,
                        data: 3,
                        cb: 4
                    }
                },

                DeleteShippingInfo:{
                    Rules:{},
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
                        options: 2,
                        cb: 3
                    }
                }
            },

            Catalog:{
                Config: {
                    name: "Catalog",
                    namespace: "/rest"
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
                        options: 0,
                        cb: 1
                    }
                },

                GetOfferById: {
                    Rules:{},
                    Options: {
                        path: "/offers/{offerId}",
                        method: 'GET',
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    },
                    Arguments: {
                        offerId: 0,
                        options: 1,
                        cb: 2
                    }
                },

                GetItemById: {
                    Rules:{},
                    Options: {
                        path: "/items/{itemId}",
                        method: 'GET',
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    },
                    Arguments: {
                        itemId: 0,
                        options: 1,
                        cb: 2
                    }
                }
            },

            Emails:{
                Config: {
                    name: "Emails",
                    namespace: "/rest"
                },

                Send: {
                    Rules:{},
                    Options: {
                        path: "/emails",
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    },
                    Arguments: {
                        options: 0,
                        data: 1,
                        cb: 2
                    }
                }
            },

            Entitlements: {
                Config: {
                    name: "Entitlements",
                    namespace: "/rest"
                },

                GetEntitlementsByUserId: {
                    Rules:{},
                    Options: {
                        path: "/users/{userId}/entitlements",
                        method: 'GET',
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    },
                    Arguments: {
                        userId: 0,
                        options: 1,
                        cb: 2
                    }
                },

                PostEntitlement: {
                    Rules:{},
                    Options: {
                        path: "/entitlements",
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    },
                    Arguments: {
                        options: 0,
                        data: 1,
                        cb: 2
                    }
                }
            },

            Cart: {
                Config: {
                    name: "Cart",
                    namespace: "/rest"
                },

                GetPrimaryCartByUserId: {
                    Rules:{},
                    Options: {
                        path: "/users/{userId}/carts/primary",
                        method: 'GET',
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    },
                    Arguments: {
                        userId: 0,
                        options: 1,
                        cb: 2
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
                        options: 1,
                        cb: 2
                    }
                },

                GetCartById: {
                    Rules:{},
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
                        options: 2,
                        cb: 3
                    }
                },

                PutCart: {
                    Rules:{},
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
                        options: 2,
                        data: 3,
                        cb: 4
                    }
                },

                PostMergeCart: {
                    Rules:{},
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
                        options: 2,
                        data: 3,
                        cb: 4
                    }
                }
            },

            Order: {
                Config: {
                    name: "Order",
                    namespace: "/rest"
                },

                GetOrdersByUserId: {
                    Rules:{},
                    Options: {
                        path: "/orders?userId={userId}",
                        method: 'GET',
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    },
                    Arguments: {
                        userId: 0,
                        options: 1,
                        cb: 2
                    }
                },

                GetOrderById: {
                    Rules:{},
                    Options: {
                        path: "/orders/{orderId}",
                        method: 'GET',
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    },
                    Arguments: {
                        orderId: 0,
                        options: 1,
                        cb: 2
                    }
                },

                PostOrder:  {
                    Rules:{},
                    Options: {
                        path: "/orders",
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    },
                    Arguments: {
                        options: 0,
                        data: 1,
                        cb: 2
                    }
                },

                PutOrder: {
                    Rules:{},
                    Options: {
                        path: "/orders/{orderId}",
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    },
                    Arguments: {
                        orderId: 0,
                        options: 1,
                        data: 2,
                        cb: 3
                    }
                }
            },

            Payment: {
                Config: {
                    name: "Payment",
                    namespace: "/rest"
                },

                GetPaymentInstrumentsByUserId: {
                    Rules:{},
                    Options: {
                        path: "/users/{userId}/payment-instruments",
                        method: 'GET',
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    },
                    Arguments: {
                        userId: 0,
                        options: 1,
                        cb: 2
                    }
                },

                GetPaymentInstrumentById: {
                    Rules:{},
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
                        options: 2,
                        cb: 3
                    }
                },

                PostPaymentInstrument: {
                    Rules:{},
                    Options: {
                        path: "/users/{userId}/payment-instruments",
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        }
                    },
                    Arguments: {
                        userId: 0,
                        options: 1,
                        data: 2,
                        cb: 3
                    }
                },

                PutPaymentInstrument: {
                    Rules:{},
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
                        options: 2,
                        data: 3,
                        cb: 4
                    }
                },

                DeltePaymentInstrument: {
                    Rules:{},
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
                        options: 2,
                        cb: 3
                    }
                }
            }
        }
    };

    if(typeof(window) != "undefined"){
        Module.Load(this, "AppConfig", Configs);
    }else{
        module.exports = Configs;
    }
}).call(this);

