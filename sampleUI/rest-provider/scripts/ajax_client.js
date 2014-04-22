/**
 * Created by Haiwei on 2014/4/18.
 */
var RestClient = function(){};

RestClient.prototype.Request = function(options, data, cb){

    var async = true;
    var cache = false;
    var headers = {};
    var contentType = "application/x-www-form-urlencoded";

    if(typeof(options["async"]) != "undefined") async = options["async"];
    if(typeof(options["cache"]) != "undefined") cache = options["cache"];
    if(typeof(options["headers"]) != "undefined"){
        headers = options["headers"];

        if(typeof(headers["Content-Type"]) != "undefined"){
            contentType = headers["Content-Type"];
        }
    }

    // TODO: Request Log
    console.log("Request: ", options["url"], " ", {
        url: options["url"],
        type: options["method"],
        async: async,
        cache: cache,
        headers: headers,
        contentType: contentType,
        data: data
    });
    console.log("Request Options: ", options);

    $.ajax({
        url: options["url"],
        type: options["method"],
        async: async,
        cache: cache,
        headers: headers,
        contentType: contentType,
        data: data,
        success: function(data, textStatus, jqXHR){
            var model = new Lib.ResultModel();
            model.status = jqXHR.status;
            model.headers = {};
            model.data = data;

            //TODO: Log
            console.log("Response: ", options["url"], " ", model);

            cb(model);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            var model = new Lib.ResultModel();
            model.status = XMLHttpRequest.status;
            model.headers = {};
            model.data = XMLHttpRequest.responseText;

            //TODO: Log
            console.log("Response: ", options["url"], " ", model);

            cb(model);
        }
    });
};