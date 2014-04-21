(function(){
    var Config = {
        env: "production",
        RunProcessNumber: 1, // useful for cluster launch
        Localhost: "store.oculusvr-demo.com",
        ListenOnPort: 80,

        ProviderHost: "http://provider.oculusvr-demo.com",

        Identity_API_Host: "api.oculusvr-demo.com",
        Identity_API_Port: 8081,

        Catalog_API_Host: "api.oculusvr-demo.com",
        Catalog_API_Port: 8083,

        Cart_API_Host: "api.oculusvr-demo.com",
        Cart_API_Port: 8082,

        Order_API_Host: "api.oculusvr-demo.com",
        Order_API_Port: 8082,

        Billing_API_Host: "api.oculusvr-demo.com",
        Billing_API_Port: 8082,

        Payment_API_Host: "api.oculusvr-demo.com",
        Payment_API_Port: 8082,

        Entitlement_API_Host: "api.oculusvr-demo.com",
        Entitlement_API_Port: 8082,

        Emails_API_Host: "api.oculusvr-demo.com",
        Emails_API_Port: 8082,

        OauthHost: "http://api.oculusvr-demo.com:8081"   //http://54.186.20.200:8081
    };

    if(typeof(window) != "undefined"){
        Module.Load(this, "Config.Prod", Config);
    }else{
        module.exports = Config;
    }
}).call(this);
