var AppConfig = function () {};

AppConfig.Templates = {
    Identity: {
        Login: { name: "login", url: "/template/identity/login"},
        Captcha: {name: "captcha", url: "/template/identity/captcha"},
        TFA: {name: "tfa", url: "/template/identity/tfa"},
        Register: {name: "register", url: "/template/identity/register"},
        PIN: {name: "pin", url: "/template/identity/pin"},
        My: {name: "my", url: "/template/identity/my"}
    },
    Store: {
        Index: {name: "index", url: "/template/store/index"},
        Detail: {name: "detail", url: "/template/store/detail"}
    }
};

AppConfig.API = {
    Identity: {
        Config: {
            Namespace: "/api/identity/"
        },
        Login: { Path: "login" },
        Captcha: { Path: "captcha" },
        TFA: { Path: "tfa" },
        Register: { Path: "register" },
        PIN: { Path: "pin" }
    },
    Catalog: {
        Config: {
            Namespace: "/api/catalog/"
        },
        Products: { Path: "products" }
    }
};

AppConfig.DataModelMapTable = {
    "Ember.App.Product": {
        Provider: "CatalogProvider",
        Method: "GetProducts"
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