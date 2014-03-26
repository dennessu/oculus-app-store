
var Login = require('./login');
var Captcha = require('./captcha');
var TFA = require('./tfa');
var Register = require('./register');
var PIN = require('./pin');
var GetAnonymousUser = require('./get_anonymous_user');
var Profile = require('./profile');

exports.Login = Login;
exports.Captcha = Captcha;
exports.TFA = TFA;
exports.Register = Register;
exports.PIN = PIN;
exports.GetAnonymousUser = GetAnonymousUser;
exports.GetProfile = Profile.GetProfile;
exports.PutProfile = Profile.PutProfile;
exports.GetOptIns = Profile.GetOptIns;
exports.PostOptIns = Profile.PostOptIns;
exports.PutUser = Profile.PutUser;
