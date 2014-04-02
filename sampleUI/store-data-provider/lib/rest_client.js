
var http = require("http");
var QueryString = require("querystring");
var APIResultModel = require("./result_model");

var RestClient = function(){};

RestClient.Encoding = "UTF8";
RestClient.GetDefaultOptions = function(){

  return {
    host: '127.0.0.1',
    port: 80,
    path: "/",
    method: 'POST',
    headers:{
      'accept': '*/*',
      'user-agent': 'wan-san.com rest client',
      'Content-Type': 'application/x-www-form-urlencoded'
    }
  };
};

/*
 Fill original object use target object
 @type: 0:full, 1:ChildFull, 2:OneWay
 @return: original object
 */
RestClient.prototype.FillObject = function(original, target, type){
    if(type == 0 && (original == undefined || original == null)) return target;

    for(var p in original){
        var p_type = typeof(original[p]);

        if(p_type != "function"){
            if(p_type == "object"){
                if(typeof(target[p]) != "undefined" && target[p] != null){
                    if(type == 1){
                        original[p] = this.FillObject(original[p], target[p], 0);
                    }else{
                        original[p] = this.FillObject(original[p], target[p], type);
                    }
                }
            }else{
                if(typeof(target[p]) != "undefined" && target[p] != null){
                    original[p] = target[p];
                }
            }
        }
    }

    if (type == 0) {
        // Append new property
        for (var p in target) {
            var p_type = typeof(target[p]);

            if (p_type != "function") {
                if (typeof(original[p]) != "undefined" && original[p] != null) continue;
                original[p] = target[p];
            }
        }
    }

    return original;
};

RestClient.prototype.Request = function(options, data, cb){

  console.log("HTTP Request --------------------", new Date());
  console.log("Request Options:\n", options);
  console.log("Request Data:\n", JSON.stringify(data));

  // Fill options
  var requestOpts = RestClient.GetDefaultOptions();
  requestOpts = this.FillObject(requestOpts, options, 0);

  // Handle data type
  if(data != null
      && typeof(data) != "undefined"
      && typeof(options.headers['Content-Type']) != "undefined"){

    switch(options.headers['Content-Type'].toLowerCase()){

      case "application/x-www-form-urlencoded": // POST Form
        data = QueryString.stringify(data);
        break;

      case "application/json": // POST JSON
        data = JSON.stringify(data);
        break;

      default:
        data = QueryString.stringify(data);
        break;
    }

    options.headers["Content-Length"] = data.length;
  }

  // Create request
  var request = http.request(requestOpts, function(res){

    console.log("HTTP Response -----------------", new Date());
    console.log("[" + requestOpts.method+ "]", requestOpts.path);
    console.log('Response Status: ', res.statusCode);
    console.log('Response Headers: \n', res.headers);

    res.setEncoding(RestClient.Encoding);

    var resData = "";
    res.on('data', function(chunk){
      //console.log("Data Receive...\n");
      resData += chunk;
    });
    res.on('end', function(){
      console.log("Receive Data:\n", resData);
      console.log("-------------------------------------------------------");

      RestClient.CallBack(res, resData, cb);
    });
  });

  request.on("error", function(e){
    console.log("Request Error:\n" + e.message);
    RestClient.CallBack(null, e, cb);
  });

  if(data != null && typeof(data) != "undefined"){
    request.write(data);
  }

  request.end();
};

RestClient.CallBack = function(res, data, cb){
  var result = new APIResultModel();
  if(res != null){
    result.StatusCode = res.statusCode;
    result.Headers = res.headers;
  }else{
    result.StatusCode = -1;
    result.Headers = null;
  }
  result.Data = data;

  cb(result);
};

module.exports = RestClient;