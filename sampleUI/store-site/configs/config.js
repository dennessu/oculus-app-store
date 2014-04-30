var Utils = require('../utils/utils');

var Configs = {

    // async client config
    CookiesTimeout: 600 * 60 * 1000,
    Google_Captcha_PublicKey: "6LeKhO4SAAAAAL53gitVTB5ddevC59wE-6usFCnT",
    Feature: {
        Captcha: false,
        TFA: false
    },
    CookiesName: {
        AccessToken: "access_token",
        IdToken: "id_token",
        ConversationId: "cid",
        Event: "event",
        RedirectUrl: "redirect_url",

        UserId: "user_id",
        Username: "username",
        CartId: "cart_id",
        AnonymousUserId: "anonymous_user_id",
        AnonymousCartId: "anonymous_cart_id",
        ShippingId: "shipping_id",
        ShippingMethodId: "shipping_method_id",
        PaymentId: "payment_id",
        OrderId: "order_id",

        IsDev: "is_dev",
        BeforeRoute: "before_route"
    },
    QueryStrings: {
        Code: "code",
        IdToken: "id_token",
        AccessToken: "access_token",
        ConversationId: "cid",
        Event: "event",
        RedirectUrl: "redirect_url"
    },
    SettingTypeEnum:{
        Cookie: 'cookie'
    },
    UrlConstants: {
        CatalogManageAPPsUrl: "",
        CatalogManageOffersUrl: "http://catalog.oculusvr-demo.com:8081"
    },
    Runtime: {
        SocketAddress: "",
        LoginCallbackUrl: "{1}/callback/login",
        RegisterCallbackUrl: "{1}/callback/register",
        LogoutCallbackUrl: "{1}/callback/logout",
        LoginUrl: "{1}/rest/oauth2/authorize?client_id=client&response_type=code&redirect_uri={2}&scope=openid%20identity&nonce=123",
        //LoginUrl: "{1}/rest/oauth2/authorize?client_id=client&response_type=token%20idtoken&redirect_uri={2}&scope=openid%20identity&nonce=123",
        LogoutUrl: "{1}/rest/oauth2/end-session?post_logout_redirect_uri={2}&id_token_hint={3}",
        RegisterUrl: "{1}/identity?redirect_url={2}#/register"
    },

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

    APIs: {
        Identity: {
            Config: {
                namespace: "/api/identity/"
            },
            Login: { path: "post_login" },
            Captcha: { path: "captcha" },
            PostTFA: { path: "post_tfa" },
            Register: { path: "register" },
            PostPIN: { path: "post_pin" },
            GetAnonymousUser: { path: "get_anonymous_user" },
            GetPayinProfiles: { path: "get_payin_profiles" },
            PutProfile: { path: "put_profile" },
            RestPassword: { path: "rest_password" },
            GetOptIns: {path: "get_opt_ins"},
            PostOptIns: {path: "post_opt_ins"}
        },
        Catalog: {
            Config: {
                namespace: "/api/catalog/"
            },
            GetProducts: { path: "products" },
            GetDownloadLinks: { path: "get_download_links" }
        },

        Cart: {
            Config: {
                namespace: "/api/cart/"
            },
            GetCart: { path: "get_cart" },
            AddCartItem: { path: "add_cart_item" },
            RemoveCartItem: { path: "remove_cart_item" },
            UpdateCartItem: { path: "update_cart_item" },
            MergeCart: { path: "merge_cart" },
            PostOrder: { path: "post_order" },
            GetOrderById: { path: "get_order_by_id" },
            GetOrders: { path: "get_order_by_user" },
            PutOrder: { path: "put_order" },
            PurchaseOrder: { path: "purchase_order" }
        },
        Billing: {
            Config: {
                namespace: "/api/billing/"
            },
            GetShippingInfos: {path: "get_shipping_infos"},
            GetShippingInfoById: {path: "get_shipping_info_by_id"},
            PostShippingInfo: {path: "post_shipping_info"},
            DeleteShippingInfo: {path: "delete_shipping_info"}
        },
        Payment: {
            Config: {
                namespace: "/api/payment/"
            },
            GetPayments: {path: "get_payments" },
            GetPaymentById: {path: "get_payment_by_id"},
            PostPayment: {path: "post_payment"},
            DeletePayment: {path: "delete_payment"}
        },
        Entitlement: {
            Config: {
                namespace: "/api/entitlement/"
            },
            GetEntitlements: {path: "get_entitlements"},
            PostEntitlement: {path: "post_entitlement"}
        }
    },

    Templates: {
        Identity: {
            Login: "Login",
            Captcha: "Captcha",
            TFA: "TFA",
            Register: "Register",
            PIN: "PIN",
            My: "My"
        },
        Store: {
            Index: "Index",
            Detail: "Detail",
            Cart: "Cart",
            OrderSummary: "OrderSummary",
            Thanks: "Thanks"
        },
        ShippingInfo: {
            Layout: "Shipping",
            Index: "Shipping/index",
            Address: "Shipping/Address",
            Edit: "Shipping/Edit"
        },
        Payment: {
            Layout: "Payment",
            Index: "Payment/Index",
            Edit: "Payment/Edit"
        },
        Account:{
            Layout: "Account",
            Index: "Account/Index",
            EditInfo: "Account/EditInfo",
            EditPassword: "Account/EditPassword",
            EditShipping: "Account/EditShipping",
            Profile: "Account/Profile",
            EditProfile: "Account/EditProfile",
            History: "Account/History",
            Payment: "Account/Payment",
            AddPayment: "Account/AddPayment",
            Shipping: "Account/Shipping",
            AddShipping: "Account/AddShipping",
            Entitlements: "Account/Entitlements"
        },
        DevCenter: {
            Index: "DevCenter"
        }
    },

    // only on server
    Google_Captcha_Hostname: "www.google.com",
    Google_Captcha_VerifyUrl: "/recaptcha/api/verify",
    Google_Captcha_PrivateKey: "6LeKhO4SAAAAACP5W0NoL7YV9dZAs3-5Z-T4Dl5i",


    SaveQueryStringArray: ["cid", "event", "id_token", "access_token", "redirect_url"]
};

module.exports = Configs;