var GlobalConfig = require('./config');
var Utils = require('../utils/utils');

var Config = null;
var Application = function(){};

Application.Init = function(args){

    if(args.length > 2 && args[2].toLowerCase() == 'prod'){
        console.log('Application environment is Production.');
        Config = require('./prod');
    }else{
        console.log('Application environment is Development.');
        Config = require('./dev');
    }

    process.env.PORT = Config.ListenOnPort;
    process.AppConfig = Utils.FillObject(GlobalConfig, Config, 0);
};

module.exports = Application;
