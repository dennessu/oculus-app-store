/**
 * Created by Haiwei on 2014/4/17.
 */

var path = require("path");
var fs = require("fs");

var str = "/test/test.js";
var str2 = "/test/config";

var reg = /[.](js|gif|bmg)$/

//console.log(reg.test(str));

//console.log(path.normalize(str));

//console.log(fs.realpathSync("/test/test.js"));

console.log(fs.existsSync("./test/index.html"));

