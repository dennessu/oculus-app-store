
var CustomAdapter = DS.RESTAdapter.extend({

    find: function(store, type, id) {
        var url = type.url;

        return new Ember.RSVP.Promise(function(resolve, reject) {

            var configItem = Utils.GetProperty(AppConfig.DataModelMapTable, type);
            if(configItem == null){
                throw "Can't found mapper for " + type;
            }
            var provider = new window[configItem.Provider];
            provider[configItem.Method](Utils.GenerateRequestModel({id: id}), function(receive){
                if(receive.data.status == 200){
                    var rData = Transition.Resolve(type, "find", receive.data.data);
                    Ember.run(type, resolve, rData);
                }else{

                }
            });
        });
    },
    findAll: function(store, type, sinceToken) {
        // no query

        return new Ember.RSVP.Promise(function(resolve, reject) {

            var configItem = Utils.GetProperty(AppConfig.DataModelMapTable, type);
            if(configItem == null){
                throw "Can't found mapper for " + type;
            }
            var provider = new window[configItem.Provider];
            provider[configItem.Method](Utils.GenerateRequestModel(null), function(receive){
                if(receive.data.status == 200){
                    var rData = Transition.Resolve(type, "findAll", receive.data.data);
                    Ember.run(type, resolve, rData);
                }else{

                }
            });
        });
    },

    findQuery: function(store, type, query, modelArray) {
        var url = type.collectionUrl;
        jQuery.getJSON(url, query, function(data) {
            // data is expected to be an Array of Hashes, in an order
            // determined by the server. This order may be specified in
            // the query, and will be reflected in the view.
            //
            // If your server returns a root, simply do something like:
            // modelArray.load(data.people)
            modelArray.load(data);
        });
        return;

        var configItem = Utils.GetProperty(AppConfig.DataModelMapTable, type);
        if(configItem == null){
            throw "Can't found mapper for " + type;
        }
        var provider = new window[configItem.Provider];
        provider[configItem.Method](Utils.GenerateRequestModel(query), function(receive){
            if(receive.data.status == 200){
                var rData = Transition.Resolve(type, "findQuery", receive.data.data);
                modelArray.load(rData);
            }else{

            }
        });
    }
});

