var Configuration = require('rest-provider').Configuration;
var Utils = require('rest-provider').Utils;

var GlobalConfig = require('./config');
var envConfig = null;
var ApplicationConfig = function(){};

ApplicationConfig.Init = function(args){

    if(args.length > 2 && args[2].toLowerCase() == 'prod'){
        Configuration.Init('prod');
        envConfig = require('./prod');
        process.env.NAME = "production";
        console.log('Application environment is Production.');
    }else{
        Configuration.Init('dev');
        envConfig = require('./dev');
        process.env.NAME = "development";
        console.log('Application environment is Development.');
    }

    GlobalConfig = Utils.FillObject(GlobalConfig, envConfig, 0);
    global.AppConfig = Utils.FillObject(global.AppConfig, GlobalConfig, 0);

    Configuration.InitRuntime();

    process.env.PORT = global.AppConfig.ListenOnPort;
};

module.exports = ApplicationConfig;