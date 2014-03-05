var C = require('../config');

var Utils = {
    RequestHandle: function (req, res) {
        var path = req.path;

        for (var p in C) {
            var gateway = C[p];

            for (var a in gateway) {
                var action = gateway[a];
                if (typeof(action["Path"]) != "undefined") {
                    if (Utils.CheckPath(path, action.Path)) {
                        Utils.ParseResponse(action, req, res);
                        return;
                    }
                }
            }
        }

        res.write("Can't find the path of the match");
        res.statusCode = 404;
        res.end();
    },

    ParseResponse: function (configActionObj, req, res) {

        var obj = configActionObj;
        var resObj = obj.Items[obj.ResponseItem];

        if (resObj != undefined) {
            res.statusCode = resObj.statusCode;

            if (resObj.headers != null) {
                for (var p in resObj.headers) {
                    if (typeof(resObj.headers[p]) != "undefined")  res.setHeader(p, resObj.headers[p]);
                }
            }

            if (resObj.data != null) {
                res.write(JSON.stringify(resObj.data));
            }
        } else {
            res.statusCode = 500;
            res.write("Can't found Response Item.");
        }

        res.end();
    },

    CheckPath: function (reqPath, orgPath) {
        if (reqPath == undefined || reqPath == null || reqPath == "") return true; // ignore
        if (orgPath == undefined || orgPath == null || orgPath == "") return true; // ignore

        if (orgPath.indexOf(":") > -1) {

            var paths = orgPath.split('/');
            var reqPaths = reqPath.split('/');

            if (paths.length != reqPaths.length) return false;

            for (var i = 0; i < paths.length; ++i) {
                if (paths[i].indexOf(":") > -1) {
                    continue;
                } else {
                    if (paths[i].toLowerCase() != reqPaths[i].toLowerCase()) return false;
                }
            }

            return true;
        } else {
            return reqPath.toLowerCase() === orgPath.toLowerCase();
        }
    }
};

module.exports = Utils;