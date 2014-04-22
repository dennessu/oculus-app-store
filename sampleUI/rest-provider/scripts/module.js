/**
 * Created by Haiwei on 2014/4/17.
 */

/* Only use browser */
var Module = {
    /*
        Load object to window according to namespace
        namespace: "com.aa.name"
     */
    Load: function(parent, namespace, v){
        if(parent == undefined || parent == null) parent = {};
        if(namespace == undefined || namespace == null) return;

        //console.log("Register: ", namespace);

        if(namespace.indexOf('.') == -1){
            parent[namespace] = v;
        }else{
            var len = namespace.indexOf('.');
            var name = namespace.substr(0, len);
            namespace = namespace.substr(len + 1, namespace.length - len -1);

            if(typeof(parent[name]) == "undefined") {
                parent[name] = {};
            }

            Module.Load(parent[name], namespace, v);
        }
    }
};