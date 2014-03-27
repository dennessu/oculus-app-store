module.exports = process.C = {

    RunProcessNumber: 2, // useful for cluster launch
    ListenOnPort: 80,

    Identity_API_Host: "10.0.1.133",
    Identity_API_Port: 8081,

    Catalog_API_Host: "10.0.1.133",
    Catalog_API_Port: 8083,

    Cart_API_Host: "10.0.1.133",
    Cart_API_Port: 8082,

    Order_API_Host: "10.0.1.133",
    Order_API_Port: 8082,

    Billing_API_Host: "10.0.1.133",
    Billing_API_Port: 8082,

    Payment_API_Host: "10.0.1.133",
    Payment_API_Port: 8082,

    Entitlement_API_Host: "10.0.1.133",
    Entitlement_API_Port: 8082,

    OauthUri: "http://10.0.1.133:8081",   //http://54.186.20.200:8081
    LogoutAjaxUrl: "http://10.0.1.133:8081/rest/oauth2/end-session",
    RegisterUri: "http://10.0.1.143",

    CatalogManageAPPsUrl: "#P",
    CatalogManageOffersUrl: "#P",

    // async client config
    SocketAddress: "http://10.0.1.143"
};