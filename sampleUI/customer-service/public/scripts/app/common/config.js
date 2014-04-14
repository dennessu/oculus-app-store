var AppConfig = function () {};

AppConfig.DataModelMapTable = {
    "Ember.App.Product": {
        Provider: "CatalogProvider",
        Method: "GetProducts"
    },
    "Ember.App.CartItem": {
        Provider: "CartProvider",
        Method: "GetCart"
    },
    "Ember.App.ShippingInfo": {
        Provider: "BillingProvider",
        Method: "GetShippingInfos"
    },
    "Ember.App.CreditCard": {
        Provider: "PaymentProvider",
        Method: "GetPayments"
    }
};

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