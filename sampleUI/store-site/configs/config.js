var Utils = require('../utils/utils');

var Configs = {

    // async client config
    CookiesTimeout: 60 * 60 * 1000,
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
            AddShipping: "Account/AddShipping"
        },
        DevCenter: {
            Index: "DevCenter"
        }
    },

    // only on server
    Google_Captcha_Hostname: "www.google.com",
    Google_Captcha_VerifyUrl: "/recaptcha/api/verify",
    Google_Captcha_PrivateKey: "6LeKhO4SAAAAACP5W0NoL7YV9dZAs3-5Z-T4Dl5i",

    // API Data field and other site post filed
    FieldNames: {
        AccessToken: "access_token",
        IdToken: "id_token",
        IdTokenUserId: "userId",
        Username: "userName",

        TokenInfoUser: "sub",
        ProfileFirstname: "firstName",
        ProfileLastname: "lastName"
    },
    SaveQueryStringArray: ["cid", "event", "id_token", "access_token", "redirect_url"],

    Urls:{
        GetCallbackLoginUrl: function(req){
            return Utils.Format("{1}/callback/login", Utils.GetCurrentDomain(req));
        },
        GetCallbackRegisterUrl: function(req){
            return Utils.Format("{1}/callback/register", Utils.GetCurrentDomain(req));
        },
        GetLoginUrl: function(req){
            return Utils.Format("{1}/rest/oauth2/authorize?client_id=client&response_type=code&redirect_uri={2}&scope=openid%20identity&nonce=123",
                    process.AppConfig.OauthUri,
                    process.AppConfig.Urls.GetCallbackLoginUrl(req));
        },
        GetRegisterUrl: function(req){
            return Utils.Format("{1}/identity?redirect_url={2}#/register", process.AppConfig.RegisterUri, process.AppConfig.Urls.GetCallbackRegisterUrl(req));
        }
    }
};

module.exports = Configs;