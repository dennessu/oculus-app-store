var Utils = require('../../utils/utils');

module.exports = function (req, res, next) {

    Utils.SaveQueryStrings(req, res, process.AppConfig.SaveQueryStringArray);

    res.setHeader("PID:", process.pid);
    next();
};