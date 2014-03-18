
App.ProductAdapter = DS.RESTAdapter.extend({

    find: function(store, type, id) {
        var url = type.url;
        url = url.fmt(id);

        jQuery.getJSON(url, function(data) {
            // data is a Hash of key/value pairs. If your server returns a
            // root, simply do something like:
            // store.load(type, id, data.person)
            store.load(type, id, data);
        });
    },
    findAll: function(store, type, sinceToken) {
        // Do your thing here
        var query;
        if (sinceToken) {
            query = { since: sinceToken };
        }
        var list = new Array();
        var product = {
            id: 123,
            name: "123",
            price: 10,
            picture: "",
            description: ""
        };
        list.push(product);

        //return list;

        return new Ember.RSVP.Promise(function(resolve, reject) {

            var ds = {"Products": [{"Product": {id:123, "name": "123", "price": 10, "picture": "", "description": ""}, id:123}]};

            resolve(ds);
            //Ember.run(null, resolve, product);
            /*
            jQuery.getJSON(url).then(function(data) {
                Ember.run(null, resolve, data);
            }, function(jqXHR) {
                jqXHR.then = null; // tame jQuery's ill mannered promises
                Ember.run(null, reject, jqXHR);
            });*/
        });
        //return this.ajax(this.buildURL(type.typeKey), 'GET', { data: query });
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
    }
});

App.store = DS.Store.create({
    adapter: 'CustomAdapter'
});
