var Utils = require('../utils/utils');

var ClientConfigs = {

    // Environments
    SocketAddress: "http://localhost:3000",

    // Global
    CookiesTimeout: 60 * 60 * 1000,
    Google_Captcha_PublicKey: "6LeKhO4SAAAAAL53gitVTB5ddevC59wE-6usFCnT",
    Feature: {
        Captcha: false,
        TFA: true,
        PIN: true
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
    }
};

module.exports = ClientConfigs;