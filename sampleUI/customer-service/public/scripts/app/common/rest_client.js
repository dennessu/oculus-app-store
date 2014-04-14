/**
 * Created by Haiwei on 2014/4/14.
 */

var RestClient = function(){};

RestClient.prototype.Request = function(options, data, cb){

    var async = true;
    var headers = {};
    var contentType = "application/x-www-form-urlencoded";

    if(typeof(options["async"]) != "undefined") async = options["async"];
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
        cache: false,
        headers: headers,
        contentType: contentType,
        data: data
    });

    $.ajax({
        url: options["url"],
        type: options["method"],
        async: async,
        cache: false,
        headers: headers,
        contentType: contentType,
        data: data,
        success: function(data, textStatus, jqXHR){
            var resultData = {
                status: jqXHR.status,
                data: data,
                body: jqXHR.responseText
            };

            //TODO: Log
            console.log("Response: ", options["url"], " ", resultData);

            cb(resultData);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            var resultData = {
                status: XMLHttpRequest.status,
                data: textStatus == null ? errorThrown : textStatus,
                body: XMLHttpRequest.responseText
            };

            //TODO: Log
            console.log("Response: ", options["url"], " ", resultData);

            cb(resultData);
        }
    });
};