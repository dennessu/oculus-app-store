var Configs = require('./../configs/config');
var Utils = require('./utils');
var RestClient = require('./rest_client');

var DataProvider = {};

DataProvider._BuildUrl = function(url, isFullPath, namespace, argsObj){
    var resultUrl = url;

    if(!isFullPath) {
        if (resultUrl.indexOf('/') == 0) {
            resultUrl = namespace + resultUrl;
        } else {
            resultUrl = namespace + "/" + resultUrl;
        }
    }
    if(typeof(argsObj) == "undefined" || argsObj == null) return resultUrl;
    if(resultUrl.indexOf("{") == -1) return resultUrl;

    // build url
    var maxLoopCount = 5;
    var re = new RegExp("\{\\w*\}", "i");
    var result = null;
    while((result = re.exec(resultUrl)) != null && maxLoopCount > 0)
    {
        maxLoopCount--;

        var varName = result[0].replace("{", "").replace("}", "");
        var varValue = argsObj[varName];

        if(varValue == undefined || varValue == null) throw "Parameters invalid! URL";

        resultUrl = resultUrl.replace(re, varValue);
    }

    return resultUrl;
};

DataProvider._BuildArgumentsObject = function(argumentsConfig, argsArr){
    if(argumentsConfig == undefined || argumentsConfig == null) return null;
    if(argsArr == undefined || argsArr == null || argsArr.length == 0) return argumentsConfig;

    var resultObj = {};
    for(var p in argumentsConfig){
        var argsIndex = argumentsConfig[p];

        if(argsArr.length <= argsIndex) throw "Provide the arguments invalid!";

        resultObj[p] = argsArr[argsIndex];
    }

    return resultObj;
};

DataProvider._Exec = function(provider, propertyName, args){

    var params = Utils.Clone(provider.SelfConfigItem[propertyName].Arguments);
    var options = Utils.Clone(provider.SelfConfigItem[propertyName].Options);

    var isFullPath = false;
    if(typeof(provider.SelfConfigItem[propertyName].Rules.is_full_path) != "undefined"){
        isFullPath = provider.SelfConfigItem[propertyName].Rules.is_full_path;
    }
    var namespace = "";
    if(typeof(provider.SelfConfigItem.Config.namespace) != "undefined"){
        namespace = provider.SelfConfigItem.Config.namespace;
    }

    var argsObj = DataProvider._BuildArgumentsObject(params, args);
    var pathUrl = DataProvider._BuildUrl(options["path"], isFullPath, namespace, argsObj);

    options["host"] = provider.Host;
    options["port"] = provider.Port;
    options["path"] = pathUrl;

    if(typeof(argsObj['cb']) == "undefined" || argsObj['cb'] == null)
        throw "Can't configuration the callback function, please verify the config and arguments";

    if(typeof(argsObj['data']) == "undefined" || argsObj['data'] == null){
        provider.Client.Request(options, null, argsObj['cb']);
    }else{
        provider.Client.Request(options, argsObj['data'], argsObj['cb']);
    }
};

// Append property
for (var s in Configs) {

    (function (parentClass, propertyName) {
        parentClass[propertyName] = function (host, port) {
            this.Host = host;
            this.Port = port;
            this.SelfConfigItem = Configs[propertyName];
            this.Config = this.SelfConfigItem.Config;
            this.Client = new RestClient();
        };
    })(DataProvider, s);


    for (var api in Configs[s]) {
        if (api.toLowerCase() == "config") continue;

        (function (parentClass, propertyName) {
            parentClass.prototype[propertyName] = function () {
                DataProvider._Exec(this, propertyName, arguments);
            }
        })(DataProvider[s], api);
    }
}

module.exports = DataProvider;