var ResultModel = require('./lib/result_model');
var Configs = require('./lib/config');
var RestClient = require('./lib/rest_client');

var DataProvider = function(){};

DataProvider._BuildUrl = function(url, namespace, argsObj){
    if(url.indexOf('/') == 0){
        url = namespace + url;
    }else{
        url = namespace +"/"+ url;
    }
    if(typeof(argsObj) == "undefined" || argsObj == null) return url;
    if(url.indexOf("{") == -1) return url;

    // build url
    var maxLoopCount = 5;
    var re = new RegExp("\{\\w*\}", "i");
    var result = null;
    while((result = re.exec(url)) != null && maxLoopCount > 0)
    {
        maxLoopCount--;

        var varName = result[0].replace("{", "").replace("}", "");
        var varValue = argsObj[varName];

        if(varValue == undefined || varValue == null) throw "Parameters invalid! URL:" + url;

        url = url.replace(re, varValue);
    }

    return url;
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

// Append property
for(var s in Configs) {

    var server = function (host, port) {
        this.Host = host;
        this.Port = port;
        this.Config = Configs[s].Config;
        this.Client = new RestClient();
    };

    for (var api in Configs[s]) {
        if (api.toLowerCase() == "config") continue;

        // API Funcation
        server.prototype[api] = function () {
            var args = Configs[s][api].Arguments;
            var options = Configs[s][api].Options;

            console.log("Build Arguments for ", options["method"], options["path"], " Arguments: ", arguments);
            var argsObj = DataProvider._BuildArgumentsObject(args, arguments);
            options["host"] = this.Host;
            options["port"] = this.Port;
            options["path"] = DataProvider._BuildUrl(options["path"], this.Config.namespace, argsObj);

            if(typeof(argsObj['cb']) == "undefined" || argsObj['cb'] == null)
                throw "Can't configuration the callback function, please verify the config and arguments";

            if(typeof(argsObj['data']) == "undefined" || argsObj['data'] == null){
                client.Request(options, null, argsObj['cb']);
            }else{
                client.Request(options, argsObj['data'], argsObj['cb']);
            }
        }
    }

    DataProvider[s] = server;
}


exports.ResultModel = ResultModel;
exports.DataProvider = DataProvider;