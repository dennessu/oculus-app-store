var GlobalConfig = require('./config');
var Utils = require('../utils/utils');

var Config = null;
var Application = function(){};

Application.Init = function(args){

    if(args.length > 2 && args[2].toLowerCase() == 'prod'){
        console.log('Application environment is Production.');
        Config = require('./prod');

        process.env.NAME = "production";
    }else{
        console.log('Application environment is Development.');
        Config = require('./dev');

        process.env.NAME = "development";
    }

    process.env.PORT = Config.ListenOnPort;
    process.AppConfig = Utils.FillObject(GlobalConfig, Config, 0);

    // Configuration Runtime Items
    var localhost = Utils.Format("http://{1}:{2}", process.AppConfig.Localhost, process.AppConfig.ListenOnPort);
    if(process.AppConfig.ListenOnPort == 80){
        localhost = Utils.Format("http://{1}", process.AppConfig.Localhost);
    }
    process.AppConfig.Runtime.SocketAddress = localhost;
    process.AppConfig.Runtime.LoginCallbackUrl = Utils.Format(process.AppConfig.Runtime.LoginCallbackUrl, localhost);
    process.AppConfig.Runtime.RegisterCallbackUrl = Utils.Format(process.AppConfig.Runtime.RegisterCallbackUrl, localhost);
    process.AppConfig.Runtime.LogoutCallbackUrl = Utils.Format(process.AppConfig.Runtime.LogoutCallbackUrl, localhost);
    process.AppConfig.Runtime.LoginUrl = Utils.Format(process.AppConfig.Runtime.LoginUrl, process.AppConfig.OauthHost, process.AppConfig.Runtime.LoginCallbackUrl);
    process.AppConfig.Runtime.LogoutUrl = Utils.Format(process.AppConfig.Runtime.LogoutUrl, process.AppConfig.OauthHost);
    process.AppConfig.Runtime.RegisterUrl = Utils.Format(process.AppConfig.Runtime.RegisterUrl, localhost, process.AppConfig.Runtime.RegisterCallbackUrl);
};

module.exports = Application;
