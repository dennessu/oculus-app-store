
var user = require('./user_model');
var profile = require('./profile_model');
var authenticate = require('./authenticate_model');
var code = require('./code_model');
var optIn = require('./opt_in');

exports.UserModel = user;
exports.ProfileModel = profile;
exports.AuthenticateModel = authenticate;
exports.CodeModel = code;
exports.OptInModel = optIn;
