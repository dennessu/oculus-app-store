/**
 * Created by Haiwei on 2014/4/21.
 */

/* [browser] */
var AppConfig = function () {};

AppConfig.Init = function(){
    console.log("AppConfig.Init");
    $.ajax({
        type: "GET",
        url: "/config",
        async: false,
        success: function (data, textStatus) {
            AppConfig = Utils.FillObject(AppConfig, data, 0);
            console.log(AppConfig.CookiesTimeout);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            console.log("Get Client Config Failed!");
        }
    });
};
AppConfig.Init();