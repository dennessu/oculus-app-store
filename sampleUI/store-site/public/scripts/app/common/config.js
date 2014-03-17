var AppConfig = function () {};

AppConfig.Templates = {
    Login: { name: "login", url: "/template/identity/login"},
    Register: {name: "register", url: "/template/identity/register"},
    Captcha: {name: "captcha", url: "/template/identity/captcha"},
    TFA: {name: "tfa", url: "/template/identity/tfa"},
    My: {name: "my", url: "/template/identity/my"}
};

AppConfig.API = {
    Identity: {
        Config: {
            Socket: "http://localhost:3000"
        },
        Login: {
            async: false,
            namespace: "/api/identity/login"
        },
        Register: {
            async: false,
            namespace: "/api/identity/register"
        },
        Captcha: {
            async: false,
            namespace: "/api/identity/captcha"
        },
        TFA: {
            async: false,
            namespace: "/api/identity/tfa"
        }
    }
};

AppConfig.Countries = [
    {name: "Australia", value: "AU"},
    {name: "Canada", value: "CA"},
    {name: "China", value: "CN"},
    {name: "France", value: "FR"},
    {name: "Germany", value: "DE"},
    {name: "Singapore", value: "SG"},
    {name: "United States of America", value: "US"},
];

AppConfig.Init = function(){
    console.log("AppConfig.Init");
    $.ajax({
        type: "GET",
        url: "/config",
        async: false,
        success: function (data, textStatus) {
            AppConfig = Utils.FillObject(AppConfig, data, "full");
            console.log(AppConfig.CookiesTimeout);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log("Get Client Config Failed!");
        }
    });
};
AppConfig.Init();