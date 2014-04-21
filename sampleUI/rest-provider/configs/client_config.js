/**
 * Created by Haiwei on 2014/4/21.
 */

(function(){
    var Configs = {

        CookiesTimeout: 600 * 60 * 1000,
        Feature: null,
        CookiesName: null,
        QueryStrings: null,
        //SaveQueryStrings: null,
        //SettingTypeEnum: null,
        UrlConstants: null,
        Runtime: null,

        // hard code
        PaymentTypes: null,
        CardTypes: null,
        PaymentHolderTypes: null,
        ShippingMethods: null,
        Countries: null,

        // API Data field and other site post filed
        FieldNames: null,

        /*
         Rules:{
         is_full_path: true  //When rest api return 302 in location url
         }
         Arguments: {
         options: 0,     //Reserved Keywords
         data: 1,        //Reserved Keywords
         cb: 2           //Reserved Keywords
         }
         */
        RestConfigs: {
            Identity: null
        }
    };

    if(typeof(window) != "undefined"){
        Module.Load(this, "AppConfig", Configs);
    }else{
        module.exports = Configs;
    }
}).call(this);