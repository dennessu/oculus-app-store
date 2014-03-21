var Utils = require('../utils/utils');

var Configs = {

    // async client config
    CookiesTimeout: 60 * 60 * 1000,
    Google_Captcha_PublicKey: "6LeKhO4SAAAAAL53gitVTB5ddevC59wE-6usFCnT",
    Feature: {
        Captcha: true,
        TFA: true
    },
    CookiesName: {
        AccessToken: "access_token",
        IdToken: "id_token",
        ConversationId: "cid",
        Event: "event",
        RedirectUrl: "redirect_url",

        UserId: "user_id",
        Username: "username",
        CartId: 'cart_id'
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

        TokenInfoUserId: "user_id",
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
            return Utils.Format("{1}/rest/authorize?client_id=client&response_type=code&redirect_uri={2}&scope=openid%20identity&nonce=123",
                    process.AppConfig.OauthUri,
                    process.AppConfig.Urls.GetCallbackLoginUrl(req));
        },
        GetRegisterUrl: function(req){
            return Utils.Format("{1}/identity?redirect_url={2}#/register", process.AppConfig.RegisterUri, process.AppConfig.Urls.GetCallbackRegisterUrl(req));
        }
    }
};

module.exports = Configs;