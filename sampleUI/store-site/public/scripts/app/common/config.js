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
        Detail: {name: "detail", url: "/template/store/detail"},
        Cart: {name: "cart", url: "/template/store/cart"},
        OrderSummary: {name: "cart", url: "/template/store/OrderSummary"}
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
        PIN: { Path: "pin" },
        GetAnonymousUser: { Path: "get_anonymous_user" }
    },
    Catalog: {
        Config: {
            Namespace: "/api/catalog/"
        },
        Products: { Path: "products" }
    },

    Cart: {
        Config: {
            Namespace: "/api/cart/"
        },
        Get: { Path: "get" },
        Add: { Path: "add" },
        Remove: { Path: "remove" },
        Update: { Path: "update" },
        Merge: { Path: "merge" }
    }
};

AppConfig.DataModelMapTable = {
    "Ember.App.Product": {
        Provider: "CatalogProvider",
        Method: "GetProducts"
    },
    "Ember.App.CartItem": {
        Provider: "CartProvider",
        Method: "Get"
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