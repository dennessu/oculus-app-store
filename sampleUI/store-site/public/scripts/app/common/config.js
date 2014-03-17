var AppConfig = function () {};

AppConfig.Templates = {
    Login: { name: "login", url: "/template/identity/login"},
    Register: {name: "register", url: "/template/identity/register"}
};

AppConfig.API = {
    Identity: {
        Config: {
            Socket: "http://localhost:3000"
        },
        Login: {
            async: false,
            namespace: "/api/identity/login"
        },
        Register: {
            async: false,
            namespace: "/api/identity/register"
        }
    }
};

AppConfig.CookiesName = {
    ConversationId: "cid",
    Event: "event"
};

AppConfig.Init = function(){
    console.log("AppConfig.Init");

};
AppConfig.Init();