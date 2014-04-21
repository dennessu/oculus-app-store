/**
 * Created by Haiwei on 2014/4/21.
 */
var Configuration = require('rest-provider').Configuration;
var Utils = require('rest-provider').Utils;

module.exports = function(app){

    app.all('*', function(req, res, next){
        Utils.QueryString.SaveQueryArray(req, res, global.AppConfig.SaveQueryStrings);
        res.header("PID:", process.pid);

        next();
    });

    app.route('/').get(function(req, res){
        res.render('index', {title: "Passport"});
    });

    app.route('/config').get(function(req, res){

        var clientConfig = Configuration.GetClientConfigs();

        res.json(200, clientConfig);
        //res.end();
    });
};