
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
                    var rData = Transition.Resolve(type, "find", JSON.parse(receive.data.data));
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
                    var rData = Transition.Resolve(type, "findAll", JSON.parse(receive.data.data));
                    Ember.run(type, resolve, rData);
                }else{

                }
            });
        });
    },

    findQuery: function(store, type, query, modelArray) {
        var url = type.collectionUrl;

        return new Ember.RSVP.Promise(function(resolve, reject) {

            var configItem = Utils.GetProperty(AppConfig.DataModelMapTable, type);
            if(configItem == null){
                throw "Can't found mapper for " + type;
            }
            var provider = new window[configItem.Provider];
            provider[configItem.Method](Utils.GenerateRequestModel(null), function(receive){
                if(receive.data.status == 200){
                    var rData = Transition.Resolve(type, "findQuery", JSON.parse(receive.data.data));
                    Ember.run(type, resolve, rData);
                }else{

                }
            });
        });
    },

    deleteRecord: function (store, type, record) {
        var id = record.get('id');

        return Ember.RSVP.resolve();
    }
});

