
var ProductAdapter = DS.RESTAdapter.extend({

    find: function(store, type, id) {
        var url = type.url;

        return new Ember.RSVP.Promise(function(resolve) {

            var ds = {
                "Product": {
                    "id": "1",
                    "name": "3D Parking 1",
                    "price": 9.99,
                    "picture": "/images/P1.jpg",
                    "description": ""
                }
            };
            Ember.run(null, resolve, ds);

        });
    },
    findAll: function(store, type, sinceToken) {
        // Do your thing here
        var query;
        if (sinceToken) {
            query = { since: sinceToken };
        }
        //return this.ajax("/test/products", 'GET', { data: query });

        var result = {"Products": [
            {
                "id": 111,
                "name": "3D Parking 1",
                "price": 9.99,
                "picture": "/images/P1.jpg",
                "description": ""
            },
            {
                "id": 222,
                "name": "3D Parking 2",
                "price": 9.99,
                "picture": "/images/P1.jpg",
                "description": ""
            }
        ]};

        var productsPromise = new Ember.RSVP.Promise(function(resolve, reject) {
            Ember.run(Ember.App.Product, resolve, result);
            console.log("Push done");
        });

        return productsPromise;
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

