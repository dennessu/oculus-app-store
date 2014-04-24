(function(){
    var Utils = {
        /* Deep Clone */
        Clone: function (sObj) {
            if (typeof sObj !== "object") return sObj;

            var s = {};
            if (sObj.constructor == Array) s = [];

            for (var i in sObj) s[i] = Utils.Clone(sObj[i]);

            return s;
        },

        /*
         Fill original object use target object
         @type: 0:full(if property not exists, add a new property), 1:ChildFull(child properties is full fill), 2:OneWay(fill original from target just have properties)
         @return: original object(reference).
         */
        FillObject: function (original, target, type) {

            if (original == undefined || original == null) {
                original = Utils.Clone(target);
                return original;
            }

            // fill exists properties
            for (var p in original) {
                var p_type = typeof(original[p]);

                if (p_type == "object") {
                    if (typeof(target[p]) != "undefined") {
                        if (type == 1) {
                            original[p] = Utils.Clone(target[p]);
                        } else {
                            original[p] = Utils.FillObject(original[p], target[p], type);
                        }
                    }
                } else {
                    // base type、Array or function
                    if (typeof(target[p]) != "undefined") {
                        original[p] = Utils.Clone(target[p]);
                    }
                }
            }

            // add new properties
            if (type == 0) {
                for (var p in target) {
                    var p_type = typeof(target[p]);
                    if (typeof(original[p]) != "undefined") continue;
                    original[p] = Utils.Clone(target[p]);
                }
            }

            return original;
        },

        IsEmpty: function(obj){
            return typeof(obj) === "undefined" || obj === null || obj === "";
        },

        Format: {
            /* Starting from 1 */
            StringFormat: function(){
                if (arguments.length > 0) {
                    var result = arguments[0];
                    if (arguments.length == 1 && typeof (args) == "object") {
                        for (var key in args) {
                            if (args[key] != undefined) {
                                var reg = new RegExp("({" + key + "})", "g");
                                result = result.replace(reg, args[key]);
                            }
                        }
                    }
                    else {
                        for (var i = 0; i < arguments.length; i++) {
                            if (arguments[i] != undefined) {
                                var reg = new RegExp("({)" + i + "(})", "g");
                                result = result.replace(reg, arguments[i]);
                            }
                        }
                    }
                    return result;
                }
                return null;
            },

            DateFormat: function (date, fmt) { //author: meizz
                var o = {
                    "M+": date.getMonth() + 1, //月份
                    "d+": date.getDate(), //日
                    "h+": date.getHours(), //小时
                    "m+": date.getMinutes(), //分
                    "s+": date.getSeconds(), //秒
                    "q+": Math.floor((date.getMonth() + 3) / 3), //季度
                    "S": date.getMilliseconds() //毫秒
                };
                if (/(y+)/.test(fmt))
                    fmt = fmt.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
                for (var k in o)
                    if (new RegExp("(" + k + ")").test(fmt))
                        fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));

                return fmt;
            },

            FormatNumber: function (number, fix, fh, jg){
                var fix = arguments[1] ? arguments[1] : 2;
                var fh = arguments[2] ? arguments[2] : ',';
                var jg = arguments[3] ? arguments[3] : 3;
                var str = '';
                number = number.toFixed(fix);
                var zsw = number.split('.')[0];
                var xsw = number.split('.')[1];
                var zswarr = zsw.split('');
                for (var i = 1; i <= zswarr.length; i++) {
                    str = zswarr[zswarr.length - i] + str;
                    if (i % jg == 0) {
                        str = fh + str;
                    }
                }
                str = (zsw.length % jg == 0) ? str.substr(1) : str;
                zsw = str + '.' + xsw;
                return zsw;
            }
        },

        //[node]
        Cookies: {
            Set: function (name, value) {
                var Days = 30;
                var exp = new Date();
                exp.setTime(exp.getTime() + Days * 24 * 60 * 60 * 1000);
                document.cookie = name + "=" + escape(value) + ";expires=" + exp.toGMTString();
            },
            // return all cookies in object
            GetAll: function(){
                var result = {};
                if (document.cookie != undefined && document.cookie != null && document.cookie != "") {
                    if (document.cookie.indexOf(";") != -1) {
                        var cookieArr = document.cookie.split(";");
                        for (var i = 0; i < cookieArr.length; ++i) {
                            var item = cookieArr[i].split("=");
                            if(item.length > 1){
                                result[item[0].trim()] = unescape(item[1].trim());
                            }else{
                                result[item[0].trim()] = "";
                            }
                        }
                    } else {
                        var item = document.cookie.split("=");
                        if(item.length > 1){
                            result[item[0].trim()] = unescape(item[1].trim());
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
                    return unescape(arr[2].trim()).trim();
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

        QueryString: {
            //[node]
            SaveQueryArray: function (req, res, queryStringArr) {
                if (queryStringArr == null || typeof(queryStringArr) == "undefined" || queryStringArr.length == 0) return false;

                for (var i = 0; i < queryStringArr.length; ++i) {
                    if (req.query[queryStringArr[i]]) {
                        res.cookie(queryStringArr[i], req.query[queryStringArr[i]], { maxAge: global.AppConfig.CookiesTimeout, domain: '', path: '/', secure: false });
                    }
                }
                return true;
            },

            //[node]
            ClearQueryArray: function (res, queryStringArr) {
                if (queryStringArr == null || typeof(queryStringArr) == "undefined" || queryStringArr.length == 0) return false;

                for (var i = 0; i < queryStringArr.length; ++i) {
                    res.clearCookie(queryStringArr[i], { path: '/'});
                }
                return true;
            },

            //[browser]
            GetArray: function () {
                var result = location.search.match(new RegExp("[\?\&][^\?\&]+=[^\?\&]+", "g"));
                if (result == null) return [];
                for (var i = 0; i < result.length; i++) {
                    result[i] = result[i].substring(1);
                }
                return result;
            },

            //[browser]
            Get: function (name) {
                var result = location.search.match(new RegExp("[\?\&]" + name + "=([^\&]+)", "i"));
                if (result == null || result.length < 1) return null;
                return result[1];
            }
        },

        // [browser] Need Ember
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

        // [browser]
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

    if(typeof(window) != "undefined"){
        Module.Load(this, "Utils", Utils);
    }else{
        module.exports = Utils;
    }
}).call(this);
