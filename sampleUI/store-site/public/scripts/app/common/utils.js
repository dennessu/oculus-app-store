var Utils = {

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
                            result[item[0]] = item[1];
                        }else{
                            result[item[0]] = "";
                        }
                    }
                } else {
                    var item = document.cookie.split("=");
                    if(item.length > 1){
                        result[item[0]] = item[1];
                    }else{
                        result[item[0]] = "";
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
    }
};