define([
    'jquery'
],function($){

    var Cookies = {
        Set: function(name,value)
        {
            var Days = 30;
            var exp  = new Date();
            exp.setTime(exp.getTime() + Days*24*60*60*1000);
            document.cookie = name + "="+ escape (value) + ";expires=" + exp.toGMTString();
        },
        Get: function(name)
        {
            var arr = document.cookie.match(new RegExp("(^| )"+name+"=([^;]*)(;|$)"));
            if(arr != null) return unescape(arr[2]); return null;
        },
        Remove: function(name)
        {
            var exp = new Date();
            exp.setTime(exp.getTime() - 1);
            var cval= Cookies.Get(name);
            if(cval!=null) document.cookie= name + "="+cval+";expires="+exp.toGMTString();
        }
    };

    return Cookies;
});