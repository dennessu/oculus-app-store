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
        GetLoginSuccessBackUrl: function(req){
            return Utils.Format("{1}/auth/login", Utils.GetCurrentDomain(req));
        },
        GetLoginUrl: function(req){
            return Utils.Format("{1}/rest/authorize?client_id=client&response_type=code&redirect_uri={2}&scope=openid%20identity&nonce=123",
                    process.AppConfig.OauthUri,
                    process.AppConfig.Urls.GetLoginSuccessBackUrl(req));
        },
        GetRegisterUrl: function(req){
            return Utils.Format("{1}/?redirect_url={2}/auth/register#/register", process.AppConfig.RegisterUri, Utils.GetCurrentDomain(req));
        }
    }
};

module.exports = Configs;