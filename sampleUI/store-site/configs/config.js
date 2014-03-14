var Utils = require('../utils/utils');

var Configs = {

    CookiesTimeout: 10 * 60 * 1000,

    // API Data field and other site post filed
    FieldNames: {
        AccessToken: "access_token",
        IdToken: "id_token",
        IdTokenUserId: "userId",
        Username: "userName",

        TokenInfoUserId: "sub",
        ProfileFirstname: "firstName",
        ProfileLastname: "lastName"
    },

    QueryStrings: {
        Code: "code",
        IdToken: "id_token",
        AccessToken: "access_token"
    },
    SaveQueryStringArray: ["id_token", "access_token"],

    SessionKeys: {
        AccessToken: "access_token",
        IdToken: "id_token",
        IsAuthenticate: "is_auth",
        UserId: "user_id",
        Username: "username"
    },

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
            return Utils.Format("{1}/?redirect_url={2}#/register", process.AppConfig.RegisterUri, process.AppConfig.Urls.GetCallbackRegisterUrl(req));
        }
    }
};

module.exports = Configs;