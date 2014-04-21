/**
 * Created by Haiwei on 2014/4/21.
 */
var http = require("http");
var fs = require("fs");
var path = require("path");

Init( process.argv);

http.createServer(function(req, res){
    var reg = /[.](js|html)$/
    var url = req.url;
    if(reg.test(url)){
        WriteFile(req, res);
    }else{
        WriteJSON(req, res);
    }
}).listen(process.env.PORT, function(){
    console.log("Server running at port " + process.env.PORT);
});

function Init(args){
    if(args.length > 2 && args[2].toLowerCase() == 'prod'){
        Configuration.Init('prod');
        envConfig = require('./prod');
        process.env.NAME = "production";
        console.log('Application environment is Production.');
    }else{
        Configuration.Init('dev');
        envConfig = require('./dev');
        process.env.NAME = "development";
        console.log('Application environment is Development.');
    }

    GlobalConfig = Utils.FillObject(GlobalConfig, envConfig, 0);
    global.AppConfig = Utils.FillObject(global.AppConfig, GlobalConfig, 0);

    process.env.PORT = global.AppConfig.ListenOnPort;
}

function WriteFile(req, res){
    var filePath = "." + req.url; // ./lib/utils.js
    var extname = path.extname(filePath);

    fs.readFile(filePath, function(err, data){
        if(err){
            res.writeHead(404);

            console.log(req.method, 404, req.url);
            res.end();
        }

        var contentType = "text/html";
        switch(extname.trim()){
            case ".js":
                contentType = "application/x-javascript";
        }

        res.writeHead(200,
            {
                "Content-Type": contentType + "; charset=utf-8",
                "Cache-Control": "max-age=3600"
            });

        console.log(req.method, 200 , req.url);
        res.end(data);
    });
}

function WriteJSON(req, res){
    res.writeHead(200, {'Content-Type': 'text/javascript; charset=UTF-8'});

    res.end('JSON');
}