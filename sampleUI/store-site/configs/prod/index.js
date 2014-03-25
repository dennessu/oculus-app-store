module.exports = process.C = {

    RunProcessNumber: 2, // useful for cluster launch
    ListenOnPort: 80,

    Identity_API_Host: "10.0.1.166",
    Identity_API_Port: 8081,

    Catalog_API_Host: "10.0.1.166",
    Catalog_API_Port: 8083,

    Cart_API_Host: "10.0.1.166",
    Cart_API_Port: 8082,

    Order_API_Host: "10.0.1.166",
    Order_API_Port: 8082,

    Billing_API_Host: "10.0.1.166",
    Billing_API_Port: 8082,

    Payment_API_Host: "10.0.1.166",
    Payment_API_Port: 8082,

    OauthUri: "http://10.0.1.166:8081",   //http://54.186.20.200:8081
    RegisterUri: "http://127.0.0.1:3000",

    // async client config
    SocketAddress: "http://localhost"
};