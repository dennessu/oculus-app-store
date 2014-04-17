var Utils = require('../utils/utils');
var GlobalConfig = require('./config');

var Configuration = function(){};

Configuration.Init = function(args){
    var envConfig = null;

    if(args.length > 2 && args[2].toLowerCase() == 'prod'){
        console.log('Application environment is Production.');
        envConfig = require('./prod');

        process.env.NAME = "production";
    }else{
        console.log('Application environment is Development.');
        envConfig = require('./dev');

        process.env.NAME = "development";
    }

    process.env.PORT = envConfig.ListenOnPort;
    global.Config = Utils.FillObject(GlobalConfig, envConfig, 0);

    // Configuration Runtime Items
    var localhost = Utils.Format("http://{1}:{2}", global.Config.Localhost, global.Config.ListenOnPort);
    if(global.Config.ListenOnPort == 80){
        localhost = Utils.Format("http://{1}", global.Config.Localhost);
    }
    global.Config.Runtime.SocketAddress = localhost;
    global.Config.Runtime.LoginCallbackUrl = Utils.Format(global.Config.Runtime.LoginCallbackUrl, localhost);
    global.Config.Runtime.RegisterCallbackUrl = Utils.Format(global.Config.Runtime.RegisterCallbackUrl, localhost);
    global.Config.Runtime.LogoutCallbackUrl = Utils.Format(global.Config.Runtime.LogoutCallbackUrl, localhost);
    global.Config.Runtime.LoginUrl = Utils.Format(global.Config.Runtime.LoginUrl, global.Config.OauthHost, global.Config.Runtime.LoginCallbackUrl);
    global.Config.Runtime.LogoutUrl = Utils.Format(global.Config.Runtime.LogoutUrl, global.Config.OauthHost);
    global.Config.Runtime.RegisterUrl = Utils.Format(global.Config.Runtime.RegisterUrl, localhost, global.Config.Runtime.RegisterCallbackUrl);
};

module.exports = Configuration;
