var Utils = require('../utils/utils');
var ClientConfigs = require('../configs/client_config');
var Auth = require('./auth');

module.exports = function(app){

    app.get("/", function(req, res){
        res.render("index",
            {
                title: "Customer Service",
                loginUrl: global.Config.Runtime.LoginUrl
            });
    });

    // Config
    app.get('/config', function(req, res){
        ClientConfigs = Utils.FillObject(ClientConfigs, global.Config, 1);

        res.json(200, ClientConfigs);
    });

    app.get('/Callback/Login', Auth.Login);
    app.get('/Callback/Register', Auth.Register);
    app.get('/Callback/Logout', Auth.Logout);

}