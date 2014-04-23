var C = require('../config');
var Utils = require('./utils');

module.exports = function (app) {

    app.get("/", function (req, res) {
        res.render("index", {title: "Rest Service"});
    });
    app.get("/rest/oauth2/authorize", function (req, res) {
        var redirect_url = req.query["redirect_uri"];

        res.redirect("http://localhost:3100/Identity?event=TestEvent&cid=12345&redirect_url="+redirect_url+"?code=123");
        res.end();
    });

    /*
    app.post("/rest/oauth2/authorize", function (req, res) {
        var redirect_url = "http://localhost:3100/callback/login?code=1234";
        res.redirect(redirect_url);
        res.end();
    });
    */


    app.get("/rest/oauth2/end-session", function (req, res) {
        var redirect_url = req.query["post_logout_redirect_uri"];
        res.redirect(redirect_url);
        res.end();
    });


    for (var p in C) {
        var gateway = C[p];

        for (var a in gateway) {
            var action = gateway[a];

            try {
                if (typeof(action["Path"]) != "undefined" && typeof(action["Method"]) != "undefined") {
                    app[action.Method.toLowerCase()](action.Path, Utils.RequestHandle);

                    console.log("Append Path: ["+ action.Method +"] " + action.Path);

                } //end if
            } catch (e) {
                console.log(e);
            }

        } // end for
    } // end for
}