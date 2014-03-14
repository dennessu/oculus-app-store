var AppConfig = function () {};

AppConfig.Templates = {
    Login: { name: "login", url: "/template/identity/login"},
    Register: {name: "register", url: "/template/identity/register"}
};

AppConfig.API.Identity = {
    Login:{
        async: false,
        namespace: "/api/identity/login"
    }
};


AppConfig.Init = function(){
    console.log("AppConfig.Init");

};
AppConfig.Init();