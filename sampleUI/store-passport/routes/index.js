/**
 * Created by Haiwei on 2014/4/21.
 */
var Configuration = require('rest-provider').Configuration;
var Utils = require('rest-provider').Utils;
var Rest = require('./rest');

module.exports = function(app){

    // Filter
    app.all('*', function(req, res, next){
        Utils.QueryString.SaveQueryArray(req, res, global.AppConfig.SaveQueryStrings);
        res.header("PID:", process.pid);

        next();
    });

    app.route('/').get(function(req, res){

        var identityRest = global.AppConfig.RestConfigs.Identity;

        var authUrl = Utils.Format.StringFormat("{1}{2}{3}",
                                    global.AppConfig.OauthHost,
                                    identityRest.Config.namespace,
                                    identityRest.PostAuthorize.Options.path);

        res.render('index',
            {
                title: "Passport",
                authUrl: authUrl
            }
        );
    });

    app.route('/config').get(function(req, res){
        var clientConfig = Configuration.GetClientConfigs();

        res.json(200, clientConfig);
        //res.end();
    });

    // rest api
    app.route('/api/login').post(Rest.Login);
};