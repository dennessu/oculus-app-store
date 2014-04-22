
var ResultModel = function(){
  this.status = 404; // response status code
  this.headers = ""; // response headers(Use node)
  this.data = ""; // response data
};

if(typeof(window) != "undefined"){
    Module.Load(window, "ResultModel", ResultModel);
}else{
    module.exports = ResultModel;
}