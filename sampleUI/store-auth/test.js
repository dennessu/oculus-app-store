

var str = "ASDF";

var re = ".*[A-Z]+.*";
var reg = new RegExp(re,"g");

console.log(reg.test(str));