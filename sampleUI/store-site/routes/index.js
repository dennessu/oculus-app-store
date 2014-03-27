var Utils = require('../utils/utils');
var ClientConfigs = require('../configs/client_config');
var Templates = require('./template');
var Auth = require('./auth');


module.exports = function(app){

    /*
        Application
        1.Store
        2.Auth
    */

    // Application
    app.get('/', function(req, res){
        res.render("index",
            {
                layout: false,
                title: "Store Demo",
                loginUrl: process.AppConfig.Urls.GetLoginUrl(req),
                registerUrl: process.AppConfig.Urls.GetRegisterUrl(req)
            });
    });
    app.get('/Identity', function(req, res){
        res.render("identity/index", {layout: false, title: "Store Demo"});
    });

    // Config
    app.get('/config', function(req, res){
        ClientConfigs["LoginUrl"] = process.AppConfig.Urls.GetLoginUrl(req);

        ClientConfigs = Utils.FillObject(ClientConfigs, process.AppConfig, 1);

        res.json(ClientConfigs);
        res.end();
    });

    // Templates
    for(var c in process.AppConfig.Templates){
        for(var s in process.AppConfig.Templates[c]){
            app.get(Utils.Format("/Templates/{1}/{2}", c, s), Templates[c][s]);
        }
    }

    // Redirect back handler
    app.get('/Callback/Login', Auth.Login);
    app.get('/Callback/Register', Auth.Register);
    app.get('/Logout', Auth.Register);

    // API Rest

};