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
        GetAnonymousUser: { Path: "get_anonymous_user" },
        GetProfile: { Path: "get_profile" },
        PutProfile: { Path: "put_profile" },
        PutUser: { Path: "put_user" },
        GetOptIns: {Path: "get_opt_ins"},
        PostOptIns: {Path: "post_opt_ins"},
        Logout: {Path: "logout"}
    },
    Catalog: {
        Config: {
            Namespace: "/api/catalog/"
        },
        GetProducts: { Path: "products" },
        GetDownloadLinks: { Path: "get_download_links" }
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
        PostOrder: { Path: "post_order" },
        GetOrder: { Path: "get_order_by_id" },
        GetOrders: { Path: "get_order_by_user" },
        PutOrder: { Path: "put_order" },
        PurchaseOrder: { Path: "purchase_order" }
    },
    Billing: {
        Config: {
            Namespace: "/api/billing/"
        },
        ShippingInfo: {Path: "get_shipping_info"},
        Get: {Path: "get_shipping_info_by_id"},
        Add: {Path: "add"},
        Del: {Path: "del"}
    },
    Payment:{
        Config: {
            Namespace: "/api/payment/"
        },
        PaymentInstruments: {Path: "get_payment_instruments" },
        Get: {Path: "get_payment_instruments_by_id"},
        Add: {Path: "add"},
        Del: {Path: "del"}
    },
    Entitlement:{
        Config: {
            Namespace: "/api/entitlement/"
        },
        Get: {Path: "get"},
        GetByUser: { Path: "get_by_user"},
        Post: {Path: "post"}
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
    },
    "Ember.App.CreditCard": {
        Provider: "PaymentProvider",
        Method: "PaymentInstruments"
    },
    "Ember.App.Profile": {
        Provider: "IdentityProvider",
        Method: "GetProfile"
    }
};

AppConfig.PaymentType = [
    {name: "Credit Card", value: "CREDITCARD"}
];
AppConfig.CardType = {
    CreditCard: [
        {name: "VISA", value: "VISA"},
        {name: "MASTERCARD", value: "MASTERCARD"}
    ]
};
AppConfig.PaymentHolderType = [
    {name: "Parent", value: "Parent"}
];

AppConfig.ShippingMethods = [
    {name: "Standard", value: "000000000001"},
    {name: "Economy", value: "000000000002"},
    {name: "Express", value: "000000000003"}
];

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