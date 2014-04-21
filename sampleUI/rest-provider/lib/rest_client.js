/* Only use Node */

var http = require("http");
var QueryString = require("querystring");
var Utils = require('./utils');
var ResultModel = require("./result_model");

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

RestClient.prototype.Request = function(options, data, cb){

  console.log("HTTP Request --------------------", new Date());
  console.log("Request Options:\n", options);
  console.log("Request Data:\n", JSON.stringify(data));

  // Fill options
  var requestOpts = RestClient.GetDefaultOptions();
  requestOpts = Utils.FillObject(requestOpts, options, 0);

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
  var result = new ResultModel();
  if(res != null){
    result.status = res.statusCode;
    result.headers = res.headers;
  }else{
    result.status = -1;
    result.headers = null;
  }
  result.data = data;

  cb(result);
};

module.exports = RestClient;