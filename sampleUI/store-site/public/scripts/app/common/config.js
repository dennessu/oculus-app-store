var AppConfig = function () {};


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
        Merge: { Path: "merge" },
        PostOrder: { Path: "post_order" }
    },
    Billing: {
        Config: {
            Namespace: "/api/billing/"
        },
        ShippingInfo: {Path: "get_shipping_info"},
        Add: {Path: "add"}
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
    },
    "Ember.App.ShippingInfo": {
        Provider: "BillingProvider",
        Method: "ShippingInfo"
    }
};

AppConfig.Countries = [
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
            AppConfig = Utils.FillObject(AppConfig, data, 0);
            console.log(AppConfig.CookiesTimeout);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log("Get Client Config Failed!");
        }
    });
};
AppConfig.Init();