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
        Utils = require('../scripts/utils');
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

        if(typeof(window) != "undefined"){
            Module.Load(this, "AppConfig", GlobalConfig);
        }else{
            global.AppConfig = GlobalConfig;
        }
    };

    Configuration.InitRuntime = function(){

        var runConfig = global.AppConfig;

        // Configuration Runtime Items
        var localhost = Utils.Format.StringFormat("http://{1}:{2}", runConfig.Localhost, runConfig.ListenOnPort);
        if(runConfig.ListenOnPort == 80){
            localhost = Utils.Format.StringFormat("http://{1}", runConfig.Localhost);
        }
        runConfig.Runtime.SocketAddress = localhost;
        runConfig.Runtime.LoginCallbackUrl = Utils.Format.StringFormat(runConfig.Runtime.LoginCallbackUrl, localhost);
        runConfig.Runtime.RegisterCallbackUrl = Utils.Format.StringFormat(runConfig.Runtime.RegisterCallbackUrl, localhost);
        runConfig.Runtime.LogoutCallbackUrl = Utils.Format.StringFormat(runConfig.Runtime.LogoutCallbackUrl, localhost);
        runConfig.Runtime.LoginUrl = Utils.Format.StringFormat(runConfig.Runtime.LoginUrl, runConfig.OauthHost, runConfig.Runtime.LoginCallbackUrl);
        runConfig.Runtime.LogoutUrl = Utils.Format.StringFormat(runConfig.Runtime.LogoutUrl, runConfig.OauthHost);
        runConfig.Runtime.RegisterUrl = Utils.Format.StringFormat(runConfig.Runtime.RegisterUrl, localhost, runConfig.Runtime.RegisterCallbackUrl);
    };

    //[node]
    Configuration.GetClientConfigs = function(){
        var clientConfig = require('./client_config');

        return Utils.FillObject(clientConfig, global.AppConfig, 1);
    };

    if(typeof(window) != "undefined"){
        Module.Load(this, "Configuration", Configuration);
    }else{
        module.exports = Configuration;
    }
}).call(this);
