var Utils = {
    /*
     Fill original object use target object
     @type: 0:full, 1:ChildFull, 2:OneWay
     @return: original object
     */
    FillObject: function (original, target, type) {
        if (type == 0 && (original == undefined || original == null)) return target;

        for (var p in original) {
            var p_type = typeof(original[p]);

            if (p_type != "function") {
                if (p_type == "object") {
                    if (typeof(target[p]) != "undefined" && target[p] != null) {
                        if (type == 1) {
                            original[p] = this.FillObject(original[p], target[p], 0);
                        } else {
                            original[p] = this.FillObject(original[p], target[p], type);
                        }
                    }
                } else {
                    if (typeof(target[p]) != "undefined" && target[p] != null) {
                        original[p] = target[p];
                    }
                }
            }
        }

        if (type == 0) {
            // Append new property
            for (var p in target) {
                var p_type = typeof(target[p]);

                if (p_type != "function") {
                    if (typeof(original[p]) != "undefined" && original[p] != null) continue;
                    original[p] = target[p];
                }
            }
        }

        return original;
    },

    // {1} is {2}
    Format: function(str, args) {
    var result = str;
    if (arguments.length > 0) {
        if (arguments.length == 1 && typeof (args) == "object") {
            for (var key in args) {
                if(args[key]!=undefined){
                    var reg = new RegExp("({" + key + "})", "g");
                    result = result.replace(reg, args[key]);
                }
            }
        }
        else {
            for (var i = 0; i < arguments.length; i++) {
                if (arguments[i] != undefined) {
                    var reg= new RegExp("({)" + i + "(})", "g");
                    result = result.replace(reg, arguments[i]);
                }
            }
        }
    }
    return result;
    },

    GetViews: function (templateName) {
        templateName = templateName.toLowerCase();
        if (Ember.isEmpty(templateName)) {
            return;
        }

        if (Ember.isEmpty(Ember.TEMPLATES[templateName])) {

            var tempUrl = "";
            for (var c in AppConfig.Templates) {
                for (var s in AppConfig.Templates[c]) {
                    if (AppConfig.Templates[c][s].toLowerCase() === templateName) {
                        tempUrl = Utils.Format("/templates/{1}/{2}", c, s);
                        break;
                    }
                }
            }

            if (Ember.isEmpty(tempUrl)) throw "The template not exists!";

            console.log("Get Template: ", templateName);

            $.ajax({
                url: tempUrl,
                async: false,
                success: function (data, textStatus, jqXHR) {
                    Ember.TEMPLATES[templateName] = Ember.Handlebars.compile(data);
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    throw errorThrown;
                }
            });
            console.log("Get Template: ", templateName, " done");
        }

        return Ember.TEMPLATES[templateName];
    },

    GetProperty: function(obj, propertyName){
        for (var p in obj) {
            if(p == propertyName)
                return obj[p];
        }
        return null;
    },

    Cookies: {
        Set: function (name, value) {
            var Days = 30;
            var exp = new Date();
            exp.setTime(exp.getTime() + Days * 24 * 60 * 60 * 1000);
            document.cookie = name + "=" + escape(value) + ";expires=" + exp.toGMTString();
        },
        // return cookies array
        GetAll: function(){
            var result = {};
            if (document.cookie != undefined && document.cookie != null && document.cookie != "") {
                if (document.cookie.indexOf(";") != -1) {
                    var cookieArr = document.cookie.split(";");
                    for (var i = 0; i < cookieArr.length; ++i) {
                        var item = cookieArr[i].split("=");
                        if(item.length > 1){
                            result[item[0].trim()] = item[1].trim();
                        }else{
                            result[item[0].trim()] = "";
                        }
                    }
                } else {
                    var item = document.cookie.split("=");
                    if(item.length > 1){
                        result[item[0].trim()] = item[1].trim();
                    }else{
                        result[item[0].trim()] = "";
                    }
                }
            }
            return result;
        },
        Get: function (name) {
            var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
            if (arr = document.cookie.match(reg))
                return unescape(arr[2]);
            else
                return null;
        },
        Remove: function (name) {
            var exp = new Date();
            exp.setTime(exp.getTime() - 1);
            var cval = Utils.Cookies.Get(name);
            if (cval != null)
                document.cookie = name + "=" + cval + ";expires=" + exp.toGMTString();
        }
    },

    SettingHandler: function(settingModels){
        if(settingModels !== undefined && settingModels != null && settingModels != ""){
            for(var i  = 0; i < settingModels.length; ++i){
                var item = settingModels[i];
                if(item.type == AppConfig.SettingTypeEnum.Cookie){
                    Utils.Cookies.Set(item.data.name, item.data.value);
                }
            }
        }
    },

    GetErrorMessage: function(resultModel){
        var resultMessage = "Please try again later!";

        if(resultModel.status == 1000){
            // api throw error
            try{
                var resObj = JSON.parse(resultModel.data);
                if(typeof(resObj['data']) != "undefined"){
                    var errObj = JSON.parse(resObj.data);
                    if(typeof(errObj['description']) != "undefined"){
                        resultMessage = errObj['description'];
                    }
                }
            }catch(e){}

        }else if(resultModel.status == 2000){
            // code throw exception
        }

        return resultMessage;
    },

    /*
        Generate a request model according to arguments
        @arguments: 0:Data, 1:query, 2:cookies
     */
    GenerateRequestModel: function(){

        var requestModel = new RequestDataModel();
        if(arguments.length > 0) requestModel.data = arguments[0];
        if(arguments.length > 1) requestModel.query = arguments[1];
        requestModel.cookies = Utils.Cookies.GetAll();

        return requestModel;
    }

};