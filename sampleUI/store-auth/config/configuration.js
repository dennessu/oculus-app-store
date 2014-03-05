module.exports = {

    Identity_API_Host: "54.186.20.200",
    Identity_API_Port: 8081,

    Google_Captcha_Hostname: "www.google.com",
    Google_Captcha_VerifyUrl: "/recaptcha/api/verify",
    Google_Captcha_PrivateKey: "6LeKhO4SAAAAACP5W0NoL7YV9dZAs3-5Z-T4Dl5i",

    EnabledCaptcha: true,
    CookiesTimeout: 10 * 60 * 1000,

    SaveQueryStringArray: ["redirect_url", "fs"],
    QueryStringConstants: {
        RedirectUrl: "redirect_url",
        FlowState: "fs"
    },
    CookiesKeyConstants: {
        AccessToken: "access_token",
        IdToken: "id_token"
    },

    APIFileds: {
        AccessToken: "access_token",
        IdToken: "id_token"
    }


};