var Utils = {
    MoreFilling: function (original, target) {
        for (var p in original) {
            var p_type = typeof(original[p]);

            if (p_type != "function") {
                if (p_type == "object") {
                    if (typeof(target[p]) != "undefined") {
                        original[p] = Utils.MoreFilling(original[p], target[p]);
                    }
                } else {
                    if (typeof(target[p]) != "undefined") {
                        original[p] = target[p];
                    }
                }
            }
        }

        // Append new property
        for (var p in target) {
            var p_type = typeof(target[p]);

            if (p_type != "function") {
                if (typeof(original[p]) != "undefined") continue;
                original[p] = target[p];
            }
        }

        return original;
    },

    GetViews: function (templateObj) {
        if (templateObj == undefined || templateObj == null) {
            throw "The template not exists!";
        }

        if (Ember.TEMPLATES[templateObj.name] == undefined || Ember.TEMPLATES[templateObj.name] == null) {
            console.log("Request Template: ", templateObj.name);

            $.ajax({
                url: templateObj.url,
                async: false,
                success: function (data, textStatus, jqXHR) {
                    Ember.TEMPLATES[templateObj.name] = Ember.Handlebars.compile(data);
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    throw errorThrown;
                }
            });
        }
        return Ember.TEMPLATES[templateObj.name];
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
    }

};