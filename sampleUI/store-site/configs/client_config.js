var Utils = require('../utils/utils');

var ClientConfigs = {

    CookiesTimeout: 60 * 60 * 1000,
    Google_Captcha_PublicKey: "6LeKhO4SAAAAAL53gitVTB5ddevC59wE-6usFCnT",
    Feature: {
        Captcha: true,
        TFA: true
    },
    CookiesName: {
        AccessToken: "access_token",
        IdToken: "id_token",
        IsAuthenticate: "is_auth",
        UserId: "user_id",
        Username: "username",
        RedirectUrl: "redirect_url"
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
};

module.exports = ClientConfigs;