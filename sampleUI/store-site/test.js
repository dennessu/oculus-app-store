var Config = require('./configs/config');



var testObj = {
    Arr: ["1", "2", "3"],
    Obj: {
        A: "aa",
        B: "bb"
    },
    APIs: {
        Identity: null
    }
};

console.log(Utils.FillObject(testObj, Config, 2));
Config.APIs = null;
console.log(testObj);
console.log(Config);

