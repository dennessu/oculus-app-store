
var DataProvider = function(apiConfig){

    if(this._instance !== undefined && this._instance !== null){
        return this._instance;
    }

    var config = apiConfig;
    this._isSocket = true; // false: XMLHttpRequest

    // TODO: Check support web socket

    if(this._isSocket){
        this._conn = io.connect(config.Socket); //, {'reconnect':false,'auto connect':false});
        this._conn.on('connect', function () {
            console.log("Connected to " + config.Socket);
        });
        this._conn.on("welcome", function(data){
            console.log("[Event: welcome]", data);
        });
        //this._conn.socket.connect();
    }else{
        throw "Can't support XMLHttpRequest now!";
    }

    this._instance = this;
};

DataProvider.prototype.Discount = function(){
    this._conn.emit("discount");
    this._conn.disconnect();
};

DataProvider.prototype.Emit = function(api, data, cb){
    /*
    api:{
     async: false,
     namespace: "/api/identity/login"
     }
    data:{
        body: {},
        cookies:{}
    }
    */

    if(this._isSocket){
        console.log("[Emit: "+ api.namespace +"]: ", data);
        this._conn.emit(api.namespace, data, function(data){
            console.log("[Emit: "+ api.namespace +": Callback]: ", data);
            cb(data);
        });
    }else{

    }
};
