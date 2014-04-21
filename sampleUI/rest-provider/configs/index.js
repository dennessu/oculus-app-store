(function(){
    var Utils = null;
    var DevConfig = null;
    var ProdConfig = null;
    var Configs = null;
    var GlobalConfig = null;

    if(typeof(window) != "undefined"){
        Utils = window.Lib.Utils;
        DevConfig = window.Config.Dev;
        ProdConfig = window.Config.Prod;
        Configs = window.AppConfig;
    }else{
        Utils = require('../lib/utils');
        DevConfig = require('./dev');
        ProdConfig = require('./prod');
        Configs = require('./config');
    }

    var Configuration = function(){};
    Configuration.Init = function(env){

        if(env.toLowerCase() == 'prod'){
            GlobalConfig = Utils.FillObject(Configs, ProdConfig, 0);
        }else{
            GlobalConfig = Utils.FillObject(Configs, DevConfig, 0);
        }

        // Configuration Runtime Items
        var localhost = Utils.Format("http://{1}:{2}", GlobalConfig.Localhost, GlobalConfig.ListenOnPort);
        if(GlobalConfig.ListenOnPort == 80){
            localhost = Utils.Format("http://{1}", GlobalConfig.Localhost);
        }
        GlobalConfig.Runtime.SocketAddress = localhost;
        GlobalConfig.Runtime.LoginCallbackUrl = Utils.Format(GlobalConfig.Runtime.LoginCallbackUrl, localhost);
        GlobalConfig.Runtime.RegisterCallbackUrl = Utils.Format(GlobalConfig.Runtime.RegisterCallbackUrl, localhost);
        GlobalConfig.Runtime.LogoutCallbackUrl = Utils.Format(GlobalConfig.Runtime.LogoutCallbackUrl, localhost);
        GlobalConfig.Runtime.LoginUrl = Utils.Format(GlobalConfig.Runtime.LoginUrl, GlobalConfig.OauthHost, GlobalConfig.Runtime.LoginCallbackUrl);
        GlobalConfig.Runtime.LogoutUrl = Utils.Format(GlobalConfig.Runtime.LogoutUrl, GlobalConfig.OauthHost);
        GlobalConfig.Runtime.RegisterUrl = Utils.Format(GlobalConfig.Runtime.RegisterUrl, localhost, GlobalConfig.Runtime.RegisterCallbackUrl);

        if(typeof(window) != "undefined"){
            Module.Load(this, "AppConfig", GlobalConfig);
        }else{
            global.AppConfig = GlobalConfig;
        }
    };

    if(typeof(window) != "undefined"){
        Module.Load(this, "Configuration", Configuration);
    }else{
        module.exports = Configuration;
    }
}).call(this);
