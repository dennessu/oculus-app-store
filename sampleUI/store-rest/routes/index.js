var C = require('../config');
var Utils = require('./Utils');

module.exports = function (app) {

    app.get("/", function (req, res) {

        res.render("index", {title: "Redirect Test"});
    });
    app.get("/rest/authorize", function (req, res) {

        res.redirect("http://localhost:3000/identity?fs=12345");
        res.end();
    });

    for (var p in C) {
        var gateway = C[p];

        for (var a in gateway) {
            var action = gateway[a];

            try {
                if (typeof(action["Path"]) != "undefined" && typeof(action["Method"]) != "undefined") {
                    switch (action.Method.toUpperCase()) {
                        case "GET":
                            app.get(action.Path, Utils.RequestHandle);
                            break;
                        case "POST":
                            app.post(action.Path, Utils.RequestHandle);
                            break;
                    }

                    console.log("Append Path: ["+ action.Method +"] " + action.Path);

                } //end if
            } catch (e) {
                console.log(e);
            }

        } // end for
    } // end for
}