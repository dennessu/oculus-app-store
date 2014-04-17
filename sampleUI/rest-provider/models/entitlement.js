/**
 * Created by Haiwei on 2014/4/16.
 */

var EntitlementModel = function(){
    this.user = {
        "href": "https://data.oculusvr.com/v1/users/123",
        "id": 1234444
    };
    this.developer = {
        "href": "https://data.oculusvr.com/v1/users/123",
        "id": 1234
    };

    this.type = "developer";
};

if(typeof(window) != "undefined"){
    Module.Load(window, "Models.Entitlement.EntitlementModel", EntitlementModel);
}else{
    exports.EntitlementModel = EntitlementModel;
}
