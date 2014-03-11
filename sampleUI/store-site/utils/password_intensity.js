

var PasswordIntensity = function(){};

PasswordIntensity.prototype.STAR = "*";
PasswordIntensity.prototype.ANY_STRING_PATTERN = ".*";
PasswordIntensity.prototype.PLUS = "+";
PasswordIntensity.prototype.NOT_ALLOWED_PATTEN = "[ ]";
PasswordIntensity.prototype.UPPER_ALPHA_PATTEN = "[A-Z]";
PasswordIntensity.prototype.LOWER_ALPHA_PATTEN = "[a-z]";
PasswordIntensity.prototype.ALPHA_PATTEN = "[A-Za-z]";
PasswordIntensity.prototype.NUMBER_PATTEN = "[0-9]";
PasswordIntensity.prototype.SPECIAL_CHARACTER_PATTEN = "[`~!@#$%^&*()+=|{}\\':;\\',//[//].<>/\" +\n?]";
PasswordIntensity.prototype.IntensityEnum = {
    STRONG: 'STRONG',
    FAIR: 'FAIR',
    WEAK: 'WEAK'
};


PasswordIntensity.prototype._match = function(s, re){
    return s.match(re) > -1;
};

PasswordIntensity.prototype._isUpperAlphaContain = function(s) {
    return PasswordIntensity._match(s, PasswordIntensity.ANY_STRING_PATTERN + PasswordIntensity.UPPER_ALPHA_PATTEN + PasswordIntensity.PLUS + PasswordIntensity.ANY_STRING_PATTERN);
};

PasswordIntensity.prototype._isLowerAlphaContain = function(s) {
    return PasswordIntensity._match(s, PasswordIntensity.ANY_STRING_PATTERN + PasswordIntensity.LOWER_ALPHA_PATTEN + PasswordIntensity.PLUS + PasswordIntensity.ANY_STRING_PATTERN);
};

PasswordIntensity.prototype._isSpecialCharacterContain = function(s) {
    return PasswordIntensity._match(s, PasswordIntensity.ANY_STRING_PATTERN + PasswordIntensity.SPECIAL_CHARACTER_PATTEN + PasswordIntensity.PLUS + PasswordIntensity.ANY_STRING_PATTERN);
};

PasswordIntensity.prototype._isNumberContain = function(s) {
    return PasswordIntensity._match(s, PasswordIntensity.ANY_STRING_PATTERN + PasswordIntensity.NUMBER_PATTEN + PasswordIntensity.PLUS + PasswordIntensity.ANY_STRING_PATTERN);
};
PasswordIntensity.prototype._getPasswordScore = function(s){
    var score = 0;

    if(this._isNumberContain(s)) {
        score += 2;
    }
    if(this._isLowerAlphaContain(s)) {
        score += 2;
    }
    if(this._isUpperAlphaContain(s)) {
        score += 2;
    }
    if(this._isSpecialCharacterContain(s)) {
        score += 1;
    }

    return score;
};

PasswordIntensity.prototype.GetIntensity = function(s){

    if(s.length() < 4 || s.length() > 16) {
        return this.IntensityEnum.WEAK;
    }

    var score = this._getPasswordScore(s);

    if(score >= 6) {
        return this.IntensityEnum.STRONG;
    }
    else if (score >= 3) {
        return this.IntensityEnum.FAIR;
    }
    else {
        return UserPasswordStrength.WEAK;
    }
};

module.exports = PasswordIntensity;





