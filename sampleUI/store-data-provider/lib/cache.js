/**
 * Created by Haiwei on 2014/5/4.
 */

exports.CacheTypes = {
    OFFERS:{
        key: "offers",
        expires: 5    // minutes
    }
};

exports.Cache = function(){

    this.EXPIRES_KEY = "cache_expires";

    this._SetExpires = function(cacheType){
        // update expires time
        if(typeof(global[this.EXPIRES_KEY]) == "undefined"){
            global[this.EXPIRES_KEY] = {};
        }
        global[this.EXPIRES_KEY][cacheType.key] = new Date();
    };

    // return bool
    this._IsExpires = function(cacheType){
        if(typeof(global[this.EXPIRES_KEY]) != "undefined"
            && typeof(global[this.EXPIRES_KEY][cacheType.key]) != "undefined") {

            var oldVersion = global[this.EXPIRES_KEY][cacheType.key];
            if(parseInt(((new Date()).getTime() - oldVersion.getTime())/1000/60) > cacheType.expires){
                return true;
            }
        }

        return false;
    };

    this.AddArray = function(cacheType, keyFieldName, objArray){
        if(typeof(global[cacheType.key]) == "undefined"){
            global[cacheType.key] = {};
        }

        for(var i = 0; i < objArray.length; ++i){
            var itme = objArray[i];
            var key = itme[keyFieldName];

            global[cacheType.key][key] = itme;
        }

        this._SetExpires(cacheType);
    };

    this.Add = function(cacheType, key, v){

        if(typeof(global[cacheType.key]) == "undefined"){
            global[cacheType.key] = {};
        }
        var itemCache = global[cacheType.key];
        itemCache[key] = v;

        this._SetExpires(cacheType);
    };

    this.GetArray = function(cacheType){
        if(typeof(global[cacheType.key]) == "undefined"){
            console.log(cacheType.key, " is undefined! ***********************");
            return null;
        }

        if(this._IsExpires(cacheType)){
            console.log(cacheType.key, " is expired! ***********************");
            global[cacheType.key] = undefined;
            return null;
        }

        var result = new Array();
        for(var p in global[cacheType.key]){
            result.push(global[cacheType.key][p]);
        }

        return result;
    };

    this.Get = function(cacheType, key){
        if(typeof(global[cacheType.key]) == "undefined"
            || typeof(global[cacheType.key][key]) == "undefined"){
            return null;
        }

        if(this._IsExpires(cacheType)){
            global[cacheType.key][key] = undefined;
            return null;
        }

        return global[cacheType.key][key];
    };

    this.Clear = function(cacheType){
        if(typeof(global[cacheType.key]) == "undefined"){
            return;
        }

        global[cacheType.key] = undefined;
    }
};