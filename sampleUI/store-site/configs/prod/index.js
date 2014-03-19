module.exports = process.C = {

    RunProcessNumber: 2, // useful for cluster launch
    ListenOnPort: 80,

    Identity_API_Host: "54.186.20.200",
    Identity_API_Port: 8081,

    Catalog_API_Host: "54.186.20.200",
    Catalog_API_Port: 8083,

    Cart_API_Host: "54.186.20.200",
    Cart_API_Port: 8082,

    OauthUri: "http://54.186.20.200:8081",   //http://54.186.20.200:8081
    RegisterUri: "http://127.0.0.1:3000",

    // async client config
    SocketAddress: "http://localhost:3000"
};