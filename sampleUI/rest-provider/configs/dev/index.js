(function(){
    var Config = {
        env: "development",
        RunProcessNumber: 2, // useful for cluster launch
        Localhost: "csr.oculusvr-demo.com",
        ListenOnPort: 3000,

        Identity_API_Host: "127.0.0.1",
        Identity_API_Port: 8000,

        Catalog_API_Host: "127.0.0.1",
        Catalog_API_Port: 8000,

        Cart_API_Host: "127.0.0.1",
        Cart_API_Port: 8000,

        Order_API_Host: "127.0.0.1",
        Order_API_Port: 8000,

        Billing_API_Host: "127.0.0.1",
        Billing_API_Port: 8000,

        Payment_API_Host: "127.0.0.1",
        Payment_API_Port: 8000,

        Entitlement_API_Host: "127.0.0.1",
        Entitlement_API_Port: 8000,

        Emails_API_Host: "127.0.0.1",
        Emails_API_Port: 8000,

        OauthHost: "http://127.0.0.1:8000"  //http://54.186.20.200:8081
    };

    if(typeof(window) != "undefined"){
        Module.Load(this, "Config.Dev", Config);
    }else{
        module.exports = Config;
    }
}).call(this);