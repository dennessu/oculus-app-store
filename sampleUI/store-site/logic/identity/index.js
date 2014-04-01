
var Login = require('./login');
var Captcha = require('./captcha');
var TFA = require('./tfa');
var Register = require('./register');
var PIN = require('./pin');
var GetAnonymousUser = require('./get_anonymous_user');
var Profile = require('./profile');

exports.Login = Login;
exports.Captcha = Captcha;
exports.PostTFA = TFA;
exports.Register = Register;
exports.PostPIN = PIN;
exports.GetAnonymousUser = GetAnonymousUser;
exports.GetPayinProfiles = Profile.GetPayinProfiles;
exports.PutProfile = Profile.PutProfile;
exports.GetOptIns = Profile.GetOptIns;
exports.PostOptIns = Profile.PostOptIns;
exports.RestPassword = Profile.RestPassword;
